package com.barlificent.ratify1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.barlificent.ratify1.CustomClasses.User;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;

    private UserPrefs userPrefs;

    private TextInputEditText name;
    private TextInputEditText phoneNumber;
    private Spinner countriesSpinner;
    RadioButton male;
    RadioButton female;
    String prefix = "+01";

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userPrefs = new UserPrefs(getApplicationContext());

        name = (TextInputEditText) findViewById(R.id.name);
        phoneNumber = (TextInputEditText) findViewById(R.id.phone_number);
        countriesSpinner = (Spinner) findViewById(R.id.spinner);
        male = (RadioButton) findViewById(R.id.male_radio);
        female = (RadioButton) findViewById(R.id.female_radio);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        }

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CustomMethods.deviceIsConnected(getApplicationContext())) {
                    final String nameString = name.getText().toString();
                    final String phoneNumberString = phoneNumber.getText().toString();

                    if (!nameString.equals("") && !phoneNumberString.equals("") &&(male.isChecked()||female.isChecked()))
                        signIn();
                    else Toast.makeText(getApplicationContext(),"Please enter all the required values.",Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this)
                            .setMessage("Not connected to the internet. Turn on mobile data or WiFi to login into Ratiefy.");
                    builder.create().show();
                }
            }
        });
        configureSignIn();
        mAuth = FirebaseAuth.getInstance();
    }

    // This IS the method where the result of clicking the signIn button will be handled
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                Log.w("signIn",account.getDisplayName() + "\n" + account.getId());
                String idToken = account.getIdToken();
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential);
            } else {
// Google Sign In failed, update UI appropriately
                Log.e(TAG, "Login Unsuccessful. ");
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    private void firebaseAuthWithGoogle(AuthCredential acct) {

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Signing in...");
        progressDialog.setTitle("Google sign in");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = acct/*GoogleAuthProvider.getCredential(acct.getIdToken(), null)*/;
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                            String userId = mDatabase.push().getKey();

                            String nameString = name.getText().toString();
                            String phoneNumberString = phoneNumber.getText().toString();

                            User mUser = new User(nameString, phoneNumberString, userId);
                            mUser.setLocale(CustomMethods.getLocale(getApplicationContext()));
                            mUser.setCanBeSentRequest(true);
                            mUser.setGender(male.isChecked() ? "male" : "female");
                            mDatabase.child(userId).setValue(mUser);
                            userPrefs.setName(nameString);
                            userPrefs.setId(mUser.getUserId());
                            userPrefs.setPhoneNumber(mUser != null ? mUser.getPhoneNumber() : "");
                            userPrefs.setLocale(CustomMethods.getLocale(getApplicationContext()));
                            userPrefs.setGender(male.isChecked() ? "male" : "female");
//                            if(male.isChecked()) {
//                                mDatabase.child(userId).child("gender").setValue("male");
//                                userPrefs.setGender("male");
//                            } else {
//                                mDatabase.child(userId).child("gender").setValue("female");
//                                userPrefs.setGender("female");
//                            }

                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        progressDialog.hide();
                        progressDialog.dismiss();
                        // [END_EXCLUDE]
                    }
                });
    }

    public void configureSignIn(){
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
