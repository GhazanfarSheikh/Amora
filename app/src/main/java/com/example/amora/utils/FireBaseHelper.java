package com.example.amora.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
 * FirebaseHelper
 * Single responsibility: centralize Firebase Auth + Firestore operations.

 * Why this class exists:
 * - Keeps Activities/Fragments clean.
 * - Encapsulates async callbacks.
 * - Reduces repeated boilerplate (collection names, mapping, logging).
 */
public final class FireBaseHelper {

    private static final String TAG = "FirebaseHelper";

    // Firestore collections
    public static final String USERS_COLLECTION = "users";

    // Singletons
    private static FirebaseAuth firebaseAuth;
    private static FirebaseFirestore firebaseFirestore;

    // Prevent instantiation
    private FireBaseHelper() {}

    // =========================
    // Instances / Session Info
    // =========================

    public static FirebaseAuth getAuth() {
        if (firebaseAuth == null) firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth;
    }

    public static FirebaseFirestore getFirestore() {
        if (firebaseFirestore == null) firebaseFirestore = FirebaseFirestore.getInstance();
        return firebaseFirestore;
    }

    @Nullable
    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    public static boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    @Nullable
    public static String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    // =========================
    // User Profile CRUD
    // =========================

    /**
     * Save user profile to Firestore (create or overwrite).
     * Uses a Map to ensure stable field names in Firestore.
     */
    public static void saveUserProfile(@NonNull User user, @Nullable final FirebaseCallback callback) {
        if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
            if (callback != null) callback.onFailure("Missing userId (Firebase UID).");
            return;
        }

        // Build Firestore-safe map (you control schema here)
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
        userMap.put("voiceNoteUrl", user.getVoiceNoteUrl());
        userMap.put("voiceNoteDuration", user.getVoiceNoteDuration());

        userMap.put("interests", user.getInterests());

        userMap.put("isVerified", user.isVerified());
        userMap.put("createdAt", user.getCreatedAt() == 0 ? System.currentTimeMillis() : user.getCreatedAt());
        userMap.put("lastActive", System.currentTimeMillis());

        getFirestore()
                .collection(USERS_COLLECTION)
                .document(user.getUserId())
                .set(userMap)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "User profile saved: " + user.getUserId());
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving user profile", e);
                    if (callback != null) callback.onFailure(e.getMessage());
                });
    }

    /**
     * Fetch single user profile by userId.
     */
    public static void getUserProfile(@NonNull String userId, @Nullable final UserCallback callback) {
        getFirestore()
                .collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Log.d(TAG, "User not found: " + userId);
                        if (callback != null) callback.onFailure("User not found");
                        return;
                    }

                    User user = documentSnapshot.toObject(User.class);

                    // Defensive: ensure userId is set even if mapping misses it
                    if (user != null && (user.getUserId() == null || user.getUserId().isEmpty())) {
                        user.setUserId(documentSnapshot.getId());
                    }

                    Log.d(TAG, "User fetched: " + userId);
                    if (callback != null) callback.onUserFetched(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user profile", e);
                    if (callback != null) callback.onFailure(e.getMessage());
                });
    }

    /**
     * Fetch a list of users for Discover/ForYou.
     * MVP logic: load recent users and exclude current user.
     *
     * Note: For real "nearby", you’ll later add geo queries (e.g., GeoFirestore) or a backend.
     */
    public static void getNearbyUsers(@Nullable final UsersCallback callback) {
        final String currentUserId = getCurrentUserId();

        getFirestore()
                .collection(USERS_COLLECTION)
                .orderBy("lastActive", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        User user = doc.toObject(User.class);

                        if (user == null) continue;

                        // Ensure userId is present
                        if (user.getUserId() == null || user.getUserId().isEmpty()) {
                            user.setUserId(doc.getId());
                        }

                        // Exclude self
                        if (currentUserId != null && currentUserId.equals(user.getUserId())) continue;

                        users.add(user);
                    }

                    Log.d(TAG, "Nearby users fetched: " + users.size());
                    if (callback != null) callback.onUsersFetched(users);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching nearby users", e);
                    if (callback != null) callback.onFailure(e.getMessage());
                });
    }

    /**
     * Update current user's lastActive timestamp.
     * Best called in MainActivity.onResume().
     */
    public static void updateLastActive() {
        String userId = getCurrentUserId();
        if (userId == null) return;

        getFirestore()
                .collection(USERS_COLLECTION)
                .document(userId)
                .update("lastActive", System.currentTimeMillis())
                .addOnSuccessListener(unused -> Log.d(TAG, "Last active updated"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating lastActive", e));
    }

    /**
     * Sign out the current user.
     */
    public static void signOut() {
        getAuth().signOut();
        Log.d(TAG, "Signed out");
    }

    // =========================
    // Callback Interfaces
    // =========================

    public interface FirebaseCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface UserCallback {
        void onUserFetched(@Nullable User user);
        void onFailure(String error);
    }

    public interface UsersCallback {
        void onUsersFetched(@NonNull List<User> users);
        void onFailure(String error);
    }
}
