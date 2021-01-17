package com.barlificent.ratify1;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.barlificent.ratify1.CustomClasses.User;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.crashlytics.android.Crashlytics;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    /*

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
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        TextView mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
        setTitle("");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        }

        name = (TextInputEditText) findViewById(R.id.name);
        phoneNumber = (TextInputEditText) findViewById(R.id.phone_number);
        countriesSpinner = (Spinner) findViewById(R.id.spinner);
        male = (RadioButton) findViewById(R.id.male_radio);
        female = (RadioButton) findViewById(R.id.female_radio);

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this)
                            .setMessage("Not connected to the internet. Turn on mobile data or WiFi to login into Ratiefy.");
                    builder.create().show();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        userPrefs = new UserPrefs(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

// Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countryCodes, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        countriesSpinner.setAdapter(adapter);
        countriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prefix = (adapter.getItem(i) + "").substring(0, (adapter.getItem(i) + "").indexOf(","));
                Log.w("Prefix", prefix);
                countriesSpinner.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
    //todo    /////////////// onCreate() ends here/////////////////


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setMessage("Signing in...");
        progressDialog.setTitle("Google sign in");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
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
                            mDatabase.child(userId).setValue(mUser);
                            userPrefs.setName(nameString);
                            userPrefs.setId(mUser.getUserId());
                            userPrefs.setPhoneNumber(mUser != null ? mUser.getPhoneNumber() : "");
                            userPrefs.setLocale(CustomMethods.getLocale(getApplicationContext()));

                            if(male.isChecked()) {
                                mDatabase.child(userId).child("gender").setValue("male");
                                userPrefs.setGender("male");
                            } else {
                                mDatabase.child(userId).child("gender").setValue("female");
                                userPrefs.setGender("female");
                            }

                            startActivity(new Intent(SignInActivity.this, MainActivity.class));


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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }*/
}
