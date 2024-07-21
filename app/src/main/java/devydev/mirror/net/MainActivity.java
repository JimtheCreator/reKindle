package devydev.mirror.net;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import front_page.HomeActivity;


public class MainActivity extends AppCompatActivity {

    public static final int SIGN_IN = 100;
    TextView login, register, forgotpass, disclaimer, display, txtype;
    View view1, view2;
    TextInputLayout emailbox1, pwdbox1;
    boolean Seen;

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    //    GoogleSignInClient googleSignInClient;
    RelativeLayout signInButton;
    SignInButton sign;
    ProgressDialog pd;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    RelativeLayout reglay, loglay, register_login;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        SharedPreferences pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        Seen = pref.getBoolean("Seen", false);

        if (user != null) {
            startActivity(new Intent(MainActivity.this, HabitSetActivity.class));
        }else {
            if (!Seen){
                startActivity(new Intent(MainActivity.this, WelcomeScreenActivity.class));
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SharedPreferences.Editor pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
//        pref.putString("State", "Fresh");
//        pref.apply();

        disclaimer = findViewById(R.id.disclaimer);
        register = findViewById(R.id.ttt);
        login = findViewById(R.id.txt);

        signInButton = findViewById(R.id.llsllslsl);

        emailbox1 = findViewById(R.id.layoutedit);
        pwdbox1 = findViewById(R.id.layoutedito);

        view1 = findViewById(R.id.view1);
        display = findViewById(R.id.display);
        txtype = findViewById(R.id.txtype);
        view2 = findViewById(R.id.view2);
        reglay = findViewById(R.id.lalallal);
        loglay = findViewById(R.id.blah);
        register_login = findViewById(R.id.register_login);
        forgotpass = findViewById(R.id.fwp);

        findViewById(R.id.register_login).setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), HabitSetActivity.class));
        });

        register.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dimmed_green));

        view2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.dimmed_green));

        forgotpass.setVisibility(View.GONE);
        display.setText("REGISTER");
        txtype.setText("Register");

        loglay.setOnClickListener(v -> {
            emailbox1.setError("");
            pwdbox1.setError("");
            display.setText("LOGIN");
            login.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dimmed_green));

            view1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.dimmed_green));

            forgotpass.setVisibility(View.VISIBLE);
            disclaimer.setVisibility(View.GONE);
            txtype.setText("Login");

            register.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.thickGrey));

            view2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.thickGrey));

        });

        reglay.setOnClickListener(v -> {
            emailbox1.setError("");
            pwdbox1.setError("");
            display.setText("REGISTER");
            login.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.thickGrey));

            view1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.thickGrey));

            forgotpass.setVisibility(View.GONE);

            disclaimer.setVisibility(View.VISIBLE);
            txtype.setText("Register");

            register.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dimmed_green));

            view2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.dimmed_green));

        });

        register_login.setOnClickListener(v -> {
            if (display.getText().equals("LOGIN")) {
                onClick();
                return;
            }

            if (display.getText().equals("REGISTER")) {
                Log.d("TEXT", ""+display.getText().toString());
                pd = new ProgressDialog(MainActivity.this);
                pd.show();

                pd.setCancelable(false);
                pd.setContentView(R.layout.progress_bar);

                pd.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                final String em = emailbox1.getEditText().getText().toString();
                final String pwd = pwdbox1.getEditText().getText().toString();

                if (em.isEmpty()) {
                    pd.dismiss();
                    emailbox1.setError("Email is required");
                    emailbox1.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                    pd.dismiss();
                    emailbox1.setError("Please enter a valid email!");

                    return;
                }

                if (pwd.isEmpty()) {
                    pd.dismiss();
                    pwdbox1.setError("Password is required");
                    return;
                }

                if (pwd.length() < 8) {
                    pd.dismiss();
                    pwdbox1.setError("Password is weak");

                    return;
                }

                if (pwd.contains(" ")) {
                    pd.dismiss();
                    pwdbox1.setError("Password should only contain abcABC123@?# and no other special symbols(eg., White Spaces)");
                    return;
                }

                register(em, pwd);
            }
        });

        //Configure sign -in to request the user 's ID, email address, and basic
        //profile.ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("443884029845-nk2dvqqg80deuqhrrblcle7jtsd693ab.apps.googleusercontent.com")
                .requestEmail()
                .build();

//        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId("443884029845-nk2dvqqg80deuqhrrblcle7jtsd693ab.apps.googleusercontent.com")
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(v -> {
            switch (v.getId()) {
                case R.id.llsllslsl:
                    googlecreation();
                    break;
                // ...
            }
        });


        String note = "By signing into reKindle, you agree to the Terms of Service and Privacy Policy";

        SpannableString spannableString = new SpannableString(note);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                gotoURL(getString(R.string.terms_of_service));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.dimmed_green));
                ds.setUnderlineText(true);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                String privacyurl = getString(R.string.privacy_policy);
                gotoURL(privacyurl);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.dimmed_green));
                ds.setUnderlineText(true);
            }
        };

        spannableString.setSpan(clickableSpan1, 43, 59, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2, 64, 78, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        disclaimer.setText(spannableString);
        disclaimer.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void register(final String email, final String password) {
        closeKeyboard();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, task -> {

                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String userID = firebaseUser.getUid();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("Userid", userID);
                        hashMap.put("Profilepic", "https://firebasestorage.googleapis.com/v0/b/purypal-caf27.appspot.com/o/pic_pic.png?alt=media&token=d23ed0ec-79d9-4289-a1c0-f1781b9715f5");
                        hashMap.put("Email", email);
                        hashMap.put("Name", "");
                        hashMap.put("Isgoogle", false);
                        hashMap.put("Iscollection", false);
                        hashMap.put("Ishabit", false);
                        hashMap.put("Ispaid", false);
                        hashMap.put("Iskillerswitch", false);


                        ref.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                pd.dismiss();
                                // Initialize SharedPreferences
                                SharedPreferences.Editor pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
                                pref.putString("State", "SignUp");
                                pref.apply();

                                Intent intent = new Intent(MainActivity.this, HabitSetActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        });
                    }
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setCancelable(true);
                    alert.setTitle("Error");
                    alert.setMessage("An unknown issue has occurred. Please try again later");
                    alert.setPositiveButton("Ok", (dialogInterface, i) -> {

                    });

                    alert.create().show();
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }


    private void onClick() {
        closeKeyboard();
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.show();

        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progress_bar);

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String email1 = emailbox1.getEditText().getText().toString();
        final String password1 = pwdbox1.getEditText().getText().toString();

        if (email1.isEmpty()) {
            progressDialog.dismiss();
            emailbox1.setError("Email address is required");
            return;
        }

        if (password1.isEmpty()) {
            progressDialog.dismiss();
            pwdbox1.setError("Password is required");
            return;
        }

        if (password1.length() < 8) {
            progressDialog.dismiss();
            pwdbox1.setError("Password must consist of at least 8 characters");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email1, password1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();

            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setCancelable(true);
            alert.setTitle("Error");
            alert.setMessage(e.getMessage());
            alert.setPositiveButton("Ok", (dialogInterface, i) -> {

            });

            alert.create().show();

        });

    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void gotoURL(String privacyurl) {
        Uri uri = Uri.parse(privacyurl);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void googlecreation() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInResultLauncher.launch(signInIntent);
        Log.d("CLICKED", "YES");
    }


//    private void googlecreation() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, SIGN_IN);
//        Log.d("CLICKED", "YES");
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account.getIdToken());
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                // ...
//            }
//        }
//    }

    private ActivityResultLauncher<Intent> signInResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    handleSignInResult(data);
                }
            });

    private void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            // ...
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.show();

        pd.setCancelable(false);
        pd.setContentView(R.layout.progress_bar);

        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            Log.d("CLICKED", "AN0THER LAUNCH");
            if (task.isSuccessful()) {
                Log.d("CLICKED", "AN0THER LAUNCH");
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success");
                FirebaseUser user = mAuth.getCurrentUser();
                // Check if the account exists in your Firebase database
                assert user != null;
                checkIfAccountExistsInDatabase(user.getEmail(), pd);
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            pd.dismiss();
            final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setCancelable(true);
            alert.setTitle("Unknown Error");
            alert.setMessage(e.getMessage());
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            alert.create().show();

        });
    }
    private void checkIfAccountExistsInDatabase(String email, ProgressDialog pr) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Perform a query to check if any user has the given email
        usersRef.orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    pr.dismiss();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();

                } else {
                    // User with the given email does not exist, proceed with sign-up
                    googleSignup(pr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors or onCancelled event
                Log.e(TAG, "Error checking if account exists in the database: " + databaseError.getMessage());
                Toast.makeText(MainActivity.this, "Error checking account existence.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void googleSignup(ProgressDialog ppp){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        String split = firebaseUser.getDisplayName();
        String[] parts = split.split("\\s+");

        String name = parts[0];

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Userid", firebaseUser.getUid());
        hashMap.put("Profilepic", "https://firebasestorage.googleapis.com/v0/b/purypal-caf27.appspot.com/o/pic_pic.png?alt=media&token=d23ed0ec-79d9-4289-a1c0-f1781b9715f5");
        hashMap.put("Email", firebaseUser.getEmail());
        hashMap.put("Name", name);
        hashMap.put("Isgoogle", true);
        hashMap.put("Iscollection", false);
        hashMap.put("Ishabit", false);
        hashMap.put("Ispaid", false);
        hashMap.put("Timemillis", "" + System.currentTimeMillis());
        hashMap.put("Iskillerswitch", false);

        reference.child(firebaseUser.getUid()).setValue(hashMap).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                ppp.dismiss();

                // Initialize SharedPreferences
                SharedPreferences.Editor pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
                pref.putString("State", "SignUp");
                pref.apply();

                Intent intent = new Intent(MainActivity.this, HabitSetActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

}