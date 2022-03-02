package com.sekhgmainuddin.calculator.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.models.chat.Chats;
import com.sekhgmainuddin.calculator.whatsappclone.tools.AudioService;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {

    private List<Chats> list;
    private Context context;
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser firebaseUser;

    private ImageButton tmpBtnPlay;
    private AudioService audioService;

    public ChatsAdapter(List<Chats> list, Context context) {
        this.list = list;
        this.context = context;
        audioService=new AudioService(context);
    }

    public void setList(List<Chats> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==MSG_TYPE_LEFT){
            view= LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
        }
        else{
            view=LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);

        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(list.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView textMessage;
        private LinearLayout layout_text,layout_image,layout_voice;
        private ImageView image;
        private ImageButton btn_voice;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage=itemView.findViewById(R.id.tv_text_message);
            layout_image=itemView.findViewById(R.id.ln_image_message);
            layout_text=itemView.findViewById(R.id.ln_text_message);
            layout_voice=itemView.findViewById(R.id.ln_voice_message);
            image=itemView.findViewById(R.id.image_chat);
            btn_voice=itemView.findViewById(R.id.btn_play_chat);
        }

        void bind(Chats chats) {

            switch (chats.getType()){
                case "TEXT":
                    layout_image.setVisibility(View.GONE);
                    layout_text.setVisibility(View.VISIBLE);
                    layout_voice.setVisibility(View.GONE);
                    textMessage.setText(chats.getTextMessage());
                    break;
                case "IMAGE":
                    layout_image.setVisibility(View.VISIBLE);
                    layout_text.setVisibility(View.GONE);
                    layout_voice.setVisibility(View.GONE);
                    Glide.with(context).load(chats.getUrl()).into(image);
                case "VOICE":
                    layout_image.setVisibility(View.VISIBLE);
                    layout_text.setVisibility(View.INVISIBLE);
                    layout_voice.setVisibility(View.VISIBLE);
                    layout_voice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tmpBtnPlay!=null){
                                tmpBtnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                            }

                            btn_voice.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_circle_filled_24));
                            audioService.playAudioFromUrl(chats.getUrl(), new AudioService.OnPlayCallBack() {
                                @Override
                                public void onFinished() {
                                    btn_voice.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                                }
                            });
                            tmpBtnPlay=btn_voice;
                        }
                    });
            }
        }
    }
}
