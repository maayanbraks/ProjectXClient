package com.example.malicteam.projectxclient.View;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Callbacks.EventListCallback;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EventsListFragment extends Fragment {

    public interface EventListListener {
        void onEventSelected(Event event);

        void initConvertedObserver(Observer<Integer> observer);

        void initEventsList(Observer<List<Event>> observer);
    }

    private EventListListener mListener;
    private List<Event> eventsList = new LinkedList<>();
    //    private UserViewModel currentUser = null;
    private int _userId;
    private EventAdapter adapter;

    public EventsListFragment() {
        // Required empty public constructor
    }

    public static EventsListFragment newInstance() {
        EventsListFragment fragment = new EventsListFragment();
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
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);
        if (getArguments() != null) {
            ListView eventListView = (ListView) view.findViewById(R.id._listOfEvents);
            adapter = new EventAdapter();
            eventListView.setAdapter(adapter);

            this._userId = getArguments().getInt(Consts.USER_ID, Consts.DEFAULT_UID);

//            //get events list
//            mListener.initEventsList(new Observer<List<Event>>() {
//                @Override
//                public void onChanged(List<Event> data) {
//                    eventsList = data;
//                    refreshList();
//                }
//            });

            Repository.instance.getEventsFromServer(new EventListCallback<List<Event>>() {
                @Override
                public void onSuccees(List<Event> data) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (data != null) {
                                eventsList = data;
                                Collections.reverse(eventsList);
                                if (adapter != null)
                                    adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

                @Override
                public void UserIsNotExist() {
                    //    Toast.makeText(MyApp.getContext(), "User not exist,try again.", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "In getEventFromServer->EventListFragment --->UserIsNotExist");
                }

                @Override
                public void userMustToLogin() {

//                    Toast.makeText(MyApp.getContext(), "You must log in first", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "In getEventFromServer->EventListFragment --->userMustToLogin");
                }
            });

            mListener.initConvertedObserver(new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer integer) {
                    for (Event e : eventsList) {
                        if(e.getId() == integer)
                            e.setConverted(true);
                    }
                }
            });
        }
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EventListListener) {
            mListener = (EventListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewEventInteraction");
        }

        _userId = getArguments().getInt(Consts.USER_ID);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Repository.instance.getEventsFromServer(new EventListCallback<List<Event>>() {
            @Override
            public void onSuccees(List<Event> data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (data != null) {
                            eventsList = data;
                            Collections.reverse(eventsList);
                            if (adapter != null)
                                adapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void UserIsNotExist() {
                //    Toast.makeText(MyApp.getContext(), "User not exist,try again.", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "In getEventFromServer->EventListFragment --->UserIsNotExist");
            }

            @Override
            public void userMustToLogin() {

//                    Toast.makeText(MyApp.getContext(), "You must log in first", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "In getEventFromServer->EventListFragment --->userMustToLogin");
            }
        });
        refreshList();
    }

    private void refreshList() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });

        } catch (Exception e) {
            Log.d("REFRESH_LIST_TAG", e.getMessage());
        }
    }


    //Adapter class - uses eventList
    public class EventAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return eventsList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.records_list_row, viewGroup, false);
            }
            if (i < eventsList.size()) {
                Event event = eventsList.get(i);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onEventSelected(event);
                    }
                });
                TextView _nameEvent = view.findViewById(R.id._nameEvent);
                TextView _date = view.findViewById(R.id._date);
                TextView _participats = view.findViewById(R.id._participates);
                TextView _status = view.findViewById(R.id.recordStatus_textView);
                _nameEvent.setText(event.getTitle());
                _participats.setText(ProductTypeConverters.GenerateStringFromList(ProductTypeConverters.GenerateListUserToListMails(event.getParticipats())));
                _date.setText(event.getDate());
                if (event.isConverted()) {
                    _status.setText("Done");
                } else if (!event.isRecording()) {
                    _status.setText("Analyzing");
                } else {
                    _status.setText("Live");
                }
            }

            return view;
        }

    }

}
