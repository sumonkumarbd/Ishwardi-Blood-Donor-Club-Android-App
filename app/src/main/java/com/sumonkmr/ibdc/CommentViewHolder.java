package com.sumonkmr.ibdc;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    CircleImageView cmtImg;
    TextView cmtName,cmtMsg,cmtTime,cmtDate;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);

        cmtImg = itemView.findViewById(R.id.cmtImg);
        cmtName = itemView.findViewById(R.id.cmtName);
        cmtMsg = itemView.findViewById(R.id.cmtMsg);
        cmtTime = itemView.findViewById(R.id.cmtTime);
        cmtDate = itemView.findViewById(R.id.cmtDate);
    }
}
