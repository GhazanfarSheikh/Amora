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
 * DiscoverGridAdapter - RecyclerView Adapter for Grid View
 *
 * This adapter powers the "Discover" screen's grid layout
 * Shows multiple user profiles in a grid format (like Instagram Explore)
 *
 * Features:
 * - Displays user profiles in a 2-column grid
 * - Shows compact profile info: photo, name, age
 * - Efficient image loading with Glide
 * - Click handling to view full profiles
 *
 * Used in: DiscoverFragment.java
 */
public class DiscoverGridAdapter extends RecyclerView.Adapter<DiscoverGridAdapter.GridViewHolder> {

    // Context for accessing resources and Glide
    private Context context;

    // List of users to display in grid
    private List<User> userList;

    // Interface for handling grid item clicks
    private OnItemClickListener listener;

    /**
     * Interface for item click callbacks
     * Implement this in your Fragment/Activity to handle clicks
     */
    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    /**
     * Constructor - Initialize adapter with data
     *
     * @param context - Application context
     * @param userList - List of User objects to display
     * @param listener - Click listener for grid items
     */
    public DiscoverGridAdapter(Context context, List<User> userList, OnItemClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    /**
     * onCreateViewHolder - Creates new ViewHolder instances
     * Called when RecyclerView needs a new grid item view
     *
     * @param parent - Parent ViewGroup
     * @param viewType - Type of view (not used here, single type)
     * @return New GridViewHolder instance
     */
    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the grid item layout XML
        View view = LayoutInflater.from(context).inflate(R.layout.item_discover_grid, parent, false);
        return new GridViewHolder(view);
    }

    /**
     * onBindViewHolder - Binds data to ViewHolder
     * Called when RecyclerView wants to display data at a position
     *
     * @param holder - ViewHolder to bind data to
     * @param position - Position in the list
     */
    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        // Get the user at this position
        User user = userList.get(position);

        // Set user name and age (e.g., "Sarah, 25")
        holder.nameAgeTextView.setText(user.getName() + ", " + user.getAge());

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

        // Show online status badge if user is online
        if (user.isOnline()) {
            holder.onlineBadge.setVisibility(View.VISIBLE);
        } else {
            holder.onlineBadge.setVisibility(View.GONE);
        }

        // Set click listener on the entire grid item
        holder.itemView.setOnClickListener(v -> {
            // Notify listener when item is clicked
            if (listener != null) {
                listener.onItemClick(user);
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
     * Call this when you want to show new users or filter results
     *
     * @param newUserList - New list of users to display
     */
    public void updateData(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();  // Tells RecyclerView to redraw all items
    }

    /**
     * removeItem - Remove a specific user from the grid
     * Useful when a user is matched or blocked
     *
     * @param position - Position of item to remove
     */
    public void removeItem(int position) {
        if (userList != null && position >= 0 && position < userList.size()) {
            userList.remove(position);
            notifyItemRemoved(position);  // Animate the removal
        }
    }

    /**
     * GridViewHolder - Holds references to grid item UI elements
     *
     * This is the ViewHolder pattern - it caches view references
     * so we don't have to call findViewById() repeatedly
     * This improves scrolling performance significantly
     */
    static class GridViewHolder extends RecyclerView.ViewHolder {

        // UI Components for each grid item
        ImageView profileImageView;  // User's profile photo
        TextView nameAgeTextView;    // Name and age
        View onlineBadge;           // Green dot for online status

        /**
         * Constructor - Find and cache all UI elements
         *
         * @param itemView - The grid item view
         */
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find and store references to all UI elements
            // These IDs come from item_discover_grid.xml
            profileImageView = itemView.findViewById(R.id.gridProfileImage);
            nameAgeTextView = itemView.findViewById(R.id.gridNameAge);
            onlineBadge = itemView.findViewById(R.id.onlineBadge);
        }
    }
}
