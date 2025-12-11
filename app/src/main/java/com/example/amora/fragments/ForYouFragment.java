package com.example.amora.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.amora.R;
import com.example.amora.activities.ProfileDetailsActivity;
import com.example.amora.models.User;
import com.example.amora.utils.FireBaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * ForYouFragment - Card-based Profile Browsing
 *
 * This fragment shows the main "swipe" interface
 * Users see profile cards and can:
 * - Swipe right (or click heart) to like
 * - Swipe left to pass
 * - Tap card to view full profile
 *
 * Uses card swipe library for smooth animations
 */
public class ForYouFragment extends Fragment {

    // UI Components (will be initialized after layout is created)
    private View rootView;

    // Data
    private List<User> userList;              // List of users to show

    /**
     * onCreateView() - Creates the fragment's UI
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_for_you, container, false);

        // Initialize data
        userList = new ArrayList<>();

        // Initialize UI components
        initializeViews();

        // Load users from Firebase
        loadUsers();

        return rootView;
    }

    /**
     * Initialize UI components
     */
    private void initializeViews() {
        // We'll set up the card stack view here
        // For now, just show a placeholder

        // In the real implementation, we'll use CardStackView library
        // to create the swipeable card interface
    }

    /**
     * Load users from Firebase Firestore
     */
    private void loadUsers() {
        // Show loading state
        showLoading(true);

        // Fetch nearby users from Firebase
        FireBaseHelper.getNearbyUsers(new FireBaseHelper.UsersCallback() {
            @Override
            public void onUsersFetched(List<User> users) {
                // Hide loading
                showLoading(false);

                if (users != null && !users.isEmpty()) {
                    // Update user list
                    userList.clear();
                    userList.addAll(users);

                    // Update UI
                    displayUsers();
                } else {
                    // No users found
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(String error) {
                // Hide loading
                showLoading(false);

                // Show error message
                if (getContext() != null) {
                    Toast.makeText(getContext(),
                            "Failed to load users: " + error,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Display users in card stack
     */
    private void displayUsers() {
        // In real implementation:
        // 1. Create adapter for CardStackView
        // 2. Set adapter to CardStackView
        // 3. Setup swipe listeners

        // For now, just show a message
        if (getContext() != null && !userList.isEmpty()) {
            Toast.makeText(getContext(),
                    "Loaded " + userList.size() + " users",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show loading state
     */
    private void showLoading(boolean show) {
        // In real implementation, show/hide progress bar
        // For now, just a placeholder
    }

    /**
     * Show empty state when no users available
     */
    private void showEmptyState() {
        if (getContext() != null) {
            Toast.makeText(getContext(),
                    "No users nearby. Check back later!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle card click - Open full profile
     */
    private void openProfileDetails(User user) {
        Intent intent = new Intent(getActivity(), ProfileDetailsActivity.class);
        intent.putExtra(ProfileDetailsActivity.EXTRA_USER, user);
        startActivity(intent);
    }

    /**
     * Handle like action
     */
    private void handleLike(User user) {
        // In real app:
        // 1. Save like to Firestore
        // 2. Check if it's a mutual match
        // 3. Show match animation if mutual

        if (getContext() != null) {
            Toast.makeText(getContext(),
                    "Liked " + user.getFirstName() + "! 💚",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle pass action
     */
    private void handlePass(User user) {
        // In real app:
        // Save pass to Firestore to avoid showing this user again

        if (getContext() != null) {
            Toast.makeText(getContext(),
                    "Passed on " + user.getFirstName(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}