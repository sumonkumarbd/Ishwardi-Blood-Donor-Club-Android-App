package com.sumonkmr.ibdc;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

// ViewHolder class to hold references to the UI elements of each comment item in the RecyclerView
public class CommentViewHolder extends RecyclerView.ViewHolder {
    // Image view for the user's profile picture in the comment
    CircleImageView cmtImg;

    // Buttons for deleting and editing the comment
    ImageButton cmt_delete, cmt_edit;

    // Text views for displaying the comment's details
    TextView cmtName, cmtMsg, cmtTime, cmtDate, cmtEditTimeDate;

    // Constructor to initialize all the UI elements within the itemView (one item of the RecyclerView)
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView); // Calls the RecyclerView.ViewHolder constructor

        // Binding UI elements to their respective views in the layout
        cmtImg = itemView.findViewById(R.id.cmtImg); // Profile image
        cmtName = itemView.findViewById(R.id.cmtName); // Commenter's name
        cmtMsg = itemView.findViewById(R.id.cmtMsg); // Comment message text
        cmtTime = itemView.findViewById(R.id.cmtTime); // Time of the comment
        cmtDate = itemView.findViewById(R.id.cmtDate); // Date of the comment
        cmt_delete = itemView.findViewById(R.id.cmt_delete); // Button to delete the comment
        cmt_edit = itemView.findViewById(R.id.cmt_edit); // Button to edit the comment
        cmtEditTimeDate = itemView.findViewById(R.id.cmtEditTimeDate); // Text displaying when the comment was last edited
    }
}
