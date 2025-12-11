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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amora.R;
import com.example.amora.activities.ProfileDetailsActivity;
import com.example.amora.models.User;
import com.example.amora.utils.FireBaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * DiscoverFragment - Grid View of All Users
 *
 * This fragment shows all available users in a grid layout
 * Similar to the Figma design with:
 * - Filter chips at top (age, verified, etc.)
 * - Grid of user cards (2 columns)
 * - Each card shows: photo, name, age, distance, match %
 *
 * Flow:
 * MainActivity → Discover tab → DiscoverFragment
 * Click on user → ProfileDetailsActivity
 */
public class DiscoverFragment extends Fragment {

    // UI Components
    private View rootView;
    private RecyclerView usersRecyclerView;   // Grid of users
    private View progressBar;                  // Loading indicator
    private View emptyStateView;               // Shown when no users

    // Data
    private List<User> userList;               // List of all users

    // Adapter for RecyclerView (we'll create this)
    // private DiscoverAdapter discoverAdapter;

    /**
     * onCreateView() - Creates the fragment's UI
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_discover, container, false);

        // Initialize data
        userList = new ArrayList<>();

        // Initialize UI
        initializeViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load users
        loadUsers();

        return rootView;
    }

    /**
     * Initialize UI components
     */
    private void initializeViews() {
        usersRecyclerView = rootView.findViewById(R.id.usersRecyclerView);
        progressBar = rootView.findViewById(R.id.progressBar);
        emptyStateView = rootView.findViewById(R.id.emptyStateView);
    }

    /**
     * Setup RecyclerView with grid layout
     */
    private void setupRecyclerView() {
        // Use GridLayoutManager for 2-column grid
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        usersRecyclerView.setLayoutManager(gridLayoutManager);

        // In real implementation:
        // discoverAdapter = new DiscoverAdapter(userList, this::onUserClick);
        // usersRecyclerView.setAdapter(discoverAdapter);
    }

    /**
     * Load users from Firebase
     */
    private void loadUsers() {
        // Show loading
        showLoading(true);

        // Fetch users from Firebase
        FireBaseHelper.getNearbyUsers(new FireBaseHelper.UsersCallback() {
            @Override
            public void onUsersFetched(List<User> users) {
                // Hide loading
                showLoading(false);

                if (users != null && !users.isEmpty()) {
                    // Update user list
                    userList.clear();
                    userList.addAll(users);

                    // Notify adapter
                    // discoverAdapter.notifyDataSetChanged();

                    // Show RecyclerView
                    usersRecyclerView.setVisibility(View.VISIBLE);
                    emptyStateView.setVisibility(View.GONE);

                    if (getContext() != null) {
                        Toast.makeText(getContext(),
                                "Found " + users.size() + " users",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // No users found
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(String error) {
                // Hide loading
                showLoading(false);

                // Show error
                if (getContext() != null) {
                    Toast.makeText(getContext(),
                            "Failed to load users: " + error,
                            Toast.LENGTH_SHORT).show();
                }

                showEmptyState();
            }
        });
    }

    /**
     * Show or hide loading indicator
     */
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (usersRecyclerView != null) {
            usersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Show empty state (no users available)
     */
    private void showEmptyState() {
        if (emptyStateView != null) {
            emptyStateView.setVisibility(View.VISIBLE);
        }

        if (usersRecyclerView != null) {
            usersRecyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * Handle user click - Open profile details
     */
    private void onUserClick(User user) {
        Intent intent = new Intent(getActivity(), ProfileDetailsActivity.class);
        intent.putExtra(ProfileDetailsActivity.EXTRA_USER, user);
        startActivity(intent);
    }

    /**
     * Apply filters (age, verified status, etc.)
     * Called when user clicks filter chips
     */
    private void applyFilters() {
        // In real implementation:
        // 1. Get selected filters
        // 2. Filter userList based on criteria
        // 3. Update RecyclerView

        if (getContext() != null) {
            Toast.makeText(getContext(),
                    "Filters applied!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}