package com.example.amora.models;

import java.util.ArrayList;
import java.util.List;

/**
 * User Model Class
 * Represents a user profile in the Amora dating app.
 *
 * Firestore notes:
 * - Must have a public no-arg constructor.
 * - Fields must have public getters/setters for automatic mapping (toObject(User.class)).
 */
public class User {

    // =========================
    // Identity
    // =========================
    private String userId;           // Firebase UID
    private String email;            // Email used for Auth
    private String fullName;         // Display name

    // =========================
    // Profile
    // =========================
    private String bio;              // Short bio
    private int age;                 // Age
    private String gender;           // "Male/Female/Other" (or any string)

    // =========================
    // Location
    // =========================
    private double latitude;         // Lat
    private double longitude;        // Lng
    private String location;         // Human-readable location

    // =========================
    // Media
    // =========================
    private String profileImageUrl;  // Main photo
    private List<String> photoUrls;  // Additional photos
    private String voiceNoteUrl;     // Optional
    private int voiceNoteDuration;   // Seconds

    // =========================
    // Interests
    // =========================
    private List<String> interests;  // e.g., ["Hiking", "Coffee"]

    // =========================
    // Match/Discovery (derived, not necessarily persisted)
    // =========================
    private int matchPercentage;     // UI convenience
    private double distanceKm;       // UI convenience

    // =========================
    // Status
    // =========================
    private boolean isVerified;
    private long createdAt;
    private long lastActive;

    /**
     * Required empty constructor for Firestore deserialization.
     * Initialize lists to avoid null pointer issues in adapters/UI.
     */
    public User() {
        this.photoUrls = new ArrayList<>();
        this.interests = new ArrayList<>();
    }

    /**
     * Convenience constructor for creating a new user locally.
     * You can expand this as your signup flow captures more fields.
     */
    public User(String userId, String email, String fullName, String bio, int age,
                String gender, double latitude, double longitude, String location) {

        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.bio = bio;
        this.age = age;
        this.gender = gender;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;

        // Defaults
        this.photoUrls = new ArrayList<>();
        this.interests = new ArrayList<>();
        this.isVerified = false;
        this.createdAt = System.currentTimeMillis();
        this.lastActive = System.currentTimeMillis();
    }

    // =========================
    // Getters & Setters
    // =========================

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) {
        // Null-safe: never store null lists (prevents adapter crashes).
        this.photoUrls = (photoUrls == null) ? new ArrayList<>() : photoUrls;
    }

    public String getVoiceNoteUrl() { return voiceNoteUrl; }
    public void setVoiceNoteUrl(String voiceNoteUrl) { this.voiceNoteUrl = voiceNoteUrl; }

    public int getVoiceNoteDuration() { return voiceNoteDuration; }
    public void setVoiceNoteDuration(int voiceNoteDuration) { this.voiceNoteDuration = voiceNoteDuration; }

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) {
        this.interests = (interests == null) ? new ArrayList<>() : interests;
    }

    public int getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(int matchPercentage) { this.matchPercentage = matchPercentage; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getLastActive() { return lastActive; }
    public void setLastActive(long lastActive) { this.lastActive = lastActive; }

    // =========================
    // UI Helper Methods
    // =========================

    /** Returns distance like "3.2 km away" */
    public String getFormattedDistance() {
        return String.format("%.1f km away", distanceKm);
    }

    /** True if user has 2+ photos */
    public boolean hasMultiplePhotos() {
        return photoUrls != null && photoUrls.size() > 1;
    }

    /** Gets first token of fullName (best-effort) */
    public String getFirstName() {
        if (fullName == null) return "";
        String trimmed = fullName.trim();
        if (trimmed.contains(" ")) return trimmed.split("\\s+")[0];
        return trimmed;
    }
}
