package com.sekhgmainuddin.calculator.whatsappclone.view.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.databinding.ActivityUserProflieBinding;

public class UserProflieActivity extends AppCompatActivity {

    private ActivityUserProflieBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserProflieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent=getIntent();
        String userName=intent.getStringExtra("userName");
        String receiverID=intent.getStringExtra("userID");
        String profileImage=intent.getStringExtra("imageProfile");
        String userPhone=intent.getStringExtra("userPhone");

        if(receiverID!=null){
            binding.toolbarUserProfile.setTitle(userName);
            binding.tvPhone.setText(userPhone);
            if(profileImage.isEmpty()){
                Glide.with(this).load(R.drawable.img).into(binding.userImageProfile);
            }
            else{
                Glide.with(this).load(profileImage).into(binding.userImageProfile);
            }
        }
        initClick();
    }

    private void initClick() {

        binding.toolbarUserProfile.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(binding.toolbarUserProfile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.home){
            finish();
        }
        else{
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}