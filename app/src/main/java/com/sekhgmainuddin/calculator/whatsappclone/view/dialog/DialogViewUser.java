package com.sekhgmainuddin.calculator.whatsappclone.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.models.Chatlist;
import com.sekhgmainuddin.calculator.whatsappclone.view.chats.ChatsActivity;
import com.sekhgmainuddin.calculator.whatsappclone.view.common.Common;
import com.sekhgmainuddin.calculator.whatsappclone.view.display.ViewImageActivity;
import com.sekhgmainuddin.calculator.whatsappclone.view.profile.ProfileActivity;

public class DialogViewUser {

    private Context context;

    public DialogViewUser(Context context, Chatlist chatlist) {
        this.context = context;
        initialize(chatlist);
    }

    private void initialize(Chatlist chatlist) {

        Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.dialog_view_user);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width=WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        ImageButton btnChat,btnCall,btnVideoCall,btnInfo;
        ImageView profile;
        TextView userName;

        btnChat=dialog.findViewById(R.id.btn_chat);
        btnCall=dialog.findViewById(R.id.btn_call);
        btnVideoCall=dialog.findViewById(R.id.btn_video);
        btnInfo=dialog.findViewById(R.id.btn_info);
        profile=dialog.findViewById(R.id.image_profile);
        userName=dialog.findViewById(R.id.tv_username);

        userName.setText(chatlist.getUserName());
        Glide.with(context).load(chatlist.getUrlProfile()).into(profile);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userName",chatlist.getUserName())
                        .putExtra("userID",chatlist.getUserID())
                        .putExtra("profileImage",chatlist.getUrlProfile()));
                dialog.dismiss();
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Call Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Video Call Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Info Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.invalidate();
                Drawable dr=profile.getDrawable();
                Common.IMAGE_BITMAP = ((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,profile,"image");
                Intent intent=new Intent(context, ViewImageActivity.class);
                context.startActivity(intent, activityOptionsCompat.toBundle());
            }
        });

        dialog.show();


    }
}
