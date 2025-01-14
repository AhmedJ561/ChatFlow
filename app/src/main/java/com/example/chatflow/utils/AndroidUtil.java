package com.example.chatflow.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chatflow.model.UserModel;

public class AndroidUtil {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("profilePicBase64", model.getProfilePicBase64());
    }

    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setProfilePicBase64(intent.getStringExtra("profilePicBase64"));
        return userModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        if (imageUri != null) {
            Glide.with(context)
                    .load(imageUri) // Load the image from the URI
                    .apply(RequestOptions.circleCropTransform()) // Apply circular crop
                    .into(imageView); // Set the image to the ImageView
        }
    }

    public static void setProfilePicFromBase64(Context context, String base64String, ImageView imageView) {
        if (base64String != null && !base64String.isEmpty()) {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(context)
                    .load(decodedByte) // Convert the Bitmap to Glide
                    .apply(RequestOptions.circleCropTransform()) // Apply circular crop
                    .into(imageView); // Set the image to the ImageView
        }
    }

    public static void setProfilePic(Context context, String base64String, Uri imageUri, ImageView imageView) {
        if (base64String != null) {
            setProfilePicFromBase64(context, base64String, imageView); // Use base64 image
        } else if (imageUri != null) {
            setProfilePic(context, imageUri, imageView); // Use URI image
        }
    }

}
