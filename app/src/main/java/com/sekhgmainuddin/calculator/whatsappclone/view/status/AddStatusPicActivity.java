package com.sekhgmainuddin.calculator.whatsappclone.view.status;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.databinding.ActivityAddStatusPicBinding;
import com.sekhgmainuddin.calculator.whatsappclone.managers.ChatService;
import com.sekhgmainuddin.calculator.whatsappclone.models.StatusModel;
import com.sekhgmainuddin.calculator.whatsappclone.services.FirebaseService;
import com.sekhgmainuddin.calculator.whatsappclone.view.MainActivity;

import java.util.UUID;

public class AddStatusPicActivity extends AppCompatActivity {

    private ActivityAddStatusPicBinding binding;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddStatusPicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imageUri=MainActivity.imageUri;
        setInfo();
        initClick();
    }

    private void initClick() {

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FirebaseService(AddStatusPicActivity.this).uploadImageToFirebaseStorage(imageUri, new FirebaseService.OnCallBack() {
                    @Override
                    public void onUploadSuccess(String url) {
                        StatusModel statusModel=new StatusModel();
                        statusModel.setId(UUID.randomUUID().toString());
                        statusModel.setCreatedDate(new ChatService(AddStatusPicActivity.this).getCurrentDate());
                        statusModel.setImageStatus(url);
                        statusModel.setUserID(FirebaseAuth.getInstance().getUid());
                        statusModel.setViewCount("0");
                        statusModel.setTextStatus(binding.edMessageStatus.getText().toString());
                        new FirebaseService(AddStatusPicActivity.this).addNewStatus(statusModel, new FirebaseService.OnAddNewStatusCallBack() {
                            @Override
                            public void Success() {
                                Toast.makeText(AddStatusPicActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void Failed() {
                                Toast.makeText(AddStatusPicActivity.this, "Failed to upload the status", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onUploadFailed(Exception e) {

                    }
                });
            }
        });
    }

    private void setInfo() {

        Glide.with(this).load(MainActivity.imageUri).into(binding.statusImage);

    }
}