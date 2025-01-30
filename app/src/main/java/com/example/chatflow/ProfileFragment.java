package com.example.chatflow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatflow.model.UserModel;
import com.example.chatflow.utils.AndroidUtil;
import com.example.chatflow.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText emailInput;
    Button updateProfileBtn;
    ProgressBar progressBar;

    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public ProfileFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            if (getContext() != null && isAdded()) {
                                AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic);
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        emailInput = view.findViewById(R.id.profile_email);
        updateProfileBtn = view.findViewById(R.id.profle_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);

        getUserData();

        updateProfileBtn.setOnClickListener(v -> updateBtnClick());

        profilePic.setOnClickListener(v -> ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));

        return view;
    }

    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);

        if (selectedImageUri != null) {
            try {
                InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                String base64String = convertBitmapToBase64(bitmap);
                currentUserModel.setProfilePicBase64(base64String);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        updateToFirestore();
    }

    void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Updated successfully");
                    } else {
                        AndroidUtil.showToast(getContext(), "Update failed");
                    }
                });
    }

    void getUserData() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                currentUserModel = task.getResult().toObject(UserModel.class);
                if (currentUserModel != null) {
                    usernameInput.setText(currentUserModel.getUsername());
                    emailInput.setText(currentUserModel.getEmail());

                    String base64String = currentUserModel.getProfilePicBase64();
                    if (base64String != null && getContext() != null && isAdded()) {
                        AndroidUtil.setProfilePicFromBase64(getContext(), base64String, profilePic); // Circular crop for base64
                    } else if (selectedImageUri != null && getContext() != null && isAdded()) {
                        AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic); // Circular crop for URI
                    }
                }
            } else {
                // Handle failure
                AndroidUtil.showToast(getContext(), "Failed to fetch user data");
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
