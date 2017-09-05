package com.infikaa.indibubble.product;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.infikaa.indibubble.R;

/**
 * Created by Sohan on 23-Apr-17.
 */

public class CommentViewAdapter extends RecyclerView.Adapter<CommentViewAdapter.ViewHolder> {

    Comment[] comments;
    ProductDisplay fragment;
    String emaild;
    public CommentViewAdapter(Comment[] comments, ProductDisplay fragment, String emailId) {
        if (comments != null) {
            this.comments = new Comment[comments.length];
            this.comments = comments.clone();
        }
        this.fragment=fragment;
        this.emaild=emailId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.username.setText(comments[position].getUsername());
        holder.ratingBar.setRating(comments[position].getRating());
        holder.date.setText(comments[position].getDate());
        holder.comment.setText(comments[position].getComment());
        if (comments[position].getEmail().equals(emaild)) {
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(fragment.getView().getContext())
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete this entry?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    fragment.deleteComment(emaild);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        }
        else {
            holder.delete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (comments != null) {
            return comments.length;
        }
        else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        RatingBar ratingBar;
        TextView date;
        TextView comment;
        ImageButton delete;
        public ViewHolder(final View view) {
            super(view);
            username=(TextView)view.findViewById(R.id.comment_username);
            ratingBar=(RatingBar) view.findViewById(R.id.comment_ratingbar);
            date=(TextView)view.findViewById(R.id.comment_date);
            comment=(TextView)view.findViewById(R.id.comment_text);
            delete=(ImageButton) view.findViewById(R.id.comment_delete);
        }
    }
}
