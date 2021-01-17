package com.barlificent.ratify1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barlificent.ratify1.CustomClasses.User;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    String TAG = "Login";

    FirebaseAuth mAuth;
    TextInputEditText loginPhoneNumber;
    CardView nextButton1;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    Button resendButton;
    Button verifyButton;
    TextInputEditText codeSMS;
    DatabaseReference users;
    TextInputEditText name;
    LinearLayout nameLinear;
    UserPrefs userPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        users = FirebaseDatabase.getInstance().getReference("users");
        setUpToolbar();
        userPrefs = new UserPrefs(UserPrefs.USER_PREFS,this);

        loginPhoneNumber = (TextInputEditText) findViewById(R.id.phone_number_login);
        nextButton1 = (CardView) findViewById(R.id.next_button_1_login);
        resendButton = (Button) findViewById(R.id.resend_button_login);
        verifyButton = (Button) findViewById(R.id.verify_button_login);
        name = (TextInputEditText) findViewById(R.id.login_name_field);

        final ProgressDialog dialog = ProgressDialog.show(this,"","Verifying...");
        dialog.hide();
        dialog.setIndeterminate(true);

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                findViewById(R.id.linear_phone_login).setVisibility(View.GONE);
                findViewById(R.id.success_linear_login).setVisibility(View.VISIBLE);
                findViewById(R.id.linear_verifySMS_login).setVisibility(View.GONE);
                signInWithPhoneAuthCredential(credential);
                dialog.hide();
                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long l) {

                    }
                    @Override
                    public void onFinish() {
                        findViewById(R.id.success_linear_login).setVisibility(View.GONE);
                        findViewById(R.id.login_name_linear).setVisibility(View.VISIBLE);
                        findViewById(R.id.finish_button_login).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name =((TextInputEditText) findViewById(R.id.login_name_field)).getText().toString() ;
                                if(!name.equals("")){
                                    dialog.show();
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                                    String userId = mDatabase.push().getKey();
                                    User user = new User(name,FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),userId);
                                    mDatabase.child(userId).setValue(user);
                                    userPrefs.setName(user.getName());
                                    userPrefs.setId(user.getUserId());
                                    userPrefs.setPhoneNumber(user.getPhoneNumber());
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    dialog.hide();
                                }
                            }
                        });
                    }
                }.start();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                dialog.hide();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                findViewById(R.id.linear_verifySMS_login).setVisibility(View.VISIBLE);
                findViewById(R.id.linear_phone_login).setVisibility(View.GONE);
                dialog.hide();
                // ...
            }
        };

        nextButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phonenumber = loginPhoneNumber.getText().toString();
                if(!phonenumber.equals("")){
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber,
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            mCallbacks);          // OnVerificationStateChangedCallbacks
                }
                dialog.show();
            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phonenumber = loginPhoneNumber.getText().toString();
                if(!phonenumber.equals("")){
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber,
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            mCallbacks);          // OnVerificationStateChangedCallbacks
                }
            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = codeSMS.getText().toString();
                if(!code.equals("")) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void setUpToolbar() {
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbarTop);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/lyric_font.ttf"));
        setTitle("");
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}