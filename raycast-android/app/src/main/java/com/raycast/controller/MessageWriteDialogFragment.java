package com.raycast.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.raycast.R;
import com.raycast.domain.Message;
import com.raycast.domain.User;
import com.raycast.domain.util.Coordinates;
import com.raycast.domain.util.CustomLocation;
import com.raycast.service.MessageService;

/**
 * Created by Lucas on 13/09/2014.
 */
public class MessageWriteDialogFragment extends DialogFragment {

    public interface MessageWriteDialogListener {
        void onFinishedDialog();
    }

    public static final String ARGUMENT_MYLOCATION = "com.raycast.messagewritedialogfragment.mylocation";
    public static final String ARGUMENT_USERID = "com.raycast.messagewritedialogfragment.userid";

    private Location myLocation;
    private String userId;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        myLocation = args.getParcelable(ARGUMENT_MYLOCATION);
        userId = args.getString(ARGUMENT_USERID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_message_write, null);
        builder.setView(view);
        final Dialog dialog = builder.create();

        EditText messageTxt = (EditText) view.findViewById(R.id.dialogmessagewrite_messagetext);
        messageTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (EditorInfo.IME_ACTION_SEND == actionId) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });
        messageTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        Button sendBtn = (Button) view.findViewById(R.id.dialogmessagewrite_sendbtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        return dialog;
    }

    private void sendMessage(){
        User usr = new User();
        usr.setId(userId);
        Message msg = new Message();
        msg.setAuthor(usr);
        msg.setMessage(((EditText) getDialog().findViewById(R.id.dialogmessagewrite_messagetext)).getText().toString());
        msg.setLocation(CustomLocation.fromLocation(myLocation));
        //TODO make sure dialog can't be dismissable until message is sent
        new HttpRequestTask().execute(msg);
        MessageWriteDialogFragment.this.dismiss();
    }

    private class HttpRequestTask extends AsyncTask<Message, Void, Message> {
        @Override
        protected Message doInBackground(Message... params) {
            return new MessageService().add(params[0]);
        }
        @Override
        protected void onPostExecute(Message message) {
            if (message == null) {
                //TODO properly show an error
                Log.e(getClass().toString(), "couldn't save message");
            }
        }
    }
}