package com.example.mysplitapp;
import android.os.Bundle;

import com.example.mysplitapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import io.split.android.client.api.Key;
//import com.google.gson.Gson;

import io.split.android.client.SplitClientConfig;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    SplitInstance sdkclient;
    Button b1;
    String apikey = "5pij3l7laepu22kanluaqqln27kpd0ch4aoc";
    // Remote Config keys
    private static final String WELCOME_MESSAGE_KEY = "welcome_message";
    private String TAG = "FirebaseTag";

    // Build SDK configuration by default
    SplitClientConfig config = SplitClientConfig.builder().logLevel(2)
            .build();
    // Create a new user key to be evaluated
    String matchingKey = "gualondo";
    Key k = new Key(matchingKey);
    String treatment = "NA";
    // [START get_remote_config_instance]
    FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    // [END get_remote_config_instance]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


/**
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        // [END enable_dev_mode]
**/
        // [START set_default_values]
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        // [END set_default_values]

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        //String matchingKey = "ash";
        Key k = new Key(matchingKey);
        try {
            Log.d("myTag", "Creating SplitFactory object"+": key is"+k);
            sdkclient= new SplitInstance(apikey, k, getApplicationContext());
        } catch (Exception e) {
            System.out.print("Exception: "+e.getMessage());
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("myTag", "click" + sdkclient.isReady);
                if (sdkclient.isReady==true) {
                    treatment = sdkclient.GetSplitTreatment("first-split-flag");
                    Log.d("myTag", "treatment: "+treatment);
                    displayWelcomeMessage();

                    sdkclient.SendTrackEvent("user", "conversion");
//                        client.Destroy();
                } else Log.d("myTag", "SDK not ready: " + sdkclient.isReady);


                Snackbar.make(view, "Feature flag is "+treatment, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        displayWelcomeMessage();

        fetchRemoteConfigValues();
        
        // Adding the update listener
        firebaseAddUpdateListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void fetchRemoteConfigValues() {
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d("Firebase", "Config params updated: " + updated);
                            Toast.makeText(MainActivity.this, "Fetch and activate succeeded",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainActivity.this, "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        displayWelcomeMessage();
                    }
                });
    }

    private void displayWelcomeMessage() {
        //FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // [START get_config_values]
        String welcomeMessage = mFirebaseRemoteConfig.getString(WELCOME_MESSAGE_KEY);
        // [END get_config_values]
        Toast.makeText(MainActivity.this, welcomeMessage,
                Toast.LENGTH_SHORT).show();
        mFirebaseRemoteConfig.activate();
    }

    private void firebaseAddUpdateListener() {
        mFirebaseRemoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
            @Override
            public void onUpdate(ConfigUpdate configUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.getUpdatedKeys());
                mFirebaseRemoteConfig.activate().addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        displayWelcomeMessage();
                    }
                });
            }

            @Override
            public void onError(FirebaseRemoteConfigException error) {
                Log.w(TAG, "Config update error with code: " + error.getCode(), error);
            }
        });
    }
}