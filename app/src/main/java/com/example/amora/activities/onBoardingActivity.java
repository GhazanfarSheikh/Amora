package com.example.amora.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amora.R;

/**
 * OnboardingActivity - Welcome/Introduction Screen

 * This is the first screen new users see
 * Shows the app's main value proposition:
 * "Find Real Connections That Matter"

 * Flow:
 * SplashActivity → OnboardingActivity (if not logged in) → LoginActivity

 * UI Elements:
 * - Amora logo with neon green gradient
 * - Main tagline: "Find Real Connections That Matter"
 * - Description text
 * - "Get Started" button (neon green)
 */
public class OnboardingActivity extends AppCompatActivity {

    // UI Components
    private Button getStartedButton;  // The main "Get Started" button

    /**
     * onCreate() - Called when activity is created
     * Sets up the UI and button click listener
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for this activity
        // We'll create activity_onboarding.xml later
        setContentView(R.layout.activity_onboarding);

        // Initialize UI components
        initializeViews();

        // Set up button click listener
        setupClickListeners();
    }

    /**
     * Initialize all UI components
     * This connects Java code to XML layout elements
     */
    private void initializeViews() {
        // Find the "Get Started" button from the layout
        // R.id.getStartedButton refers to the button's ID in XML
        getStartedButton = findViewById(R.id.getStartedButton);
    }

    /**
     * Setup click listeners for buttons
     * This defines what happens when user clicks the button
     */
    private void setupClickListeners() {
        // When "Get Started" button is clicked
        getStartedButton.setOnClickListener(v -> {
            // Navigate to Login screen
            navigateToLogin();
        });
    }

    /**
     * Navigate to LoginActivity
     * This is called when user clicks "Get Started"
     */
    private void navigateToLogin() {
        // Create an Intent to start LoginActivity
        Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);

        // Start the new activity
        startActivity(intent);

        // Optional: Keep onboarding in the back stack
        // User can press back to return here
        // If you want to remove it, add: finish();
    }

    /**
     * onBackPressed() - Handle back button press
     * When user presses back on onboarding, exit the app
     */
    @Override
    public void onBackPressed() {
        // Exit the app instead of going back to splash
        super.onBackPressed(); // Call parent method first
        finishAffinity(); // Closes the app completely
    }