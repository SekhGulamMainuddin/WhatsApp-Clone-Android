package com.sekhgmainuddin.calculator.whatsappclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.models.user.Users;
import com.sekhgmainuddin.calculator.whatsappclone.view.chats.ChatsActivity;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Users> list;
    private Context context;

    public ContactAdapter(List<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_contact_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users= list.get(position);
        holder.username.setText(users.getUserName());
        holder.desc.setText(users.getBio());
        if(users.getImageProfile().isEmpty()){
            Glide.with(context).load(R.drawable.img).into(holder.image);
        }
        else{
            Glide.with(context).load(users.getImageProfile()).into(holder.image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                .putExtra("userName",users.getUserName())
                .putExtra("userID",users.getUserID())
                .putExtra("profileImage",users.getImageProfile())
                .putExtra("userPhone",users.getUserPhone()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircularImageView image;
        private TextView username, desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.profile_image_contact);
            username=itemView.findViewById(R.id.tv_username_contact);
            desc=itemView.findViewById(R.id.tv_desc_contact);

        }
    }
}
