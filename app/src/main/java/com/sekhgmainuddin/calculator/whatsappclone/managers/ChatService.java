package com.sekhgmainuddin.calculator.whatsappclone.managers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sekhgmainuddin.calculator.whatsappclone.interfaces.OnReadChatCallBack;
import com.sekhgmainuddin.calculator.whatsappclone.models.chat.Chats;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatService {

    private Context context;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    private String receiverID;

    public ChatService(Context context) {
        this.context = context;
    }

    public ChatService(Context context, String receiverID) {
        this.context = context;
        this.receiverID = receiverID;
    }

    public void readChatData(OnReadChatCallBack onCallBack){
        databaseReference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Chats> list=new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chats chats=dataSnapshot.getValue(Chats.class);
                    if(chats!=null && (chats.getSender().equals(firebaseUser.getUid()) || chats.getSender().equals(receiverID)) &&
                            (chats.getReceiver().equals(receiverID) || chats.getReceiver().equals(firebaseUser.getUid()))){
                        list.add(chats);
                    }
                }
                onCallBack.OnReadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onCallBack.OnReadFailed();
            }
        });
    }
    public void sendTextMsg(String text){

        Chats chats= new Chats(
                getCurrentDate(),
                text,
                "",
                "TEXT",
                firebaseUser.getUid(),
                receiverID);

        databaseReference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Send","Successfully sent the Message");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Send","Failed to send the text message");
            }
        });

        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatRef1.child("chatid").setValue(receiverID);

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());

    }

    public void sendImage(String imageUri){

        Chats chats= new Chats(
                getCurrentDate(),
                "",
                imageUri,
                "IMAGE",
                firebaseUser.getUid(),
                receiverID);

        databaseReference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Send","Successfully sent the Message");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Send","Failed to send the text message");
            }
        });

        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatRef1.child("chatid").setValue(receiverID);

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());


    }

    public String getCurrentDate(){
        Date date= Calendar.getInstance().getTime();
        SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
        String today=format.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());

        return today+","+ currentTime;
    }

    public void sendVoice(String audioPath){
        final Uri uriAudio= Uri.fromFile(new File(audioPath));
        final StorageReference audioRef= FirebaseStorage.getInstance().getReference().child("Chats/Voice/"+ System.currentTimeMillis());
        audioRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                String voiceUrl= String.valueOf(downloadUrl);


                Chats chats=new Chats(
                        getCurrentDate(),
                        "",
                        voiceUrl,
                        "VOICE",
                        firebaseUser.getUid(),
                        receiverID
                );
                databaseReference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Send","Successfully sent the Message");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Send","Failed to send the text message");
                    }
                });

                DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
                chatRef1.child("chatid").setValue(receiverID);

                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
                chatRef2.child("chatid").setValue(firebaseUser.getUid());

            }
        });
    }
}
