package com.sekhgmainuddin.calculator.whatsappclone.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sekhgmainuddin.calculator.whatsappclone.R;
import com.sekhgmainuddin.calculator.whatsappclone.adapter.CallListAdapter;
import com.sekhgmainuddin.calculator.whatsappclone.models.CallList;
import java.util.ArrayList;
import java.util.List;

public class CallsFragment extends Fragment {



    public CallsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calls, container, false);

        List<CallList> list=new ArrayList<>();
        RecyclerView recyclerView;

        recyclerView=view.findViewById(R.id.recyclerViewCalls);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CallListAdapter adapter=new CallListAdapter(list,getContext());

//        list.add(new CallList("11",
//                "Arijit Singh",
//                "https://a10.gaanacdn.com/gn_img/artists/Dk9KNk23Bx/k9KNqJJbBx/size_xl_1638898900.webp",
//                "missed",
//                "15/04/2021"));
//        list.add(new CallList("21",
//                "Sonu Nigam",
//                "https://miro.medium.com/max/900/1*ddqyoPFlzR6PbFnr4oVcfw.jpeg",
//                "income",
//                "25/04/2021"));
//        list.add(new CallList("31",
//                "KK",
//                "https://images.hindustantimes.com/rf/image_size_800x600/HT/p2/2016/07/01/Pictures/_1092b492-3f49-11e6-aebc-a22ff8d772df.jpg",
//                "out",
//                "5/04/2021"));
//        list.add(new CallList("41",
//                "Atif Aslam",
//                "https://emra.tv/en/wp-content/uploads/2021/06/20-15.jpg",
//                "out",
//                "1/04/2021"));
//
//        recyclerView.setAdapter(adapter);
        return view;
    }
}