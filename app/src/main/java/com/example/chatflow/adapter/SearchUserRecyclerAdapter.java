package com.example.chatflow.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatflow.ChatActivity;
import com.example.chatflow.R;
import com.example.chatflow.model.UserModel;
import com.example.chatflow.utils.AndroidUtil;
import com.example.chatflow.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;
    TextView noUsersFoundText;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context, TextView noUsersFoundText) {
        super(options);
        this.context = context;
        this.noUsersFoundText = noUsersFoundText;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.usernameText.setText(model.getUsername());
        holder.emailText.setText(model.getEmail()); // Add email field
        if (model.getUserId().equals(FirebaseUtil.currentUserId())) {
            holder.usernameText.setText(model.getUsername() + " (Me)");
        }

        // Set the profile picture from Base64 string if available
        if (model.getProfilePicBase64() != null && !model.getProfilePicBase64().isEmpty()) {
            AndroidUtil.setProfilePicFromBase64(context, model.getProfilePicBase64(), holder.profilePic);
        }

        holder.itemView.setOnClickListener(v -> {
            // Navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView emailText; // Add email field
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            emailText = itemView.findViewById(R.id.email_text); // Add email field
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() == 0) {
            noUsersFoundText.setVisibility(View.VISIBLE);
        } else {
            noUsersFoundText.setVisibility(View.GONE);
        }
    }
}
