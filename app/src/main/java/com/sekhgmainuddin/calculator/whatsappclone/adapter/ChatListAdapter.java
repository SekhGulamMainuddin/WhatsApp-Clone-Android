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
import com.sekhgmainuddin.calculator.whatsappclone.models.Chatlist;
import com.sekhgmainuddin.calculator.whatsappclone.view.chats.ChatsActivity;
import com.sekhgmainuddin.calculator.whatsappclone.view.dialog.DialogViewUser;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<Chatlist> list=new ArrayList<>();
    private Context context;

    public ChatListAdapter(List<Chatlist> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_chat_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chatlist chatlist=list.get(position);
        holder.tv_username.setText(chatlist.getUserName());
        holder.tv_date.setText(chatlist.getDate());
        holder.tv_desc.setText(chatlist.getDescription());
        if(chatlist.getUrlProfile()==null || chatlist.getUrlProfile().isEmpty()){
            Glide.with(context).load(R.drawable.img).into(holder.imageView);
        }
        else{
            Glide.with(context).load(chatlist.getUrlProfile()).into(holder.imageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userName",chatlist.getUserName())
                        .putExtra("userID",chatlist.getUserID())
                        .putExtra("profileImage",chatlist.getUrlProfile())
                        .putExtra("userPhone",chatlist.getUserPhone()));
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogViewUser(context,chatlist);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_username,tv_desc,tv_date;
        private CircularImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_date=itemView.findViewById(R.id.tv_date);
            tv_desc=itemView.findViewById(R.id.tv_desc);
            tv_username=itemView.findViewById(R.id.tv_username);
            imageView=itemView.findViewById(R.id.image_profile);

        }
    }
}
