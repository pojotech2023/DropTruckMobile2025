package com.pojo.droptruck.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.pojo.droptruck.databinding.FragmentHomeBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {


    FragmentHomeBinding fragmentHomeBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view=inflater.inflate(R.layout.fragment_home, container, false);

        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater,container,false);


        return fragmentHomeBinding.getRoot();

    }

}