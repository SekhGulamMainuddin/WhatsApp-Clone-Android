package com.sekhgmainuddin.calculator.whatsappclone.view.display;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.databinding.ActivityReviewSendImageBinding;

public class ReviewSendImageActivity extends AppCompatActivity {

    private ActivityReviewSendImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityReviewSendImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}