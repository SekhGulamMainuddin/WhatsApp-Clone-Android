package com.sekhgmainuddin.calculator.whatsappclone.services;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sekhgmainuddin.calculator.whatsappclone.models.StatusModel;

public class FirebaseService {

    private Context context;

    public FirebaseService(Context context) {
        this.context = context;
    }

    public void uploadImageToFirebaseStorage(Uri uri, OnCallBack onCallBack){

        StorageReference reference= FirebaseStorage.getInstance().getReference().child("ImagesChats/"+System.currentTimeMillis()+"."+getFileExtension(uri));
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()  {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri downloadUrl = uriTask.getResult();
                final String download_url=String.valueOf(downloadUrl);
                onCallBack.onUploadSuccess(download_url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCallBack.onUploadFailed(e);
            }
        });

    }
    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver=context.getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    public interface OnCallBack{
        void onUploadSuccess(String url);
        void onUploadFailed(Exception e);
    }

    public void addNewStatus(StatusModel statusModel,OnAddNewStatusCallBack onAddNewStatusCallBack){
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("Status Dialy").document(statusModel.getId()).set(statusModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onAddNewStatusCallBack.Success();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onAddNewStatusCallBack.Failed();
            }
        });
    }
    public interface OnAddNewStatusCallBack{
        void Success();
        void Failed();
    }
}
