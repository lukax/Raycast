package com.raycast.controller;

import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.raycast.R;
import com.raycast.controller.base.RaycastBaseActivity;
import com.raycast.controller.component.CommentListAdapter;
import com.raycast.domain.Comment;
import com.raycast.domain.Message;
import com.raycast.service.base.RaycastRESTClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;

import java.util.List;

@EActivity(R.layout.activity_message_detail)
@OptionsMenu(R.menu.message_detail)
public class MessageDetailActivity extends RaycastBaseActivity {

    public final static String EXTRA_MESSAGEDETAIL_MESSAGEID = "com.raycast.messagedetail.messageid";
    @Extra(EXTRA_MESSAGEDETAIL_MESSAGEID) String messageId;
    @RestService RaycastRESTClient raycastRESTClient;
    @ViewById(R.id.messagedetail_message) TextView msgText;
    @ViewById(R.id.messagedetail_editcomment) EditText editComment;
    @ViewById(R.id.messagedetail_commentlist) ListView commentList;
    @Bean CommentListAdapter commentListAdapter;
    Message message;
    List<Comment> comments;

    @AfterViews
    void afterViews(){
        new GetMessageTask().execute(messageId);
    }

    @OptionsItem(R.id.action_refresh)
    void actionRefresh(){
        new GetMessageTask().execute(messageId);
    }

    @Click(R.id.messagedetail_sendcomment)
    void sendComment(){
        new SaveCommentTask().execute(messageId);
    }


    class GetMessageTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            message = raycastRESTClient.getMessageById(params[0]);
            comments = raycastRESTClient.getComments(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            msgText.setText(message.getMessage());
            commentListAdapter.bind(comments);
            commentList.setAdapter(commentListAdapter);
        }
    }

    class SaveCommentTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            Comment comment = new Comment();
            comment.setComment(editComment.getText().toString());
            raycastRESTClient.addComment(params[0], comment);
            comments = raycastRESTClient.getComments(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            commentListAdapter.bind(comments);
            commentList.setAdapter(commentListAdapter);
        }
    }
}