package com.sekhgmainuddin.calculator.whatsappclone.menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sekhgmainuddin.calculator.whatsappclone.adapter.ChatListAdapter;
import com.sekhgmainuddin.calculator.whatsappclone.databinding.ActivityChatsBinding;
import com.sekhgmainuddin.calculator.whatsappclone.databinding.FragmentChatsBinding;
import com.sekhgmainuddin.calculator.whatsappclone.models.Chatlist;
import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }

    private FragmentChatsBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private DatabaseReference reference;
    private ChatListAdapter adapter;
    private List<Chatlist> list;
    private ArrayList<String> allUserID;
    private Handler handler=new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        list=new ArrayList<>();
        allUserID=new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ChatListAdapter(list,getContext());
        binding.recyclerView.setAdapter(adapter);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();

        if(firebaseUser!=null){
            getChatList();
        }

        return binding.getRoot();
    }

    private void getChatList() {
        list.clear();
        allUserID.clear();
        reference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String userID=dataSnapshot.child("chatid").getValue().toString();
                    allUserID.add(userID);
                }
                getUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfo() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for(String userId:allUserID){
                    firestore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            binding.chatlistprogressbar.setVisibility(View.GONE);
                            try{
                                Chatlist chat=new Chatlist(
                                        documentSnapshot.getString("userID"),
                                        documentSnapshot.getString("userName"),
                                        "this is the description",
                                        "",
                                        documentSnapshot.getString("imageProfile"),
                                        documentSnapshot.getString("userPhone")
                                );
                                list.add(chat);
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                            if(adapter!=null){
                                adapter.notifyItemChanged(0);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag","failed to fetch data");
                        }
                    });
                }
            }
        },300);

    }

}