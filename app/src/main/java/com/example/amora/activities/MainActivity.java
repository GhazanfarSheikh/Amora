package com.example.amora.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.amora.R;
import com.example.amora.fragments.DiscoverFragment;
import com.example.amora.fragments.ForYouFragment;
import com.example.amora.utils.FirebaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity - Main App Screen
 *
 * This is the main hub of the app after login
 * Contains bottom navigation with 4 tabs:
 * 1. For You - Card-based profile browsing (swipe interface)
 * 2. Discover - Grid view of all users
 * 3. Messages - Chat list (placeholder for now)
 * 4. Profile - User's own profile (placeholder for now)
 *
 * Flow:
 * LoginActivity/SignupActivity → MainActivity
 *
 * Uses Fragments for each tab to efficiently manage different screens
 */
public class MainActivity extends AppCompatActivity {

    // UI Components
    private BottomNavigationView bottomNavigationView;  // Bottom navigation bar

    // Currently displayed fragment
    private Fragment currentFragment;

    /**
     * onCreate() - Called when activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Update user's last active timestamp
        FireBaseHelper.updateLastActive();

        // Initialize UI
        initializeViews();

        // Setup bottom navigation
        setupBottomNavigation();

        // Load default fragment (For You)
        if (savedInstanceState == null) {
            loadFragment(new ForYouFragment());
        }
    }

    /**
     * Initialize UI components
     */
    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    /**
     * Setup bottom navigation bar
     * Handles tab switching between different screens
     */
    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;

                        // Get the ID of the clicked menu item
                        int itemId = item.getItemId();

                        // Determine which fragment to show based on clicked item
                        if (itemId == R.id.nav_for_you) {
                            // For You tab - Card swipe interface
                            selectedFragment = new ForYouFragment();

                        } else if (itemId == R.id.nav_discover) {
                            // Discover tab - Grid view of users
                            selectedFragment = new DiscoverFragment();

                        } else if (itemId == R.id.nav_messages) {
                            // Messages tab - Placeholder for now
                            Toast.makeText(MainActivity.this,
                                    "Messages coming soon!",
                                    Toast.LENGTH_SHORT).show();
                            return false; // Don't switch fragment

                        } else if (itemId == R.id.nav_profile) {
                            // Profile tab - User's own profile
                            // For now, just show a placeholder message
                            Toast.makeText(MainActivity.this,
                                    "Profile editing coming soon!",
                                    Toast.LENGTH_SHORT).show();
                            return false; // Don't switch fragment
                        }

                        // Load the selected fragment
                        if (selectedFragment != null) {
                            loadFragment(selectedFragment);
                            return true;
                        }

                        return false;
                    }
                });

        // Set default selected item (For You)
        bottomNavigationView.setSelectedItemId(R.id.nav_for_you);
    }

    /**
     * Load a fragment into the main container
     *
     * @param fragment The fragment to display
     */
    private void loadFragment(Fragment fragment) {
        // Only load if it's a different fragment
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return; // Already showing this fragment
        }

        // Replace the current fragment with the new one
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

        // Update current fragment reference
        currentFragment = fragment;
    }

    /**
     * onResume() - Called when activity comes to foreground
     * Update last active timestamp
     */
    @Override
    protected void onResume() {
        super.onResume();
        FireBaseHelper.updateLastActive();
    }

    /**
     * onBackPressed() - Handle back button press
     * Show exit confirmation or minimize app
     */
    @Override
    public void onBackPressed() {
        // If on For You tab, show exit confirmation
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_for_you) {
            // Move app to background instead of closing
            moveTaskToBack(true);
        } else {
            // Go back to For You tab
            bottomNavigationView.setSelectedItemId(R.id.nav_for_you);
        }
    }
}