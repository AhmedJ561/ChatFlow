package com.example.chatflow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatflow.R;
import com.example.chatflow.model.ChatMessageModel;
import com.example.chatflow.utils.AndroidUtil;
import com.example.chatflow.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    DeleteMessageListener deleteMessageListener;

    public interface DeleteMessageListener {
        void onDeleteMessage(ChatMessageModel model);
    }

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    public void setDeleteMessageListener(DeleteMessageListener listener) {
        this.deleteMessageListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        String formattedTimestamp = formatTimestamp(model.getTimestamp());

        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            // Show right-hand chat layout
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
            holder.rightChatTimestamp.setText(formattedTimestamp);
            holder.userBl.setText(model.getSenderUsername());
            AndroidUtil.setProfilePicFromBase64(context, model.getSenderProfilePicBase64(), holder.imgBl);

            // Attach long-click listener for right-hand chat
            holder.rightChatLayout.setOnLongClickListener(v -> {
                showPopupMenu(v, model);
                return true;
            });
        } else {
            // Show left-hand chat layout
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
            holder.leftChatTimestamp.setText(formattedTimestamp);
            holder.userGr.setText(model.getSenderUsername());
            AndroidUtil.setProfilePicFromBase64(context, model.getSenderProfilePicBase64(), holder.imgGr);

            // Disable long-click listener for left-hand chat
            holder.leftChatLayout.setOnLongClickListener(null);
        }
    }


    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextview, rightChatTextview, leftChatTimestamp, rightChatTimestamp;
        TextView userGr, userBl;
        ImageView imgGr, imgBl;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatTimestamp = itemView.findViewById(R.id.left_chat_timestamp);
            rightChatTimestamp = itemView.findViewById(R.id.right_chat_timestamp);
            userGr = itemView.findViewById(R.id.user_gr);
            userBl = itemView.findViewById(R.id.user_bl);
            imgGr = itemView.findViewById(R.id.img_gr);
            imgBl = itemView.findViewById(R.id.img_bl);
        }
    }

    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }

    private void showPopupMenu(View view, ChatMessageModel model) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.chat_message_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                deleteMessageListener.onDeleteMessage(model);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }
}
