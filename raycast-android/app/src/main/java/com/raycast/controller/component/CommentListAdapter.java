package com.raycast.controller.component;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.raycast.domain.Comment;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 20/10/2014.
 */
@EBean
public class CommentListAdapter extends BaseAdapter {

    @RootContext Context context;
    List<Comment> comments = new ArrayList<Comment>();

    public void bind(List<Comment> comments){
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Comment getItem(int position) {
        return comments.get((comments.size() - 1) - position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CommentItemView commentItemView;

        if (convertView == null) {
            commentItemView = CommentItemView_.build(context);
        } else {
            commentItemView = (CommentItemView) convertView;
        }

        commentItemView.bind(getItem(position));

        return commentItemView;
    }
}
