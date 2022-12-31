package com.sekhgmainuddin.calculator.whatsappclone.view.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.databinding.ActivitySetUserInfoBinding;
import com.sekhgmainuddin.calculator.whatsappclone.models.user.Users;
import com.sekhgmainuddin.calculator.whatsappclone.view.MainActivity;
import com.sekhgmainuddin.calculator.whatsappclone.view.profile.ProfileActivity;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SetUserInfoActivity extends AppCompatActivity {

    private ActivitySetUserInfoBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private BottomSheetDialog bottomSheetDialog,bottomSheetEditName;
    private int IMAGE_GALLERY_REQUEST=111;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySetUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firestore= FirebaseFirestore.getInstance();
        
        firestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                boolean t=false;
                for(DocumentSnapshot v:value.getDocuments()){
                    if(firebaseUser.getUid().equals(v.getId())){
                        getInfo();
                        t=true;
                        break;
                    }
                }
                if(t==false){
                    newUser();
                }
            }
        });

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ...");
        initButtonClick();
    }
    private void newUser(){
        Users users=new Users(firebaseUser.getUid(),"",firebaseUser.getPhoneNumber(),"","","","","","","");
        firestore.collection("Users").document(firebaseUser.getUid()).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(SetUserInfoActivity.this, "New User Created", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetUserInfoActivity.this, "Failed to create new User", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initButtonClick() {

        binding.btnSetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if(TextUtils.isEmpty(binding.edNameInfo.getText().toString())){
                    progressDialog.dismiss();
                    Toast.makeText(SetUserInfoActivity.this, "Please Enter the UserName", Toast.LENGTH_SHORT).show();
                }
                else{
                    doUpdate();
                }
            }
        });
        binding.userProfileImageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetPickPhoto();
            }
        });
    }

    private void doUpdate() {
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            String userId=user.getUid();
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("userName", binding.edNameInfo.getText().toString());

            firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SetUserInfoActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(SetUserInfoActivity.this,MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetUserInfoActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SetUserInfoActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            progressDialog.dismiss();
            Toast.makeText(this, "You need to login first", Toast.LENGTH_SHORT).show();
        }

    }

    private void showBottomSheetPickPhoto() {

        bottomSheetDialog=new BottomSheetDialog(this);
        View view=getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);
        bottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        view.findViewById(R.id.ln_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SetUserInfoActivity.this, "Gallery", Toast.LENGTH_SHORT).show();
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });
        view.findViewById(R.id.ln_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog=null;
            }
        });

        bottomSheetDialog.show();

    }

    private void checkCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    221);
        }else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    222);
        }else{
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp=new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_"+timeStamp+".jpg";

        try{
            File file=File.createTempFile("IMG_"+timeStamp,".jpg",getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider",file);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,imageUri);
            intent.putExtra("listPhotoName",imageFileName);
            startActivityForResult(intent, 440);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void openGallery() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select image"),IMAGE_GALLERY_REQUEST);

    }

    private void getInfo() {

        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String imageUrl=documentSnapshot.getString("imageProfile").toString();
                String name=documentSnapshot.getString("userName").toString();
                binding.edNameInfo.setText(name);
                if (imageUrl.isEmpty()){
                    Glide.with(SetUserInfoActivity.this).load(getDrawable(R.drawable.img)).into(binding.userProfileImageInfo);
                }else{
                    Glide.with(getApplicationContext()).load(imageUrl).into(binding.userProfileImageInfo);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "data retrieval failure in profile activity");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_GALLERY_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            imageUri=data.getData();
            Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show();
            uploadToFirebase();

//            try{
//                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
//                binding.profileImage.setImageBitmap(bitmap);
//            }catch(Exception e){
//                e.printStackTrace();
//            }

        }
        if(requestCode==440 && resultCode==RESULT_OK){
//            imageUri=data.getData();
//            Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                uploadToFirebase();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadToFirebase() {
        progressDialog.setMessage("Uploading Please Wait...");
        progressDialog.show();
        if(imageUri==null){
            progressDialog.dismiss();
            //Toast.makeText(this, "No data imageUri null", Toast.LENGTH_SHORT).show();
        }
        if(imageUri!=null){
            final StorageReference reference= FirebaseStorage.getInstance().getReference().child("ImagesProfile/"+System.currentTimeMillis()+"."+getFileExtension(imageUri));
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()  {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                    Uri downloadUrl = uriTask.getResult();
                    final String download_url=String.valueOf(downloadUrl);
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("imageProfile", download_url);

                    firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(SetUserInfoActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                    getInfo();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SetUserInfoActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SetUserInfoActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SetUserInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
}