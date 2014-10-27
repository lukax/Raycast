package com.raycast.controller.component;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.raycast.R;
import com.raycast.domain.Comment;
import com.raycast.util.CachedImageLoader;
import com.raycast.util.FormatUtil;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Lucas on 20/10/2014.
 */
@EViewGroup(R.layout.item_comment)
public class CommentItemView extends RelativeLayout {

    @Bean FormatUtil formatUtil;
    @Bean CachedImageLoader loader;

    @ViewById(R.id.comment_author) TextView authorView;
    @ViewById(R.id.comment_text) TextView commentView;
    @ViewById(R.id.comment_image) ImageView imageView;
    @ViewById(R.id.comment_time) TextView timeView;

    DisplayImageOptions options;
    ImageLoadingListener animateFirstListener;

    @AfterInject
    void afterInjection() {
        options = loader.getImageDisplayOptions();
        animateFirstListener = loader.getAnimateFirstListener();
    }

    public CommentItemView(Context context) {
        super(context);
    }

    public void bind(Comment comment){
        authorView.setText(comment.getAuthor().getName());
        commentView.setText(comment.getComment());
        timeView.setText(formatUtil.formatDate(comment));
        ImageLoader.getInstance().displayImage(comment.getAuthor().getImage(), imageView, options, animateFirstListener);
    }
}
