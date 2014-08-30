package com.raycast.controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.raycast.R;
import com.raycast.domain.Message;
import com.raycast.service.base.Tracker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageCompact.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageCompact#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MessageCompact extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MESSAGE_COMPACT_ID = "com.raycast.controller.MessageCompact.FRAGMENT_BUNDLE_KEY";

    // TODO: Rename and change types of parameters
    private String messageID;
    private Message thisMessage;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id MessageID.
     * @return A new instance of fragment MessageCompact.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageCompact newInstance(String id) {
        MessageCompact fragment = new MessageCompact();
        Bundle args = new Bundle();
        args.putString(MESSAGE_COMPACT_ID, id);
        fragment.setArguments(args);
        return fragment;
    }
    public MessageCompact() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            messageID = getArguments().getString(MESSAGE_COMPACT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.message_compact, container, false);

        TextView name = (TextView) view.findViewById(R.id.message_creator);
        TextView content = (TextView) view.findViewById(R.id.message_content);
        TextView distance = (TextView) view.findViewById(R.id.message_distance);

        ImageView profileImage = (ImageView) view.findViewById(R.id.profile_image);
        Bitmap userImage = BitmapFactory.decodeByteArray(thisMessage.getAuthor().getImage(), 0,
                thisMessage.getAuthor().getImage().length);

        Tracker tracker = new Tracker(view.getContext());
        Location myLocation = new Location("");

        Location messageLocation = new Location("");
        messageLocation.setLatitude(thisMessage.getLocation().getCoordinates().getLatitude());
        messageLocation.setLongitude(thisMessage.getLocation().getCoordinates().getLongitude());

        //thisMessage = MessageService.getMessageID(messageID);

        name.setText(thisMessage.getAuthor().getName());
        content.setText(thisMessage.getMessage());

        if (tracker.canGetLocation()) {
            myLocation = tracker.getLocation();
        }

        distance.setText(String.valueOf(messageLocation.distanceTo(myLocation)));
        profileImage.setImageBitmap(userImage);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
