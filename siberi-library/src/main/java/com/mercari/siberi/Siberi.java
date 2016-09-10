package com.mercari.siberi;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.mercari.siberi.db.SiberiSQLStorage;
import com.mercari.siberi.db.SiberiStorage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Siberi {

    private static SiberiStorage sStorage;
    private static ExecutorService sExecutor = Executors.newSingleThreadExecutor();

    private Siberi(){}

    public static void setUp(Context context) {
        if (sStorage == null) {
            sStorage = new SiberiSQLStorage(context);
        }
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
                synchronized (sStorage) {
                    sStorage.clear();
                    for (int i = 0, l = dataArray.length(); i < l; i++) {
                        JSONObject object = dataArray.optJSONObject(i);
                        String test = object.optString("name");
                        int variant = object.optInt("variant");
                        JSONObject metaData = object.optJSONObject("metadata");
                        sStorage.insert(test, variant, metaData);
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

    /**
     * Run Test
     * A test is executed on the thread where this method is called
     * @param testName
     * @param experimentRunner
     */
    public static void runTest(final String testName, final ExperimentRunner experimentRunner){
        checkIfInitialized();
        final Handler handler = new Handler();
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final ExperimentContent content = sStorage.select(testName);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        experimentRunner.run(content);
                    }
                });
            }
        });
    }

    /**
     * Run Test asynchronously
     * A test is executed on a worker thread.
     * @param testName
     * @param experimentRunner
     */
    @WorkerThread
    public static void runTestOnWorkerThread(final String testName, final ExperimentRunner experimentRunner){
        checkIfInitialized();
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final ExperimentContent content = sStorage.select(testName);
                experimentRunner.run(content);
            }
        });
    }

    /**
     * Run Test explicitly executed on a Ui thread
     * @param testName
     * @param experimentRunner
     */
    @UiThread
    public static void runTestOnUiThread(final String testName, final ExperimentRunner experimentRunner){
        checkIfInitialized();
        final ExperimentContent content = sStorage.select(testName);
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

}
