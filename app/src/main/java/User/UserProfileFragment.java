package User;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.malicteam.projectxclient.R;

import java.util.HashMap;

import User.UserProfileViewModel;

public class UserProfileFragment extends Fragment {
    private static final String UID_KEY = "uid";
    private UserProfileViewModel viewModel;

    private HashMap<String,View> _detailsViews;//holds all the texts, pictures and other details of the user;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    private void initDetailsViews(View view){

        _detailsViews = new HashMap<String,View>();
        _detailsViews.put("name",view.findViewById(R.id.userNameDetail));
        _detailsViews.put("mail",view.findViewById(R.id.userMailAdressDetail));
        _detailsViews.put("picture",view.findViewById(R.id.userPictureSrc));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        viewModel.getUser().observe(this, user -> {

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile, container, false);
        initDetailsViews (view);
        return view;

    }
}

//
//public interface OnFragmentInteractionListener {
//    // TODO: Update argument type and name
//    void onFragmentInteraction(Uri uri);
//}

