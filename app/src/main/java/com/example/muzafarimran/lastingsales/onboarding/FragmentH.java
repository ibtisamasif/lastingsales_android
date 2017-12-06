package com.example.muzafarimran.lastingsales.onboarding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;

public class FragmentH extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button bFinish;
    private TextView tvInviteNow;

    public FragmentH() {
        // Required empty public constructor
    }

    public static FragmentH newInstance(String param1, String param2) {
        FragmentH fragment = new FragmentH();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_h, container, false);
        bFinish = view.findViewById(R.id.bFinish);
        tvInviteNow = view.findViewById(R.id.tvInviteNow);
        tvInviteNow.setMovementMethod(LinkMovementMethod.getInstance());
        tvInviteNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://app.lastingsales.com"));
                startActivity(i);
            }
        });
        bFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login Now
                ((OnBoardingActivity) getActivity()).dataFromFragmentH();
//                startActivity(new Intent(getActivity(), NavigationBottomMainActivity.class));
//                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}