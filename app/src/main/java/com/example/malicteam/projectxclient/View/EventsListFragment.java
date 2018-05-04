package com.example.malicteam.projectxclient.View;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.example.malicteam.projectxclient.Activity.LoginActivity;
import com.example.malicteam.projectxclient.Activity.MainActivity;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.Model.UserDao;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

import java.util.LinkedList;
import java.util.List;

public class EventsListFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private List<Event> eventsList = new LinkedList<>();
    private UserViewModel currentUser = null;
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
        if(getArguments()!= null){
            ListView eventListView = (ListView) view.findViewById(R.id._listOfEvents);
            adapter = new EventAdapter();
            eventListView.setAdapter(adapter);

            this._userId = getArguments().getInt(Consts.USER_ID, Consts.DEFAULT_UID);
            Repository.instance.getEvents(_userId, new CloudManager.CloudCallback<List<Event>>() {
                @Override
                public void onComplete(List<Event> data) {
                    eventsList = data;
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancel() {
                }
            });

            currentUser = ViewModelProviders.of(this).get(UserViewModel.class);
            currentUser.init(_userId, true);
            currentUser.getUser().observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    if (user != null) {
                        //update details
                        _userId = user.getId();
                        refreshList();
                    }
                }
            });
        }
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
        refreshList();
    }

    private void refreshList() {
        Repository.instance.getEvents(_userId, new CloudManager.CloudCallback<List<Event>>() {
            @Override
            public void onComplete(List<Event> data) {
                eventsList = data;
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancel() {
            }
        });
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
        void onFragmentInteraction(Uri uri);
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

                TextView _nameEvent = view.findViewById(R.id._nameEvent);
                TextView _date = view.findViewById(R.id._date);

                _nameEvent.setText(event.getTitle());
                _date.setText(event.getDate());
            }

            return view;
        }
    }
}
