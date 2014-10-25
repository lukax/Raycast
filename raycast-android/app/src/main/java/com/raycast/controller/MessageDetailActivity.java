package com.raycast.controller;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.raycast.R;
import com.raycast.controller.base.RaycastBaseActivity;
import com.raycast.controller.component.CommentListAdapter;
import com.raycast.domain.Comment;
import com.raycast.domain.Message;
import com.raycast.service.base.RaycastRESTClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;

import java.util.List;

@EActivity(R.layout.activity_message_detail)
@OptionsMenu(R.menu.message_detail)
public class MessageDetailActivity extends RaycastBaseActivity {

    public final static String TAG = "MessageDetailActivity";
    public final static String EXTRA_MESSAGEDETAIL_MESSAGEID = "com.raycast.messagedetail.messageid";
    @Extra(EXTRA_MESSAGEDETAIL_MESSAGEID) String messageId;
    @RestService RaycastRESTClient raycastRESTClient;
    @ViewById(R.id.swipe_area) SwipeRefreshLayout swipeArea;
    @ViewById(R.id.messagedetail_message) TextView msgText;
    @ViewById(R.id.messagedetail_editcomment) EditText editComment;
    @ViewById(R.id.messagedetail_commentlist) ListView commentList;
    @Bean CommentListAdapter commentListAdapter;
    Message message;
    List<Comment> comments;

    @AfterViews
    void afterViews(){
        fetchMessage();
    }

    @AfterViews
    void setListeners() {
        swipeArea.setColorSchemeResources(android.R.color.holo_purple, android.R.color.holo_purple, android.R.color.holo_purple, android.R.color.holo_purple);
        swipeArea.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateCommentUi();
            }
        });

        commentList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    swipeArea.setEnabled(true);
                } else {
                    swipeArea.setEnabled(false);
                }
            }
        });
    }

    @OptionsItem(R.id.action_refresh)
    @Background
    void fetchMessage(){
        try{
            message = raycastRESTClient.getMessageById(messageId);
            comments = raycastRESTClient.getComments(messageId);
            updateMessageUi();
        }catch(Exception ex){
            handleException("Não foi possível baixar mensagem :(", ex);
        }
    }

    @Click(R.id.messagedetail_sendcomment)
    @Background
    void sendComment(){
        try{
            Comment comment = new Comment();
            comment.setComment(editComment.getText().toString());
            raycastRESTClient.addComment(messageId, comment);
            comments = raycastRESTClient.getComments(messageId);
            updateCommentUi();
        }catch (Exception ex){
            handleException("Não foi possível enviar comentário :(", ex);
        }
    }

    @UiThread
    void updateMessageUi(){
        msgText.setText(message.getMessage());
        commentListAdapter.bind(comments);
        commentList.setAdapter(commentListAdapter);
    }

    @UiThread
    void updateCommentUi(){
        swipeArea.setRefreshing(true);

        commentListAdapter.bind(comments);
        commentList.setAdapter(commentListAdapter);
        editComment.setText("");

        swipeArea.setRefreshing(false);
    }

    @UiThread
    void handleException(String msg, Exception ex){
        Log.e(TAG, ex.getMessage(), ex);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}