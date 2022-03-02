package com.sekhgmainuddin.calculator.whatsappclone.view.chats;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.adapter.ChatsAdapter;
import com.sekhgmainuddin.calculator.whatsappclone.databinding.ActivityChatsBinding;
import com.sekhgmainuddin.calculator.whatsappclone.interfaces.OnReadChatCallBack;
import com.sekhgmainuddin.calculator.whatsappclone.managers.ChatService;
import com.sekhgmainuddin.calculator.whatsappclone.models.chat.Chats;
import com.sekhgmainuddin.calculator.whatsappclone.services.FirebaseService;
import com.sekhgmainuddin.calculator.whatsappclone.view.dialog.DialogReviewSendImage;
import com.sekhgmainuddin.calculator.whatsappclone.view.profile.UserProflieActivity;

import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatsActivity extends AppCompatActivity {

    private static final int REQUEST_CORD_PERMISSION = 332;
    private ActivityChatsBinding binding;
    private String receiverID;
    private ChatsAdapter adapter;
    private List<Chats> list=new ArrayList<>();
    private String profileImage;
    private String phoneNumber;
    private String userName;
    private boolean isActionShown=false;
    private ChatService chatService;
    private int IMAGE_GALLERY_REQUEST=111;
    private Uri imageUri;
    private String TAG="ChatsActivity";
    private MediaRecorder mediaRecorder;
    private String audio_path;
    private String sTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialize();
        initBtnClick();
        readChats();

    }

    private void initialize(){

        Intent intent=getIntent();
        userName=intent.getStringExtra("userName");
        receiverID=intent.getStringExtra("userID");
        profileImage=intent.getStringExtra("profileImage");
        phoneNumber=intent.getStringExtra("userPhone");

        chatService=new ChatService(this, receiverID);

        if(receiverID!=null){
            binding.tvUsernameChat.setText(userName);

            if(profileImage.isEmpty()){
                Glide.with(this).load(R.drawable.img).into(binding.chatUserProfileImage);
            }
            else{
                Glide.with(this).load(profileImage).into(binding.chatUserProfileImage);
            }
        }

        binding.btnBackChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(binding.edMessage.getText().toString())){
                    binding.btnSend.setVisibility(View.INVISIBLE);
                    binding.recordButton.setVisibility(View.VISIBLE);
                }
                else{
                    binding.btnSend.setVisibility(View.VISIBLE);
                    binding.recordButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager layoutManager=new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerViewChats.setLayoutManager(layoutManager);
        adapter=new ChatsAdapter(list,this);
        binding.recyclerViewChats.setAdapter(adapter);

        binding.recordButton.setRecordView(binding.recordView);
        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                if(!checkPermissionFromDevice()){
                    binding.btnEmoji.setVisibility(View.INVISIBLE);
                    binding.btnAttachment.setVisibility(View.INVISIBLE);
                    binding.btnCamera.setVisibility(View.INVISIBLE);
                    binding.edMessage.setVisibility(View.INVISIBLE);

                    startRecord();
                    Vibrator vibrator=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if(vibrator!=null){
                        vibrator.vibrate(100);
                    }
                }
                else{
                    requestPermission();
                }
            }

            @Override
            public void onCancel() {

                try{
                    mediaRecorder.reset();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinish(long recordTime) {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.btnAttachment.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);

                try{
                    sTime=getHumanTimeText(recordTime);
                    stopRecord();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onLessThanSecond() {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.btnAttachment.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
            }
        });
        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.btnAttachment.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
            }
        });

    }

    private void stopRecord() {

        try{
            if(mediaRecorder!=null){
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder=null;

                chatService.sendVoice(audio_path);
            }else{
                Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Stop Recording Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds){
        return String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(milliseconds)+TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

    }

    private void startRecord() {

        setUpMediaRecorder();
        try{
            if(mediaRecorder!=null){

                mediaRecorder.prepare();
                mediaRecorder.start();
            }
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Recording Error. Please Restart the App", Toast.LENGTH_SHORT).show();
        }

    }

    private void setUpMediaRecorder() {

        String path_save= Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "audio_record.m4a";
        audio_path=path_save;

        mediaRecorder= new MediaRecorder();
        try{
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(path_save);
        }catch(Exception e){
            Log.d(TAG,"setUpMediaRecord: "+e.getMessage());
        }

    }

    private void readChats() {

        chatService.readChatData(new OnReadChatCallBack() {
            @Override
            public void OnReadSuccess(List<Chats> list) {
                adapter.setList(list);
            }

            @Override
            public void OnReadFailed() {
                Log.d("Failed","onReadFailed");
            }
        });

    }


    private void initBtnClick(){
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(binding.edMessage.getText().toString())){
                    chatService.sendTextMsg(binding.edMessage.getText().toString());
                }
                binding.edMessage.setText("");
            }
        });

        binding.chatUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatsActivity.this, UserProflieActivity.class)
                .putExtra("userID",receiverID)
                .putExtra("imageProfile",profileImage)
                .putExtra("userName",userName)
                .putExtra("userPhone",phoneNumber));
            }
        });
        binding.btnAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActionShown){
                    isActionShown=false;
                    binding.attachmentDrawer.setVisibility(View.GONE);
                }
                else{
                    binding.attachmentDrawer.setVisibility(View.VISIBLE);
                    isActionShown=true;
                }
            }
        });
        binding.btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }
    private void requestPermission(){

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_CORD_PERMISSION);

    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_DENIED || record_audio_result==PackageManager.PERMISSION_DENIED;
    }

    private void openGallery() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select image"),IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_GALLERY_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            imageUri=data.getData();
            //uploadToFirebase();

            try{
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                reviewImage(bitmap);
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    private void reviewImage(Bitmap bitmap) {
        new DialogReviewSendImage(ChatsActivity.this,bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void OnButtonSendClick() {
                if(imageUri!=null){
                    ProgressDialog progressDialog=new ProgressDialog(ChatsActivity.this);
                    progressDialog.setMessage("Sending image...");
                    progressDialog.show();

                    binding.attachmentDrawer.setVisibility(View.GONE);
                    isActionShown=false;
                    new FirebaseService(ChatsActivity.this).uploadImageToFirebaseStorage(imageUri, new FirebaseService.OnCallBack() {
                        @Override
                        public void onUploadSuccess(String url) {
                            chatService.sendImage(url);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onUploadFailed(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}