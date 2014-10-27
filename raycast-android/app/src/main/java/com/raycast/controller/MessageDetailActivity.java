package com.raycast.controller;

import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
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
    @ViewById(R.id.messagedetail_sendcomment) Button sendCommentButton;
    @ViewById(R.id.swipe_area) SwipeRefreshLayout swipeArea;
    @ViewById(R.id.messagedetail_message) TextView msgText;
    @ViewById(R.id.messagedetail_editcomment) EditText editComment;
    @ViewById(R.id.messagedetail_commentlist) ListView commentList;
    @Bean CommentListAdapter commentListAdapter;
    Message message;
    List<Comment> comments;

    @AfterViews
    void afterViews(){
        swipeArea.setColorSchemeResources(android.R.color.holo_purple, android.R.color.holo_purple, android.R.color.holo_purple, android.R.color.holo_purple);
        swipeArea.setEnabled(false);

        fetchMessage();
    }

    @AfterViews
    void setListeners() {
        if (editComment.getText() == null || editComment.getText().toString().length() == 0) {
            sendCommentButton.setEnabled(false);
            sendCommentButton.setVisibility(View.GONE);
        }

        editComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() == null || s.toString().length() == 0) {
                    sendCommentButton.setEnabled(false);
                    sendCommentButton.setVisibility(View.GONE);
                } else {
                    sendCommentButton.setEnabled(true);
                    sendCommentButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OptionsItem(R.id.action_refresh)
    @Background
    void fetchMessage(){
        swipeArea.setRefreshing(true);

        try{
            message = raycastRESTClient.getMessageById(messageId);
            comments = raycastRESTClient.getComments(messageId);
            updateMessageUi();
        }catch(Exception ex){
            handleException("Não foi possível baixar mensagem :(", ex);
            swipeArea.setRefreshing(false);
        }
    }

    @Click(R.id.messagedetail_sendcomment)
    @Background
    void sendComment(){
        swipeArea.setRefreshing(true);

        try{
            Comment comment = new Comment();
            comment.setComment(editComment.getText().toString());
            raycastRESTClient.addComment(messageId, comment);
            comments = raycastRESTClient.getComments(messageId);
            updateCommentUi();
        }catch (Exception ex){
            handleException("Não foi possível enviar comentário :(", ex);
            swipeArea.setRefreshing(false);
        }
    }

    @UiThread
    void updateMessageUi(){
        msgText.setText(message.getMessage());
        commentListAdapter.bind(comments);
        commentList.setAdapter(commentListAdapter);

        swipeArea.setRefreshing(false);
    }

    @UiThread
    void updateCommentUi(){
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