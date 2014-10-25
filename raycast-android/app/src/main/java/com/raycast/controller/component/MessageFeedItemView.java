package com.raycast.controller.component;

import android.content.Context;
import android.location.Location;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.raycast.R;
import com.raycast.domain.Message;
import com.raycast.util.CachedImageLoader;
import com.raycast.util.FormatUtil;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Lucas on 20/10/2014.
 */
@EViewGroup(R.layout.item_message)
public class MessageFeedItemView extends RelativeLayout {

    @Bean FormatUtil formatUtil;
    @Bean CachedImageLoader loader;

    @ViewById(R.id.message_image) ImageView profileImage;
    @ViewById(R.id.message_author) TextView name;
    @ViewById(R.id.message_content) TextView content;
    @ViewById(R.id.message_distance) TextView distance;
    @ViewById(R.id.message_time) TextView time;

    Location myLocation;
    DisplayImageOptions options;
    ImageLoadingListener animateFirstListener;

    @AfterInject
    void afterInjection() {
        options = loader.getImageDisplayOptions();
        animateFirstListener = loader.getAnimateFirstListener();
    }

    public MessageFeedItemView(Context context) {
        super(context);
    }

    public void setLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    public void bind(Message message) {
        ImageLoader.getInstance().displayImage(message.getAuthor().getImage(), profileImage, options, animateFirstListener);

        name.setText(message.getAuthor().getName());
        content.setText(message.getMessage());
        distance.setText(formatUtil.calculateMessageDistanceFromMyLocation(message, myLocation));
        time.setText(formatUtil.formatDate(message));
    }
}
