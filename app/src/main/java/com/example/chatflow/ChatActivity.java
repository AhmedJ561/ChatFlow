package com.example.chatflow;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatflow.adapter.ChatRecyclerAdapter;
import com.example.chatflow.model.ChatMessageModel;
import com.example.chatflow.model.ChatroomModel;
import com.example.chatflow.model.UserModel;
import com.example.chatflow.utils.AndroidUtil;
import com.example.chatflow.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity implements ChatRecyclerAdapter.DeleteMessageListener {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;

    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;
    String lastMsg="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get UserModel from Intent
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);

        // Set profile pic from Base64 string (if available)
        if (otherUser.getProfilePicBase64() != null) {
            AndroidUtil.setProfilePicFromBase64(this, otherUser.getProfilePicBase64(), imageView);
        }
        imageView.setOnClickListener(v -> showImagePopup());
        backBtn.setOnClickListener(v -> onBackPressed());
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty()) return;
            lastMsg=messageInput.getText().toString();
            sendMessageToUser(message);
        });

        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        adapter.setDeleteMessageListener(this); // Set the listener
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        // Scroll to the bottom when the adapter is updated
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.scrollToPosition(0); // Scroll to the bottom
            }
        });
    }

    void sendMessageToUser(String message) {
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        FirebaseUtil.currentUserDetails().get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String senderUsername = documentSnapshot.getString("username");
                String senderProfilePicBase64 = documentSnapshot.getString("profilePicBase64");

                String messageId = UUID.randomUUID().toString(); // Generate a unique message ID
                ChatMessageModel chatMessageModel = new ChatMessageModel(
                        messageId,
                        message,
                        FirebaseUtil.currentUserId(),
                        senderUsername,
                        senderProfilePicBase64,
                        Timestamp.now(),
                        false
                );
                FirebaseUtil.getChatroomMessageReference(chatroomId).document(messageId).set(chatMessageModel)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                messageInput.setText("");
                                recyclerView.scrollToPosition(0); // Scroll to the bottom
                            }
                        });
            }
        });
    }

    void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    // First time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    private void showImagePopup() {
        final Dialog imageDialog = new Dialog(this);
        imageDialog.setContentView(R.layout.dialog_image_popup);
        ImageView popupImageView = imageDialog.findViewById(R.id.popup_image_view);
        Drawable imageDrawable = imageView.getDrawable();

        if (imageDrawable != null) {
            Bitmap bitmap = drawableToBitmap(imageDrawable);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true);
            popupImageView.setImageBitmap(scaledBitmap);
        }

        imageDialog.show();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        int wallpaperResId = preferences.getInt("selected_wallpaper", -1);

        RelativeLayout rootLayout = findViewById(R.id.activity_chat_root);
        if (rootLayout != null) {
            if (wallpaperResId == -1) {
                rootLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
            } else {
                rootLayout.setBackgroundResource(wallpaperResId);
            }
        }
    }

    @Override
    public void onDeleteMessage(ChatMessageModel model) {
        // Forbidden emoji for deleted message
        String deletedMessageText = "This message was deleted! 🚫";

        // Update the message document to set the deleted status and modify the message text
        FirebaseUtil.getChatroomMessageReference(chatroomId).document(model.getMessageId())
                .update("deleted", true, "message", deletedMessageText) // Update deleted flag and message content
                .addOnSuccessListener(aVoid -> {
                    Log.d("DeleteMessage", "Message deleted successfully.");
                    model.setDeleted(true); // Update the local model to reflect deletion
                    model.setMessage(deletedMessageText); // Update message to deleted text

                    // Now check if this was the last message
                    FirebaseUtil.getChatroomReference(chatroomId).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String lastMessage = documentSnapshot.getString("lastMessage");
                                    Log.d("lastmessage",lastMessage+"->"+lastMsg);
                                    if (lastMessage != null && lastMessage.equals(lastMsg)) {
                                        // This was the last message, so update the chatroom's last message
                                        FirebaseUtil.getChatroomReference(chatroomId)
                                                .update("lastMessage", deletedMessageText)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    Log.d("DeleteMessage", "Last message updated to deleted text.");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("DeleteMessage", "Error updating last message: " + e.getMessage(), e);
                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("DeleteMessage", "Error retrieving chatroom reference: " + e.getMessage(), e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteMessage", "Error deleting message: " + e.getMessage(), e);
                });
    }
}
