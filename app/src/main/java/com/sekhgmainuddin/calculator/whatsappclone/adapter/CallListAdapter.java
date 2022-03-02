package com.sekhgmainuddin.calculator.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.models.CallList;

import java.util.ArrayList;
import java.util.List;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.ViewHolder> {

    private List<CallList> list=new ArrayList<>();
    private Context context;

    public CallListAdapter(List<CallList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_call_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallList callList=list.get(position);
        holder.tv_username.setText(callList.getUserName());
        holder.tv_date.setText(callList.getDate());
        if(callList.getUrlProfile().isEmpty()){
            Glide.with(context).load(R.drawable.img).into(holder.imageView);
        }
        else{
            Glide.with(context).load(callList.getUrlProfile()).into(holder.imageView);
        }

        if(callList.getCallType().equals("missed")){
            holder.calltype.setImageResource(R.drawable.ic_baseline_call_missed_24);
            holder.calltype.getDrawable().setTint(context.getResources().getColor(R.color.red));
        }else if (callList.getCallType().equals("income"))
        {
            holder.calltype.setImageResource(R.drawable.ic_baseline_call_received_24);
            holder.calltype.getDrawable().setTint(context.getResources().getColor(R.color.colorPrimary));
        }else {
            holder.calltype.setImageResource(R.drawable.ic_baseline_call_made_24);
            holder.calltype.getDrawable().setTint(context.getResources().getColor(R.color.colorPrimary));
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_username,tv_date;
        private CircularImageView imageView;
        private ImageView calltype;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_date=itemView.findViewById(R.id.tv_date);
            tv_username=itemView.findViewById(R.id.tv_username);
            imageView=itemView.findViewById(R.id.image_profile);
            calltype=itemView.findViewById(R.id.call_type);
        }
    }
}
