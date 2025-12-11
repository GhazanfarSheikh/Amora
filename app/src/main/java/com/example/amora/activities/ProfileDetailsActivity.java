package com.example.amora.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amora.R;
import com.example.amora.models.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

/**
 * ProfileDetailsActivity - Full Profile View
 *
 * Shows complete profile details of a user:
 * - Profile photo(s)
 * - Name, age, location, distance
 * - Match percentage
 * - Voice note (if available)
 * - Bio
 * - Interests
 * - Additional photos
 * - Like/Message buttons
 *
 * Flow:
 * ForYouFragment/DiscoverFragment → Click on profile → ProfileDetailsActivity
 */
public class ProfileDetailsActivity extends AppCompatActivity {

    // Intent extra key for passing user data
    public static final String EXTRA_USER = "extra_user";

    // UI Components
    private ImageButton backButton;           // Back button
    private ImageButton moreButton;           // More options button
    private ImageView profileImageView;       // Main profile photo
    private TextView nameAgeTextView;         // Name and age (e.g., "Jade Walker, 24")
    private TextView locationTextView;        // Location and distance
    private TextView matchPercentageTextView; // Match percentage badge
    private ImageButton playVoiceButton;      // Play voice note button
    private TextView voiceDurationTextView;   // Voice note duration
    private TextView bioTextView;             // User bio
    private ChipGroup interestsChipGroup;     // Interest tags
    private RecyclerView photosRecyclerView;  // Additional photos
    private ImageButton likeButton;           // Like button (heart)
    private ImageButton messageButton;        // Message button (chat)

    // Data
    private User user;                        // User being viewed
    private MediaPlayer mediaPlayer;          // For playing voice notes
    private boolean isPlaying = false;        // Voice note playing state

    /**
     * onCreate() - Called when activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        // Get user data from intent
        getUserFromIntent();

        // Initialize UI
        initializeViews();

        // Display user data
        displayUserData();

        // Setup click listeners
        setupClickListeners();
    }

    /**
     * Get user object passed from previous screen
     */
    private void getUserFromIntent() {
        if (getIntent().hasExtra(EXTRA_USER)) {
            // Get user object from intent
            // Note: User class must implement Serializable or Parcelable
            // For simplicity, we'll handle this in the layout implementation
            // For now, create a sample user
            user = new User();
            user.setFullName("Jade Walker");
            user.setAge(24);
            user.setLocation("M Bloc Space, South Jakarta");
            user.setDistanceKm(0.7);
            user.setMatchPercentage(94);
            user.setBio("Drawn to quiet corners of the world and vibrant city life alike, " +
                    "Jade finds beauty in the unexpected. She believes moments are best " +
                    "remembered when felt deeply — whether it's a sketch on a napkin or " +
                    "a song at midnight.");
            user.setVoiceNoteDuration(30); // 30 seconds
        }
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        moreButton = findViewById(R.id.moreButton);
        profileImageView = findViewById(R.id.profileImageView);
        nameAgeTextView = findViewById(R.id.nameAgeTextView);
        locationTextView = findViewById(R.id.locationTextView);
        matchPercentageTextView = findViewById(R.id.matchPercentageTextView);
        playVoiceButton = findViewById(R.id.playVoiceButton);
        voiceDurationTextView = findViewById(R.id.voiceDurationTextView);
        bioTextView = findViewById(R.id.bioTextView);
        interestsChipGroup = findViewById(R.id.interestsChipGroup);
        photosRecyclerView = findViewById(R.id.photosRecyclerView);
        likeButton = findViewById(R.id.likeButton);
        messageButton = findViewById(R.id.messageButton);
    }

    /**
     * Display user data in UI components
     */
    private void displayUserData() {
        if (user == null) return;

        // Display name and age
        String nameAge = user.getFullName() + ", " + user.getAge();
        nameAgeTextView.setText(nameAge);

        // Display location
        locationTextView.setText(user.getFormattedDistance());

        // Display match percentage
        String matchText = user.getMatchPercentage() + "%";
        matchPercentageTextView.setText(matchText);

        // Display bio
        bioTextView.setText(user.getBio());

        // Display voice note duration
        if (user.getVoiceNoteDuration() > 0) {
            voiceDurationTextView.setText("0:" + user.getVoiceNoteDuration());
            playVoiceButton.setVisibility(View.VISIBLE);
        } else {
            playVoiceButton.setVisibility(View.GONE);
            voiceDurationTextView.setVisibility(View.GONE);
        }

        // Load profile image
        if (user.getProfileImageUrl() != null) {
            Glide.with(this)
                    .load(user.getProfileImageUrl())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(profileImageView);
        }

        // Display interests
        displayInterests();
    }

    /**
     * Display user interests as chips
     */
    private void displayInterests() {
        // Sample interests (in real app, get from user.getInterests())
        String[] interests = {"Actress", "Modeling", "Art", "Traveling", "Music", "Painting"};

        interestsChipGroup.removeAllViews();

        for (String interest : interests) {
            Chip chip = new Chip(this);
            chip.setText(interest);
            chip.setCheckable(false);
            chip.setClickable(false);
            interestsChipGroup.addView(chip);
        }
    }

    /**
     * Setup click listeners for buttons
     */
    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // More options button
        moreButton.setOnClickListener(v -> {
            Toast.makeText(this, "More options coming soon", Toast.LENGTH_SHORT).show();
        });

        // Play voice note button
        playVoiceButton.setOnClickListener(v -> toggleVoiceNote());

        // Like button
        likeButton.setOnClickListener(v -> handleLike());

        // Message button
        messageButton.setOnClickListener(v -> handleMessage());
    }

    /**
     * Toggle voice note playback
     */
    private void toggleVoiceNote() {
        if (isPlaying) {
            // Stop playing
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                isPlaying = false;
                playVoiceButton.setImageResource(R.drawable.ic_play);
            }
        } else {
            // Start playing
            // In real app, load from user.getVoiceNoteUrl()
            Toast.makeText(this, "Playing voice note...", Toast.LENGTH_SHORT).show();
            isPlaying = true;
            playVoiceButton.setImageResource(R.drawable.ic_pause);
        }
    }

    /**
     * Handle like button click
     */
    private void handleLike() {
        Toast.makeText(this, "Liked! It's a match! 💚", Toast.LENGTH_SHORT).show();

        // In real app:
        // 1. Save match to Firestore
        // 2. Check if it's a mutual match
        // 3. Enable messaging if mutual match

        // Close this activity and return to main screen
        finish();
    }

    /**
     * Handle message button click
     */
    private void handleMessage() {
        Toast.makeText(this, "Message feature coming soon!", Toast.LENGTH_SHORT).show();

        // In real app:
        // Open chat screen with this user
    }

    /**
     * onDestroy() - Clean up resources
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release media player if it exists
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}