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
import com.example.amora.models.User;
import com.example.amora.utils.FireBaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * SignupActivity - User Registration Screen
 *
 * Allows new users to create an account
 *
 * Flow:
 * LoginActivity → SignupActivity → MainActivity (after successful signup)
 *
 * UI Elements:
 * - Full Name input field
 * - Email input field
 * - Password input field
 * - Confirm Password input field
 * - Sign Up button
 * - "Already have an account? Login" link
 * - Loading progress bar
 */
public class SignupActivity extends AppCompatActivity {

    // UI Components
    private EditText fullNameEditText;         // Full name input
    private EditText emailEditText;            // Email input
    private EditText passwordEditText;         // Password input
    private EditText confirmPasswordEditText;  // Confirm password input
    private Button signupButton;               // Sign up button
    private TextView loginTextView;            // "Already have account?" link
    private ProgressBar progressBar;           // Loading indicator

    // Firebase
    private FirebaseAuth firebaseAuth;         // Firebase authentication

    /**
     * onCreate() - Called when activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        firebaseAuth = FireBaseHelper.getAuth();

        // Initialize UI
        initializeViews();

        // Setup click listeners
        setupClickListeners();
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        loginTextView = findViewById(R.id.loginTextView);
        progressBar = findViewById(R.id.progressBar);

        // Hide progress bar initially
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        // Sign up button click
        signupButton.setOnClickListener(v -> attemptSignup());

        // "Already have an account? Login" click
        loginTextView.setOnClickListener(v -> navigateToLogin());
    }

    /**
     * Attempt to create a new user account
     */
    private void attemptSignup() {
        // Get all input values
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate all inputs
        if (!validateInputs(fullName, email, password, confirmPassword)) {
            return; // Stop if validation fails
        }

        // Show loading
        showLoading(true);

        // Disable signup button
        signupButton.setEnabled(false);

        // Create user account in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Account created successfully
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            // Create user profile in Firestore
                            createUserProfile(firebaseUser.getUid(), fullName, email);
                        }
                    } else {
                        // Signup failed
                        showLoading(false);
                        signupButton.setEnabled(true);

                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Signup failed";

                        Toast.makeText(SignupActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Create user profile in Firestore database
     *
     * @param userId Firebase user ID
     * @param fullName User's full name
     * @param email User's email
     */
    private void createUserProfile(String userId, String fullName, String email) {
        // Create a new User object with basic information
        User user = new User();
        user.setUserId(userId);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setBio("Hey there! I'm using Amora 👋");  // Default bio
        user.setAge(0);  // User will update this later
        user.setLatitude(0.0);  // Default location
        user.setLongitude(0.0);
        user.setLocation("Unknown");  // User will update this later
        user.setCreatedAt(System.currentTimeMillis());
        user.setLastActive(System.currentTimeMillis());

        // Save user profile to Firestore
        FireBaseHelper.saveUserProfile(user, new FireBaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess() {
                // Profile saved successfully
                showLoading(false);

                Toast.makeText(SignupActivity.this,
                        "Account created successfully!",
                        Toast.LENGTH_SHORT).show();

                // Navigate to main screen
                navigateToMainActivity();
            }

            @Override
            public void onFailure(String error) {
                // Failed to save profile
                showLoading(false);
                signupButton.setEnabled(true);

                Toast.makeText(SignupActivity.this,
                        "Failed to create profile: " + error,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Validate all input fields
     *
     * @param fullName User's full name
     * @param email User's email
     * @param password User's password
     * @param confirmPassword Confirmed password
     * @return true if valid, false otherwise
     */
    private boolean validateInputs(String fullName, String email,
                                   String password, String confirmPassword) {
        // Validate full name
        if (TextUtils.isEmpty(fullName)) {
            fullNameEditText.setError("Please enter your full name");
            fullNameEditText.requestFocus();
            return false;
        }

        if (fullName.length() < 3) {
            fullNameEditText.setError("Name must be at least 3 characters");
            fullNameEditText.requestFocus();
            return false;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please enter your email");
            emailEditText.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter a password");
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Please confirm your password");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        return true; // All validations passed
    }

    /**
     * Show or hide loading indicator
     */
    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            signupButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            signupButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Navigate to LoginActivity
     */
    private void navigateToLogin() {
        // Go back to login screen
        finish(); // This closes signup and returns to login
    }

    /**
     * Navigate to MainActivity after successful signup
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);

        // Clear back stack
        finishAffinity();
    }
}