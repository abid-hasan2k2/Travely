package com.example.travely;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText v_loginemail, v_loginpassword;
    private MaterialButton v_login, v_createAcc;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupStatusBar();
        initializeViews();
        setupFirebaseAuth();
        setupButtonListeners();
    }

    private void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
    }

    private void initializeViews() {
        v_loginemail = findViewById(R.id.loginemail);
        v_loginpassword = findViewById(R.id.loginpassword);
        v_login = findViewById(R.id.login);
        v_createAcc = findViewById(R.id.createAcc);
    }

    private void setupFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            navigateToMainActivity();
        }
    }

    private void setupButtonListeners() {
        v_createAcc.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        v_login.setOnClickListener(view -> performLogin());
    }

    private void performLogin() {
        String mail = v_loginemail.getText().toString().trim();
        String pass = v_loginpassword.getText().toString().trim();

        if (!validateInputs(mail, pass)) {
            return;
        }

        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        checkEmailVerification();
                    } else {
                        handleLoginFailure(task.getException());
                    }
                });
    }

    private boolean validateInputs(String mail, String pass) {
        if (TextUtils.isEmpty(mail)) {
            v_loginemail.setError("Email is required");
            v_loginemail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            v_loginemail.setError("Invalid email address");
            v_loginemail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(pass)) {
            v_loginpassword.setError("Password is required");
            v_loginpassword.requestFocus();
            return false;
        }

        return true;
    }

    private void handleLoginFailure(Exception exception) {
        String errorMessage = "Authentication Failed";
        if (exception != null) {
            errorMessage += ": " + exception.getMessage();
        }
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            navigateToMainActivity();
        } else {
            Toast.makeText(LoginActivity.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}