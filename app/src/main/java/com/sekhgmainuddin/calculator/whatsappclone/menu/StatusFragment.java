package com.sekhgmainuddin.calculator.whatsappclone.menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.databinding.FragmentStatusBinding;
import com.sekhgmainuddin.calculator.whatsappclone.view.status.DisplayStatusActivity;


public class StatusFragment extends Fragment {



    public StatusFragment() {
        // Required empty public constructor
    }


    private FragmentStatusBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentStatusBinding.inflate(inflater,container,false);

        binding.lnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),DisplayStatusActivity.class));
            }
        });
        getProfile();

        return binding.getRoot();
    }

    private void getProfile() {

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String imageUrl=documentSnapshot.getString("imageProfile").toString();

                Glide.with(getContext()).load(imageUrl).into(binding.imageProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "data retrieval failure in profile activity");
            }
        });

    }
}