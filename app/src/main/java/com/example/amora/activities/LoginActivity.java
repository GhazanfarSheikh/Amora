package com.example.amora.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amora.R;
import com.example.amora.utils.FireBaseHelper;
import com.google.firebase.auth.FirebaseAuth;

/**
 * LoginActivity - User Login Screen
 *
 * Allows existing users to log in with email and password
 *
 * Flow:
 * OnboardingActivity → LoginActivity → MainActivity (after successful login)
 *
 * UI Elements:
 * - Email input field
 * - Password input field
 * - Login button
 * - "Don't have an account? Sign Up" link
 * - "Forgot Password?" link
 * - Loading progress bar
 */
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private EditText emailEditText;        // Email input field
    private EditText passwordEditText;     // Password input field
    private Button loginButton;            // Login button
    private TextView signupTextView;       // "Don't have an account?" link
    private TextView forgotPasswordTextView; // "Forgot Password?" link
    private ProgressBar progressBar;       // Loading indicator

    // Firebase
    private FirebaseAuth firebaseAuth;     // Firebase authentication instance

    /**
     * onCreate() - Called when activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        firebaseAuth = FireBaseHelper.getAuth();

        // Initialize UI components
        initializeViews();

        // Setup click listeners
        setupClickListeners();
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupTextView = findViewById(R.id.signupTextView);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        progressBar = findViewById(R.id.progressBar);

        // Hide progress bar initially
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Setup click listeners for all interactive elements
     */
    private void setupClickListeners() {
        // Login button click
        loginButton.setOnClickListener(v -> attemptLogin());

        // "Don't have an account? Sign Up" click
        signupTextView.setOnClickListener(v -> navigateToSignup());

        // "Forgot Password?" click
        forgotPasswordTextView.setOnClickListener(v -> handleForgotPassword());
    }

    /**
     * Attempt to log in the user
     * This validates inputs and calls Firebase authentication
     */
    private void attemptLogin() {
        // Get email and password from input fields
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(email, password)) {
            return; // Stop if validation fails
        }

        // Show loading indicator
        showLoading(true);

        // Disable login button to prevent multiple clicks
        loginButton.setEnabled(false);

        // Call Firebase to log in
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide loading indicator
                    showLoading(false);

                    // Re-enable login button
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Login successful
                        Toast.makeText(LoginActivity.this,
                                "Login successful!",
                                Toast.LENGTH_SHORT).show();

                        // Update last active timestamp
                        FireBaseHelper.updateLastActive();

                        // Navigate to main screen
                        navigateToMainActivity();
                    } else {
                        // Login failed
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed";

                        Toast.makeText(LoginActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Validate email and password inputs
     *
     * @param email User's email
     * @param password User's password
     * @return true if valid, false otherwise
     */
    private boolean validateInputs(String email, String password) {
        // Check if email is empty
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please enter your email");
            emailEditText.requestFocus();
            return false;
        }

        // Check if email is valid format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return false;
        }

        // Check if password is empty
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter your password");
            passwordEditText.requestFocus();
            return false;
        }

        // Check if password is at least 6 characters
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        return true; // All validations passed
    }

    /**
     * Show or hide loading indicator
     *
     * @param show true to show, false to hide
     */
    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Navigate to SignupActivity
     */
    private void navigateToSignup() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    /**
     * Navigate to MainActivity after successful login
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

        // Clear the back stack so user can't go back to login
        finishAffinity();
    }

    /**
     * Handle "Forgot Password?" click
     * Sends password reset email
     */
    private void handleForgotPassword() {
        String email = emailEditText.getText().toString().trim();

        // Check if email is entered
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this,
                    "Please enter your email first",
                    Toast.LENGTH_SHORT).show();
            emailEditText.requestFocus();
            return;
        }

        // Show loading
        showLoading(true);

        // Send password reset email
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    showLoading(false);

                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,
                                "Password reset email sent. Check your inbox.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Failed to send reset email. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}