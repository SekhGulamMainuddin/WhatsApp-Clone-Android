package com.sekhgmainuddin.calculator.whatsappclone.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sekhgmainuddin.calculator.whatsappclone.BuildConfig;
import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.databinding.ActivityMainBinding;
import com.sekhgmainuddin.calculator.whatsappclone.menu.CallsFragment;
import com.sekhgmainuddin.calculator.whatsappclone.menu.CameraFragment;
import com.sekhgmainuddin.calculator.whatsappclone.menu.ChatsFragment;
import com.sekhgmainuddin.calculator.whatsappclone.menu.StatusFragment;
import com.sekhgmainuddin.calculator.whatsappclone.view.contact.ContactActivity;
import com.sekhgmainuddin.calculator.whatsappclone.view.settings.SettingsActivity;
import com.sekhgmainuddin.calculator.whatsappclone.view.status.AddStatusPicActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private boolean firstfabicon=true;

    @Override
    protected void onStart() {
        super.onStart();
        if(firstfabicon){
            changeFabIcon(1);
            firstfabicon=false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());


        setUpWithViewPager(mainBinding.viewPager);
        mainBinding.tablayout.setupWithViewPager(mainBinding.viewPager);
        LinearLayout layout = ((LinearLayout) ((LinearLayout) mainBinding.tablayout.getChildAt(0)).getChildAt(0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
        layoutParams.weight = 0f;
        layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.setLayoutParams(layoutParams);

        View tab= LayoutInflater.from(this).inflate(R.layout.camerafragmenticon,null);
        try{
            mainBinding.tablayout.getTabAt(0).setCustomView(tab);
        }catch(Exception e){
            e.printStackTrace();
        }

        setSupportActionBar(mainBinding.toolBar);
        mainBinding.viewPager.setCurrentItem(1);

        mainBinding.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFabIcon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void changeFabIcon(int position) {
        mainBinding.floatingactionbutton.hide();
        mainBinding.btnStatusEdit.setVisibility(View.GONE);
        switch (position) {
            case 0: mainBinding.floatingactionbutton.hide();break;
            case 1:
                mainBinding.floatingactionbutton.show();
                mainBinding.floatingactionbutton.setImageResource(R.drawable.ic_baseline_chat_24);
                break;
            case 2:
                mainBinding.btnStatusEdit.setVisibility(View.VISIBLE);
                mainBinding.floatingactionbutton.show();
                mainBinding.floatingactionbutton.setImageResource(R.drawable.ic_baseline_camera_alt_24);
                break;
            case 3:
                mainBinding.floatingactionbutton.show();
                mainBinding.floatingactionbutton.setImageResource(R.drawable.ic_baseline_add_ic_call_24);
                break;

        }
        performClick(position);
    }
    public static Uri imageUri;

    private void checkCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    231);
        }else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    232);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==440 && resultCode==RESULT_OK){
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                uploadToFirebase();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageUri!=null){
                startActivity(new Intent(MainActivity.this, AddStatusPicActivity.class)
                .putExtra("image",imageUri));
            }

        }
    }
    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void performClick(int index){

        mainBinding.floatingactionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index==1){
                    mainBinding.floatingactionbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this, ContactActivity.class));
                        }
                    });
                }else if(index==2){
                    Toast.makeText(MainActivity.this, "Status", Toast.LENGTH_SHORT).show();
                    checkCameraPermission();
                }else {
                    Toast.makeText(MainActivity.this, "Call", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        mainBinding.btnStatusEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Edit Status", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUpWithViewPager(ViewPager viewPager) {

        MainActivity.SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.addFragment(new CameraFragment(),"");
        sectionPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        sectionPagerAdapter.addFragment(new StatusFragment(), "Status");
        sectionPagerAdapter.addFragment(new CallsFragment(), "Calls");
        viewPager.setAdapter(sectionPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                Toast.makeText(this, "Action Search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_new_group:
                Toast.makeText(this, "Action New Group", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_new_broadcast:
                Toast.makeText(this, "Action New Broadcast", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_starred_message:
                Toast.makeText(this, "Action Starred Message", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_wp_web:
                Toast.makeText(this, "Action WhatsApp Web", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}