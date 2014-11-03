package com.raycast.controller;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.raycast.R;
import com.raycast.controller.base.RaycastBaseActivity;
import com.raycast.controller.component.CommentListAdapter;
import com.raycast.domain.Comment;
import com.raycast.domain.Message;
import com.raycast.event.MessageDetailFetchedEvent;
import com.raycast.event.RaycastErrorEvent;
import com.raycast.service.RaycastService;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import static com.raycast.controller.RaycastApp.BUS;

@EActivity(R.layout.activity_message_detail)
@OptionsMenu(R.menu.message_detail)
public class MessageDetailActivity extends RaycastBaseActivity {

    public final static String TAG = "MessageDetailActivity";
    public final static String EXTRA_MESSAGEDETAIL_MESSAGEID = "com.raycast.messagedetail.messageid";
    @Extra(EXTRA_MESSAGEDETAIL_MESSAGEID) String messageId;
    @Bean RaycastService raycastService;
    @ViewById(R.id.messagedetail_sendcomment) Button sendCommentButton;
    @ViewById(R.id.swipe_area) SwipeRefreshLayout swipeView;
    @ViewById(R.id.messagedetail_message) TextView msgText;
    @ViewById(R.id.messagedetail_editcomment) EditText editComment;
    @ViewById(R.id.messagedetail_commentlist) ListView commentList;
    @ViewById(R.id.alert_nocomment) TextView noCommentView;
    @Bean CommentListAdapter commentListAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        BUS.register(this);
    }

    @Override
    protected void onStop() {
        BUS.unregister(this);
        super.onStop();
    }

    @AfterViews
    void afterViews(){
        //TODO Gambiarra desgraçada, usar loading bar
        swipeView.setColorSchemeResources(R.color.raycast_purple_main, R.color.raycast_purple_sub, R.color.raycast_purple_dark, R.color.raycast_purple_light);
        swipeView.setEnabled(false);

        raycastService.getMessageDetail(messageId);
    }

    @UiThread
    public void onEvent(MessageDetailFetchedEvent event){
        //TODO: show a loader while messages aren't yet downloaded
        if(event.getMessage() != null){
            msgText.setText(event.getMessage().getMessage());
            commentListAdapter.bind(event.getComments());
            commentList.setAdapter(commentListAdapter);
        }

        //TODO: show a loader while comments aren't yet downloaded
        if(event.getComments() != null){
            commentListAdapter.bind(event.getComments());
            commentList.setAdapter(commentListAdapter);
            editComment.setText("");
        }

        noCommentView.setVisibility(commentListAdapter.getCount() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @UiThread
    public void onEvent(RaycastErrorEvent event){
        notifyUser(event.getMessage());
    }

    @OptionsItem(R.id.action_refresh)
    public void actionRefresh(){
        raycastService.getMessageDetail(messageId);
        swipeView.setRefreshing(true);
        ( new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeView.setRefreshing(false);
            }
        }, 3000);
    }

    @Click(R.id.messagedetail_sendcomment)
    public void sendComment(){
        if(TextUtils.isEmpty(editComment.getText())){
            notifyUser("Comentário vazio!");
            return;
        }

        Comment comment = new Comment();
        comment.setComment(editComment.getText().toString());
        raycastService.addComment(messageId, comment);
        raycastService.getMessageDetail(messageId);
    }

    @UiThread
    void notifyUser(String msg){
        //TODO: show a notification bar with the message instead of a toast
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}