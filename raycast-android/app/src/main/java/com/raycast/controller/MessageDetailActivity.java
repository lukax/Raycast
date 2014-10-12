package com.raycast.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.raycast.R;
import com.raycast.controller.base.RaycastBaseActivity;
import com.raycast.domain.Comment;
import com.raycast.domain.Message;
import com.raycast.service.base.RaycastRESTClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.RootContext;
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
        new GetMessageAsyncTask().execute(messageId);
    }

    @OptionsItem(R.id.action_refresh)
    void actionRefresh(){
        new GetMessageAsyncTask().execute(messageId);
    }

    class GetMessageAsyncTask extends AsyncTask<String, Void, Void>{
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
}

@EBean
class CommentListAdapter extends BaseAdapter {
    @RootContext Context context;
    List<Comment> comments;

    public void bind(List<Comment> comments){
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position);
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

@EViewGroup(R.layout.item_comment)
class CommentItemView extends RelativeLayout{
    @ViewById(R.id.author) TextView authorView;
    @ViewById(R.id.comment_text) TextView commentView;
    @ViewById(R.id.profile_image) ImageView profileImageView;

    public CommentItemView(Context context) {
        super(context);
    }

    public void bind(Comment comment){
        authorView.setText(comment.getAuthor().getName());
        commentView.setText(comment.getComment());
        ImageLoader.getInstance().displayImage(comment.getAuthor().getImage(), profileImageView, null, null);
    }
}