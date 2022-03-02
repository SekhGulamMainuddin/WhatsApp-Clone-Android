package com.sekhgmainuddin.calculator.whatsappclone.view.contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sekhgmainuddin.calculator.whatsappclone.adapter.ContactAdapter;
import com.sekhgmainuddin.calculator.whatsappclone.databinding.ActivityContactBinding;
import com.sekhgmainuddin.calculator.whatsappclone.models.user.Users;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private ActivityContactBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private List<Users> list=new ArrayList<>();
    private ContactAdapter adapter;

    private ListView contact;
    private ArrayList mobileArray;
    private static final int REQUEST_READ_CONTACTS = 79;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

        binding.recyclerViewContact.setLayoutManager(new LinearLayoutManager(this));
        
        if(firebaseUser!=null){
           getContactFromPhone();
        }
        if(mobileArray!=null){
            getContactList();
        }

    }

    private void getContactFromPhone(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileArray = getAllPhoneContacts();
        } else {
            requestPermission();
        }
    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllPhoneContacts();
                } else {
                    finish();
                }
                return;
            }
        }
    }
    @SuppressLint("Range")
    private ArrayList getAllPhoneContacts() {
        ArrayList<String> phoneList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur!=null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                nameList.add(name);
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo=phoneNo.replace("-","").replace(" ","").trim();
                        phoneList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur!= null) {
            cur.close();
        }
        return phoneList;
    }

    private void getContactList() {

        firestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    String userId=snapshot.getString("userID");
                    String userName=snapshot.getString("userName");
                    String imageProfile=snapshot.getString("imageProfile");
                    String desc=snapshot.getString("bio");
                    String phone=snapshot.getString("userPhone");

                    Users users=new Users();
                    users.setUserID(userId);
                    users.setUserName(userName);
                    users.setImageProfile(imageProfile);
                    users.setBio(desc);
                    users.setUserPhone(phone);
                    if(userId!=null && !userId.equals(firebaseUser.getUid())){
                        if(mobileArray.contains(users.getUserPhone())){
                            list.add(users);
                        }
                    }
                }
                adapter=new ContactAdapter(list,ContactActivity.this);
                binding.recyclerViewContact.setAdapter(adapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ContactActivity.this, "Failed to fetch the contacts", Toast.LENGTH_SHORT).show();
            }
        });

    }
}