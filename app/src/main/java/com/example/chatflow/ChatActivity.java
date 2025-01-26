package com.example.chatflow;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
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

    private UserModel otherUser;
    private String chatroomId, lastMsg = "";
    private ChatroomModel chatroomModel;
    private ChatRecyclerAdapter adapter;
    private EditText messageInput;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeViews();
        setupChatData();
        setupChatRecyclerView();
    }

    private void initializeViews() {
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        ImageButton sendMessageBtn = findViewById(R.id.message_send_btn);
        ImageButton backBtn = findViewById(R.id.back_btn);
        TextView otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        ImageView imageView = findViewById(R.id.profile_pic_image_view);

        if (otherUser.getProfilePicBase64() != null) {
            AndroidUtil.setProfilePicFromBase64(this, otherUser.getProfilePicBase64(), imageView);
        }

        backBtn.setOnClickListener(v -> onBackPressed());
        imageView.setOnClickListener(v -> showImagePopup(imageView));
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) sendMessageToUser(message);
        });
    }

    private void setupChatData() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnSuccessListener(snapshot -> {
            chatroomModel = snapshot.toObject(ChatroomModel.class);
            if (chatroomModel == null) {
                chatroomModel = new ChatroomModel(
                        chatroomId,
                        Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                        Timestamp.now(),
                        ""
                );
                FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
            }
        });
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, this);
        adapter.setDeleteMessageListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(0);
            }
        });
    }

    private void sendMessageToUser(String message) {
        lastMsg = message;
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        FirebaseUtil.currentUserDetails().get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String senderUsername = snapshot.getString("username");
                String senderProfilePicBase64 = snapshot.getString("profilePicBase64");
                String messageId = UUID.randomUUID().toString();

                ChatMessageModel chatMessage = new ChatMessageModel(
                        messageId,
                        message,
                        FirebaseUtil.currentUserId(),
                        senderUsername,
                        senderProfilePicBase64,
                        Timestamp.now(),
                        false
                );
                FirebaseUtil.getChatroomMessageReference(chatroomId).document(messageId).set(chatMessage)
                        .addOnCompleteListener(task -> messageInput.setText(""));
            }
        });
    }

    private void showImagePopup(ImageView imageView) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image_popup);

        ImageView popupImage = dialog.findViewById(R.id.popup_image_view);
        Drawable drawable = imageView.getDrawable();

        if (drawable != null) {
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );
            new Canvas(bitmap).drawBitmap(((BitmapDrawable) drawable).getBitmap(), 0, 0, null);
            popupImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 800, 800, true));
        }

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBackgroundWallpaper();
    }

    private void setBackgroundWallpaper() {
        RelativeLayout rootLayout = findViewById(R.id.activity_chat_root);
        int wallpaperResId = getSharedPreferences("AppSettings", MODE_PRIVATE)
                .getInt("selected_wallpaper", -1);

        if (wallpaperResId == -1) {
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.windowBackground, typedValue, true);
            rootLayout.setBackgroundResource(typedValue.type == TypedValue.TYPE_REFERENCE
                    ? typedValue.resourceId
                    : typedValue.data);
        } else {
            rootLayout.setBackgroundResource(wallpaperResId);
        }
    }

    @Override
    public void onDeleteMessage(ChatMessageModel model) {
        String deletedMessageText = "This message was deleted! 🚫";
        FirebaseUtil.getChatroomMessageReference(chatroomId)
                .document(model.getMessageId())
                .update("deleted", true, "message", deletedMessageText)
                .addOnSuccessListener(aVoid -> updateLastMessageIfDeleted(model, deletedMessageText));
    }

    private void updateLastMessageIfDeleted(ChatMessageModel model, String deletedMessageText) {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnSuccessListener(snapshot -> {
            String lastMessage = snapshot.getString("lastMessage");
            if (lastMessage != null && lastMessage.equals(lastMsg)) {
                FirebaseUtil.getChatroomReference(chatroomId).update("lastMessage", deletedMessageText);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
