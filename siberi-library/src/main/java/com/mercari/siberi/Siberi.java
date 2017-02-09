package com.mercari.siberi;

import android.content.Context;

import com.mercari.siberi.db.SiberiSQLStorage;
import com.mercari.siberi.db.SiberiStorage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Siberi {

    private static SiberiStorage sStorage;
    private static ExecutorService sExecutor = Executors.newSingleThreadExecutor();

    private static Callback callback;

    private Siberi(){}

    public static void setUp(Context context) {
        if (sStorage == null) {
            sStorage = new SiberiSQLStorage(context);
        }
    }

    private static void setCallback(Callback callback) {
        Siberi.callback = callback;
    }

    public static void setUpCustomStorage(SiberiStorage customStorage) {
        if (sStorage == null) {
            sStorage = customStorage;
        }
    }

    public static void setExperimentContents(final JSONArray dataArray){
        checkIfInitialized();
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sStorage.clear();
                for (int i = 0, l = dataArray.length(); i < l; i++) {
                    JSONObject object = dataArray.optJSONObject(i);
                    String test = object.optString("name");
                    int variant = object.optInt("variant");
                    JSONObject metaData = object.optJSONObject("metadata");
                    sStorage.insert(test, variant, metaData);

                    if (callback != null) {
                        callback.hasSetExperimentContents();
                    }
                }
            }
        });
    }

    public static void clearExperimentContent(){
        checkIfInitialized();
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sStorage.clear();
            }
        });
    }

    public static void deleteExperimentContent(final String testName){
        checkIfInitialized();
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sStorage.delete(testName);
            }
        });
    }

    public static void runTest(String testName, ExperimentRunner experimentRunner){
        checkIfInitialized();
        ExperimentContent content = sStorage.select(testName);
        if (content == null) {
            content = new ExperimentContent(testName);
        }
        experimentRunner.run(content);
    }

    private static void checkIfInitialized() {
        if (sStorage == null) {
            throw new IllegalStateException("Siberi is not initialized!!");
        }
    }

    public interface ExperimentRunner {
        void run(ExperimentContent content);
    }

    public interface Callback {
        void hasSetExperimentContents();
    }
}
