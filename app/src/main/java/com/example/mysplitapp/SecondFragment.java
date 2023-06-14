package com.example.mysplitapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mysplitapp.databinding.FragmentSecondBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private static final String CAN_VIEW_KRATOS = "can_view_kratos";

    // [START get_remote_config_instance]
    FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    // [END get_remote_config_instance]

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
        showImage();
        firebaseAddUpdateListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showImage(){

        boolean canViewKratos = mFirebaseRemoteConfig.getBoolean(CAN_VIEW_KRATOS);
        Log.d("FIREBASE", "Can view kratos: " +  canViewKratos);
        if (!canViewKratos) {
            binding.kratosImage.setVisibility(View.INVISIBLE);
        } else {
            binding.kratosImage.setVisibility(View.VISIBLE);
        }
    }

    private void firebaseAddUpdateListener() {
        mFirebaseRemoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
            @Override
            public void onUpdate(ConfigUpdate configUpdate) {
                Log.d("FIREBASE", "Updated keys: " + configUpdate.getUpdatedKeys());
                configUpdate.getUpdatedKeys();
                mFirebaseRemoteConfig.activate().addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        task.getResult();
                        showImage();
                    }
                });
            }

            @Override
            public void onError(FirebaseRemoteConfigException error) {
                Log.w("FIREBASE", "Config update error with code: " + error.getCode(), error);
            }
        });
    }
}