package com.example.amora.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amora.R;
import com.example.amora.utils.FireBaseHelper;

/**
 * SplashActivity - First screen shown when app launches
 *
 * This screen:
 * 1. Shows the Amora logo/branding for 2 seconds
 * 2. Checks if user is already logged in
 * 3. Redirects to MainActivity (if logged in) or OnboardingActivity (if not logged in)
 *
 * Flow:
 * App Launch → SplashActivity (2 sec) → Check Login → MainActivity OR OnboardingActivity
 */
public class SplashActivity extends AppCompatActivity {

    // Duration to show splash screen (in milliseconds)
    private static final int SPLASH_DURATION = 2000; // 2 seconds

    /**
     * onCreate() - Called when activity is first created
     * This is where we set up the splash screen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for this activity
        // We'll create activity_splash.xml layout file later
        setContentView(R.layout.activity_splash);

        // Use Handler to delay execution by SPLASH_DURATION milliseconds
        // This creates the "splash screen" effect
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // After 2 seconds, check if user is logged in
                checkUserLoginStatus();
            }
        }, SPLASH_DURATION);
    }

    /**
     * Check if user is already logged in
     * If logged in → Go to MainActivity
     * If not logged in → Go to OnboardingActivity
     */
    private void checkUserLoginStatus() {
        // Use FirebaseHelper to check login status
        if (FireBaseHelper.isUserLoggedIn()) {
            // User is already logged in, go directly to main screen
            navigateToMainActivity();
        } else {
            // User is not logged in, show onboarding/welcome screen
            navigateToOnboardingActivity();
        }
    }

    /**
     * Navigate to MainActivity (Home screen)
     * This is called when user is already logged in
     */
    private void navigateToMainActivity() {
        // Create an Intent to start MainActivity
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

        // Start the new activity
        startActivity(intent);

        // Finish this activity so user can't go back to splash screen
        finish();
    }

    /**
     * Navigate to OnboardingActivity (Welcome screen)
     * This is called when user is NOT logged in
     */
    private void navigateToOnboardingActivity() {
        // Create an Intent to start OnboardingActivity
        Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);

        // Start the new activity
        startActivity(intent);

        // Finish this activity so user can't go back to splash screen
        finish();
    }

    /**
     * onBackPressed() - Prevents user from going back from splash screen
     * We override this to do nothing, so back button doesn't work on splash screen
     */
    @Override
    public void onBackPressed() {
        // Do nothing - user cannot go back from splash screen
        // This prevents accidentally exiting the app during splash
    }
}