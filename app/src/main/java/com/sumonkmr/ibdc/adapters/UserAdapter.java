package com.sumonkmr.ibdc.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.squareup.picasso.Picasso;
import com.sumonkmr.ibdc.DisplayDonorsActivity;
import com.sumonkmr.ibdc.R;
import com.sumonkmr.ibdc.StringCaseConverter;
import com.sumonkmr.ibdc.listeners.MyOnClickListener;
import com.sumonkmr.ibdc.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder>{


    ArrayList<User> users;
    Context context;
    MyOnClickListener myOnClickListenerCall,myOnClickListenerShare;


    public void updateList(ArrayList<User>users){
        this.users = users;
        notifyDataSetChanged();
    }


    public UserAdapter(Context context,ArrayList<User> users, MyOnClickListener onClickListenerCall,MyOnClickListener onClickListenerShare){
        this.context = context;
        this.users = users;
        this.myOnClickListenerCall = onClickListenerCall;
        this.myOnClickListenerShare = onClickListenerShare;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(context).inflate(R.layout.details_donor_requester,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        String state,district,tehsil,fname,village,bloodgroup,bloodImg_url,lastDonateDate;
        state = users.get(position).getDivision();
        district = users.get(position).getDistrict();
        tehsil = users.get(position).getUpazila();
        village = users.get(position).getVillage();
        bloodgroup = users.get(position).getBloodGroup();
        bloodImg_url = users.get(position).getImg_url();
        lastDonateDate = users.get(position).getLastDonateDate();
        fname = String.format("%s %s", users.get(position).getFName(), users.get(position).getLName());
        String s = StringCaseConverter.convertToTitleCaseIteratingChars(fname);
        holder.state.setText(state);
        holder.district.setText(district);
        holder.tehsil.setText(tehsil);
        holder.village.setText(village);
        holder.fullName.setText(s);
        holder.bloodGroup.setText(bloodgroup);
        holder.lastDonateDate.setText(lastDonateDate);
        Glide
                .with(context)
                .load(bloodImg_url)
                .centerCrop()
                .placeholder(R.drawable.ibdc_logo)
                .into(holder.bloodImg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.state.setText(state);
            holder.district.setTooltipText(district);
            holder.tehsil.setTooltipText(tehsil);
            holder.village.setTooltipText(village);
            holder.fullName.setTooltipText(fname);
            holder.lastDonateDate.setTooltipText(lastDonateDate);
            Glide
                    .with(holder.bloodImg)
                    .load(bloodImg_url)
                    .centerCrop()
                    .placeholder(R.drawable.ibdc_logo)
                    .into(holder.bloodImg);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.share.setOnClickListener(v -> myOnClickListenerShare.getPosition(position));
        holder.call.setOnClickListener(v -> myOnClickListenerCall.getPosition(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserHolder extends RecyclerView.ViewHolder{

        TextView fullName,bloodGroup,state,district,tehsil,village,lastDonateDate;

        ImageView share,call,bloodImg;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.detailFullName);
            bloodGroup = itemView.findViewById(R.id.detailBloodGroup);
            state = itemView.findViewById(R.id.detailState);
            district = itemView.findViewById(R.id.detailDistrict);
            tehsil = itemView.findViewById(R.id.detailTehsil);
            village = itemView.findViewById(R.id.detailVillage);
            call = itemView.findViewById(R.id.call);
            share = itemView.findViewById(R.id.share);
            lastDonateDate = itemView.findViewById(R.id.lastDonateDate);
            bloodImg = itemView.findViewById(R.id.bloodImg);



        }
    }

}
