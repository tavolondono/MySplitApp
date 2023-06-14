package com.example.mysplitapp;

import android.content.Context;
import android.util.Log;

import java.util.Map;
import java.util.logging.Logger;

import io.split.android.client.SplitClient;
import io.split.android.client.SplitClientConfig;
import io.split.android.client.SplitFactory;
import io.split.android.client.SplitFactoryBuilder;
import io.split.android.client.SplitResult;
import io.split.android.client.api.Key;
import io.split.android.client.events.SplitEvent;
import io.split.android.client.events.SplitEventTask;

public class SplitInstance {
    public SplitClient client;
    boolean isReady=false;
    SplitInstance(String apikey, Key userId, Context appContext) throws Exception {
        SplitClientConfig config = SplitClientConfig.builder()
                .featuresRefreshRate(60)
                .segmentsRefreshRate(60)
                .impressionsRefreshRate(30)
                .eventFlushInterval(30)
                .build();
        try {
            SplitFactory splitFactory = SplitFactoryBuilder.build(apikey, userId, config, appContext);
            this.client = splitFactory.client();
            this.client.isReady();
            this.client.on(SplitEvent.SDK_READY, new SplitEventTask() {
                @Override
                public void onPostExecutionView(SplitClient client) {
                    isReady = true;
                }
            });
            this.client.on(SplitEvent.SDK_READY_FROM_CACHE, new SplitEventTask() {
                @Override
                public void onPostExecutionView(SplitClient client) {
                    isReady = true;
                }
            });
            this.client.on(SplitEvent.SDK_UPDATE, new SplitEventTask() {
                @Override
                public void onPostExecutionView(SplitClient client) {
                    Log.d("debug", "SDK Updated: Value change to - " + client.getTreatment("first-split-flag"));
                    isReady = true;
                }
            });
            this.client.on(SplitEvent.SDK_READY_TIMED_OUT, new SplitEventTask() {
                @Override
                public void onPostExecutionView(SplitClient client) {
                    isReady = true;
                }
            });
        } catch (Exception e) {
            System.out.print("Exception: "+e.getMessage());
        }
    }

    String GetSplitTreatment(String splitName, Map<String, Object> attributes) {
        return this.client.getTreatment(splitName, attributes);
    }
    String GetSplitTreatment(String splitName) {
        return this.client.getTreatment(splitName);
    }

    SplitResult GetSplitTreatmentWithConfig(String splitName, Map<String, Object> attributes) {
        return this.client.getTreatmentWithConfig(splitName, attributes);
    }

    boolean SendTrackEvent(String trackType, String metricName, double metricValue) {
        return this.client.track(trackType, metricName,metricValue);
    }
    boolean SendTrackEvent(String trackType, String metricName) {
        return this.client.track(trackType, metricName);
    }
}
