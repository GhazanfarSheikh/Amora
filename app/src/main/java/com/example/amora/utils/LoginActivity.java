package com.example.amora.utils;

import android.util.Log;

import com.example.amora.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FirebaseHelper Class
 * Centralized class for all Firebase operations (Authentication & Firestore)
 * This makes it easy to manage all database operations in one place
 */
public class FireBaseHelper {

    // Firebase instances
    private static FirebaseAuth firebaseAuth;           // Handles authentication
    private static FirebaseFirestore firebaseFirestore; // Handles database operations

    // Collection names in Firestore
    private static final String USERS_COLLECTION = "users";  // Where user profiles are stored
    private static final String MATCHES_COLLECTION = "matches";  // Where matches are stored

    // Logging tag for debugging
    private static final String TAG = "FirebaseHelper";

    /**
     * Get Firebase Auth instance
     * Singleton pattern - creates only one instance
     */
    public static FirebaseAuth getAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    /**
     * Get Firestore instance
     * Singleton pattern - creates only one instance
     */
    public static FirebaseFirestore getFirestore() {
        if (firebaseFirestore == null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
        }
        return firebaseFirestore;
    }

    /**
     * Get current logged-in user
     * Returns null if no user is logged in
     */
    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    /**
     * Check if user is logged in
     */
    public static boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Get current user's ID
     */
    public static String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // ============================================
    // USER OPERATIONS
    // ============================================

    /**
     * Save user profile to Firestore
     * This creates or updates a user document in the "users" collection
     *
     * @param user The User object to save
     * @param callback Callback to handle success or failure
     */
    public static void saveUserProfile(User user, final FirebaseCallback callback) {
        // Convert User object to a Map (required by Firestore)
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", user.getUserId());
        userMap.put("email", user.getEmail());
        userMap.put("fullName", user.getFullName());
        userMap.put("bio", user.getBio());
        userMap.put("age", user.getAge());
        userMap.put("gender", user.getGender());
        userMap.put("latitude", user.getLatitude());
        userMap.put("longitude", user.getLongitude());
        userMap.put("location", user.getLocation());
        userMap.put("profileImageUrl", user.getProfileImageUrl());
        userMap.put("photoUrls", user.getPhotoUrls());
        userMap.put("interests", user.getInterests());
        userMap.put("isVerified", user.isVerified());
        userMap.put("createdAt", user.getCreatedAt());
        userMap.put("lastActive", System.currentTimeMillis());

        // Save to Firestore
        getFirestore().collection(USERS_COLLECTION)
                .document(user.getUserId())
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User profile saved successfully");
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving user profile", e);
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * Get user profile from Firestore
     *
     * @param userId The user ID to fetch
     * @param callback Callback with the User object or error
     */
    public static void getUserProfile(String userId, final UserCallback callback) {
        getFirestore().collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convert Firestore document to User object
                        User user = documentSnapshot.toObject(User.class);
                        Log.d(TAG, "User profile fetched successfully");
                        if (callback != null) {
                            callback.onUserFetched(user);
                        }
                    } else {
                        Log.d(TAG, "User profile not found");
                        if (callback != null) {
                            callback.onFailure("User not found");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user profile", e);
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * Get nearby users for discovery
     * This fetches all users except the current user
     * In production, you'd filter by location radius
     *
     * @param callback Callback with list of users
     */
    public static void getNearbyUsers(final UsersCallback callback) {
        String currentUserId = getCurrentUserId();

        getFirestore().collection(USERS_COLLECTION)
                .orderBy("lastActive", Query.Direction.DESCENDING)
                .limit(50)  // Limit to 50 users for better performance
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();

                    // Convert each document to User object
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        User user = document.toObject(User.class);

                        // Don't include the current user
                        if (user != null && !user.getUserId().equals(currentUserId)) {
                            users.add(user);
                        }
                    }

                    Log.d(TAG, "Fetched " + users.size() + " nearby users");
                    if (callback != null) {
                        callback.onUsersFetched(users);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching nearby users", e);
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * Update user's last active timestamp
     * Call this when user opens the app
     */
    public static void updateLastActive() {
        String userId = getCurrentUserId();
        if (userId != null) {
            getFirestore().collection(USERS_COLLECTION)
                    .document(userId)
                    .update("lastActive", System.currentTimeMillis())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Last active updated"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating last active", e));
        }
    }

    /**
     * Sign out the current user
     */
    public static void signOut() {
        getAuth().signOut();
        Log.d(TAG, "User signed out");
    }

    // ============================================
    // CALLBACK INTERFACES
    // These allow us to handle async Firebase operations
    // ============================================

    /**
     * Generic callback for success/failure operations
     */
    public interface FirebaseCallback {
        void onSuccess();
        void onFailure(String error);
    }

    /**
     * Callback for fetching a single user
     */
    public interface UserCallback {
        void onUserFetched(User user);
        void onFailure(String error);
    }

    /**
     * Callback for fetching multiple users
     */
    public interface UsersCallback {
        void onUsersFetched(List<User> users);
        void onFailure(String error);
    }
}