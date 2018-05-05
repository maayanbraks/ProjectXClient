package com.example.malicteam.projectxclient.View;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.R;

public class EventDetailsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EventDetailsFragment newInstance() {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        Event event = (Event) getArguments().getSerializable(Consts.SEND_EVENT);

        TextView title = view.findViewById(R.id.event_title);
        TextView _date = view.findViewById(R.id.details_date);
        TextView _participates = view.findViewById(R.id.details_partici);
        TextView desc = view.findViewById(R.id.details_descp);


        title.setText(event.getTitle());
        _date.setText(event.getDate());
        String part = "Participats: ";
//        for (int num : event.getUsersIds()) {
//            part.concat(Integer.toString(num));
//        }
        _participates.setText("part");
        desc.setText("Description:" + event.getDescription());
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
