package com.raycast.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View v = inflater.inflate(R.layout.dialog_message_write, null);
        builder.setView(v);


        Button sendBtn = (Button) v.findViewById(R.id.messagewrite_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User usr = new User();
                usr.setId(userId);
                Message msg = new Message();
                msg.setAuthor(usr);
                msg.setMessage(((EditText)v.findViewById(R.id.messagewrite_message)).getText().toString());
                msg.setLocation(CustomLocation.fromLocation(myLocation));
                //TODO make sure dialog can't be dismissable until message is sent
                new HttpRequestTask().execute(msg);
                getDialog().dismiss();
            }
        });

        return builder.create();
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