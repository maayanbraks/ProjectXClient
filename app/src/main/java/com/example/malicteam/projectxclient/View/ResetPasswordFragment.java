package com.example.malicteam.projectxclient.View;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.R;

public class ResetPasswordFragment extends Fragment {
    private ResetPasswordListener mListener;
    private EditText inputEmail;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ResetPasswordFragment newInstance() {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
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
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        inputEmail = (EditText) view.findViewById(R.id.email);

        initButtons(view);

        // Inflate the layout for this fragment
        return view;
    }


    private void initButtons(View view){
        Button btnReset = (Button) view.findViewById(R.id.btn_reset_password);
        Button btnBack = (Button) view.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBackButtonClick();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MyApp.getContext(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                mListener.sendResetPassword(email);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResetPasswordListener) {
            mListener = (ResetPasswordListener) context;
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


    public interface ResetPasswordListener {
        // TODO: Update argument type and name
        void onBackButtonClick();
        void sendResetPassword(String email);
    }
}
