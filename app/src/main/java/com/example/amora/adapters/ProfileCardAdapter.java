
package com.example.amora.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amora.R;
import com.example.amora.models.User;

import java.util.List;

/**
 * ProfileCardAdapter - RecyclerView Adapter for Card Swipe Interface
 *
 * This adapter powers the "For You" screen's card swipe feature
 * Similar to Tinder's card stack - shows user profiles one by one
 *
 * Features:
 * - Displays user profile cards with photo, name, age, bio
 * - Shows match percentage and distance
 * - Loads images efficiently using Glide library
 * - Handles click events on cards
 *
 * Used in: ForYouFragment.java
 */
public class ProfileCardAdapter extends RecyclerView.Adapter<ProfileCardAdapter.CardViewHolder> {

    // Context for accessing resources and Glide
    private Context context;

    // List of users to display in cards
    private List<User> userList;

    // Interface for handling card click events
    private OnCardClickListener listener;

    /**
     * Interface for card click callbacks
     * Implement this in your Fragment/Activity to handle card clicks
     */
    public interface OnCardClickListener {
        void onCardClick(User user);
    }

    /**
     * Constructor - Initialize adapter with data
     *
     * @param context - Application context
     * @param userList - List of User objects to display
     * @param listener - Click listener for card interactions
     */
    public ProfileCardAdapter(Context context, List<User> userList, OnCardClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    /**
     * onCreateViewHolder - Creates new ViewHolder instances
     * Called when RecyclerView needs a new card view
     *
     * @param parent - Parent ViewGroup
     * @param viewType - Type of view (not used here, single type)
     * @return New CardViewHolder instance
     */
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the card layout XML
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_card, parent, false);
        return new CardViewHolder(view);
    }

    /**
     * onBindViewHolder - Binds data to ViewHolder
     * Called when RecyclerView wants to display data at a position
     *
     * @param holder - ViewHolder to bind data to
     * @param position - Position in the list
     */
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        // Get the user at this position
        User user = userList.get(position);

        // Set user name and age (e.g., "Sarah, 25")
        holder.nameTextView.setText(user.getName() + ", " + user.getAge());

        // Set user bio/tagline
        holder.bioTextView.setText(user.getBio());

        // Set distance (e.g., "3.2 km away")
        holder.distanceTextView.setText(user.getFormattedDistance());

        // Set match percentage (e.g., "94% Match")
        holder.matchTextView.setText(user.getMatchPercentage() + "% Match");

        // Load profile image using Glide
        // Glide handles image loading, caching, and memory management
        if (user.getPhotos() != null && !user.getPhotos().isEmpty()) {
            // Load the first photo from user's photo list
            Glide.with(context)
                    .load(user.getPhotos().get(0))  // First photo URL
                    .placeholder(R.drawable.ic_placeholder_user)  // Show while loading
                    .error(R.drawable.ic_placeholder_user)  // Show if load fails
                    .centerCrop()  // Scale image to fill ImageView
                    .into(holder.profileImageView);
        } else {
            // No photos available - show placeholder
            holder.profileImageView.setImageResource(R.drawable.ic_placeholder_user);
        }

        // Set click listener on the entire card
        holder.itemView.setOnClickListener(v -> {
            // Notify listener when card is clicked
            if (listener != null) {
                listener.onCardClick(user);
            }
        });
    }

    /**
     * getItemCount - Returns total number of items
     *
     * @return Size of user list
     */
    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    /**
     * updateData - Update the adapter's data and refresh UI
     * Call this when you want to show new users
     *
     * @param newUserList - New list of users to display
     */
    public void updateData(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();  // Tells RecyclerView to redraw all items
    }

    /**
     * CardViewHolder - Holds references to card UI elements
     *
     * This is the ViewHolder pattern - it caches view references
     * so we don't have to call findViewById() repeatedly
     * This improves scrolling performance significantly
     */
    static class CardViewHolder extends RecyclerView.ViewHolder {

        // UI Components for each card
        ImageView profileImageView;  // User's profile photo
        TextView nameTextView;       // Name and age
        TextView bioTextView;        // Bio/tagline
        TextView distanceTextView;   // Distance away
        TextView matchTextView;      // Match percentage

        /**
         * Constructor - Find and cache all UI elements
         *
         * @param itemView - The card view
         */
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find and store references to all UI elements
            // These IDs come from item_profile_card.xml
            profileImageView = itemView.findViewById(R.id.profileImage);
            nameTextView = itemView.findViewById(R.id.nameText);
            bioTextView = itemView.findViewById(R.id.bioText);
            distanceTextView = itemView.findViewById(R.id.distanceText);
            matchTextView = itemView.findViewById(R.id.matchText);
        }
    }
}