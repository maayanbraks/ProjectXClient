package com.example.malicteam.projectxclient.View;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.malicteam.projectxclient.Common.Callbacks.EventDetailCallback;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.R;

import java.util.List;

import ResponsesEntitys.ProtocolLine;

public class EventDetailsFragment extends Fragment {
    private EventDetailsInteraction mListener;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

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
        TextView protocol = view.findViewById(R.id.protocolText);
        if (event.isConverted()) {
            mListener.getProtocol(event.getId(), new EventDetailCallback() {
                @Override
                public void onSuccees(List<ProtocolLine> list) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            protocol.setText(ProductTypeConverters.FromProtocolToString(list));
                        }
                    });

                }
            });
        }

        title.setText(event.getTitle());
        _date.setText(event.getDate());
        String part = "Participats: " + event.getParticipats().toString();
//        for (int num : event.getUsersIds()) {
//            part.concat(Integer.toString(num));
//        }
        //TODO sadsaddsddsasada
        _participates.setText("Participats:" + event.getParticipatsFirstNames());
        desc.setText("Description:" + event.getDescription());
        // Inflate the layout for this fragment

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EventDetailsInteraction) {
            mListener = (EventDetailsInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewEventInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface EventDetailsInteraction {
        void getProtocol(int eventId, final EventDetailCallback callback);
        // TODO: Update argument type and name
    }
}
