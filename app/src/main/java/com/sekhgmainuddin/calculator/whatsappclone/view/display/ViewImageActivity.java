package com.sekhgmainuddin.calculator.whatsappclone.view.display;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sekhgmainuddin.calculator.whatsappclone.databinding.ActivityViewImageBinding;
import com.sekhgmainuddin.calculator.whatsappclone.view.common.Common;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityViewImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.viewprofileactivityimage.setImageBitmap(Common.IMAGE_BITMAP);
    }
}