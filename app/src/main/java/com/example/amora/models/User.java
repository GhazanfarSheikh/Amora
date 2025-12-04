package com.example.amora.models;

import java.util.ArrayList;
import java.util.List;

/**
 * User Model Class
 * Represents a user profile in the Amora dating app
 * This class stores all user information like name, bio, interests, photos, etc.
 * It's used to send/receive data from Firebase Firestore
 */
public class User {

    // User identification
    private String userId;           // Unique Firebase user ID
    private String email;            // User's email address
    private String fullName;         // User's full name

    // Profile information
    private String bio;              // User's bio/description
    private int age;                 // User's age
    private String gender;           // User's gender (Male/Female/Other)

    // Location data
    private double latitude;         // User's latitude coordinate
    private double longitude;        // User's longitude coordinate
    private String location;         // User's location name (e.g., "M Bloc Space, South Jakarta")

    // Profile media
    private String profileImageUrl;  // Main profile photo URL
    private List<String> photoUrls;  // Additional photos URLs
    private String voiceNoteUrl;     // Voice introduction URL (optional)
    private int voiceNoteDuration;   // Voice note duration in seconds

    // Interests and preferences
    private List<String> interests;  // List of user interests (e.g., "Modeling", "Hiking")

    // Match data
    private int matchPercentage;     // Compatibility percentage with current user
    private double distanceKm;       // Distance from current user in kilometers

    // Account status
    private boolean isVerified;      // Whether user has verified their account
    private long createdAt;          // Account creation timestamp
    private long lastActive;         // Last active timestamp

    /**
     * Default Constructor (Required for Firebase)
     * Firebase needs this empty constructor to deserialize data from Firestore
     */
    public User() {
        // Initialize lists to avoid null pointer exceptions
        this.photoUrls = new ArrayList<>();
        this.interests = new ArrayList<>();
    }

    /**
     * Full Constructor
     * Used when creating a new user with all details
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
        this.photoUrls = new ArrayList<>();
        this.interests = new ArrayList<>();
        this.isVerified = false;
        this.createdAt = System.currentTimeMillis();
        this.lastActive = System.currentTimeMillis();
    }

    // ============================================
    // GETTERS AND SETTERS
    // These methods allow reading and writing user data
    // ============================================

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public String getVoiceNoteUrl() {
        return voiceNoteUrl;
    }

    public void setVoiceNoteUrl(String voiceNoteUrl) {
        this.voiceNoteUrl = voiceNoteUrl;
    }

    public int getVoiceNoteDuration() {
        return voiceNoteDuration;
    }

    public void setVoiceNoteDuration(int voiceNoteDuration) {
        this.voiceNoteDuration = voiceNoteDuration;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastActive() {
        return lastActive;
    }

    public void setLastActive(long lastActive) {
        this.lastActive = lastActive;
    }

    /**
     * Helper method to get formatted distance
     * Returns distance as a readable string like "3.2 km away"
     */
    public String getFormattedDistance() {
        return String.format("%.1f km away", distanceKm);
    }

    /**
     * Helper method to check if user has multiple photos
     */
    public boolean hasMultiplePhotos() {
        return photoUrls != null && photoUrls.size() > 1;
    }

    /**
     * Helper method to get first name from full name
     */
    public String getFirstName() {
        if (fullName != null && fullName.contains(" ")) {
            return fullName.split(" ")[0];
        }
        return fullName;
    }
}