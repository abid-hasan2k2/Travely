package com.example.travely;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText v_signinemail, v_signinpassword, v_signinConfirmPassword, v_fName, v_lName, v_phoneNumber;
    private MaterialButton v_signup, v_gotologin;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupStatusBar();
        initializeViews();
        setupFirebaseAuth();
        setupButtonListeners();
    }

    private void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
    }

    private void initializeViews() {
        v_fName = findViewById(R.id.fName);
        v_lName = findViewById(R.id.lName);
        v_signinemail = findViewById(R.id.signinemail);
        v_phoneNumber = findViewById(R.id.phoneNumber);
        v_signinpassword = findViewById(R.id.signinpassword);
        v_signinConfirmPassword = findViewById(R.id.confirmPassword);
        v_signup = findViewById(R.id.sign_up_btn);
        v_gotologin = findViewById(R.id.gotologin);
    }

    private void setupFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
    }

    private void setupButtonListeners() {
        v_gotologin.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        v_signup.setOnClickListener(view -> {
            if (validateInputs()) {
                registerUser();
            }
        });
    }

    private boolean validateInputs() {
        String fName = v_fName.getText().toString().trim();
        String lName = v_lName.getText().toString().trim();
        String mail = v_signinemail.getText().toString().trim();
        String phone = v_phoneNumber.getText().toString().trim();
        String pass = v_signinpassword.getText().toString().trim();
        String confirmPass = v_signinConfirmPassword.getText().toString().trim();

        // Check for empty fields
        if (TextUtils.isEmpty(fName)) {
            v_fName.setError("First name is required");
            v_fName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(lName)) {
            v_lName.setError("Last name is required");
            v_lName.requestFocus();
            return false;
        }

        // Validate email
        if (TextUtils.isEmpty(mail)) {
            v_signinemail.setError("Email is required");
            v_signinemail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            v_signinemail.setError("Invalid email address");
            v_signinemail.requestFocus();
            return false;
        }

        // Validate phone number
        if (TextUtils.isEmpty(phone)) {
            v_phoneNumber.setError("Phone number is required");
            v_phoneNumber.requestFocus();
            return false;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            v_phoneNumber.setError("Invalid phone number");
            v_phoneNumber.requestFocus();
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(pass)) {
            v_signinpassword.setError("Password is required");
            v_signinpassword.requestFocus();
            return false;
        }

        if (pass.length() < 8) {
            v_signinpassword.setError("Password must be at least 8 characters");
            v_signinpassword.requestFocus();
            return false;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPass)) {
            v_signinConfirmPassword.setError("Confirm password is required");
            v_signinConfirmPassword.requestFocus();
            return false;
        }

        if (!pass.equals(confirmPass)) {
            v_signinConfirmPassword.setError("Passwords do not match");
            v_signinConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser() {
        String mail = v_signinemail.getText().toString().trim();
        String pass = v_signinpassword.getText().toString().trim();

        // Show loader
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, task -> {
                    // Hide loader
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Registration Complete", Toast.LENGTH_SHORT).show();
                        sendEmailVerification();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to Register: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Verification Email Sent, Verify and Log in Again",
                                    Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            finish();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Failed to Send Verification Email",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Failed to Verify", Toast.LENGTH_SHORT).show();
        }
    }
}