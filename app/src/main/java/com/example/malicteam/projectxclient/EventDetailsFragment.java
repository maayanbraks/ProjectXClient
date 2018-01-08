package com.example.malicteam.projectxclient;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class EventDetailsFragment extends Fragment {

    OnFragmentInteractionListener _listener;
    TextView _dateView;
    String _date;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EventDetailsFragment newInstance(String param1, String param2) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        _dateView = view.findViewById(R.id.details_date_content);
        if(_date != null)
            _dateView.setText(_date);

        //Button
        Button button = (Button)view.findViewById(R.id.button_event_details);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onButtonClick();
            }
        });



        return view;
    }

    public void setDate(String date) {
        _date = date;
        if(_dateView != null)
            _dateView.setText(_date);
    }

    public void setListener(OnFragmentInteractionListener listener){
        _listener = listener;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
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
        void onButtonClick();
    }
}
