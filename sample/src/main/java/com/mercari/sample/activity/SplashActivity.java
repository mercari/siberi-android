package com.mercari.sample.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mercari.sample.R;
import com.mercari.sample.api.FakeApi;
import com.mercari.sample.test.ExperimentsUtil;
import com.mercari.siberi.Siberi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "Siberi";
    private static FakeApi fakeApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fakeApi = new FakeApi(this);

        LoadingTask loadingTask = new LoadingTask();
        loadingTask.setTaskFinishListener(new LoadingTask.TaskFinishListener() {
            @Override
            public void onTaskFinished() {
                gotoNextActivity();
            }
        });
        Log.i(TAG, ExperimentsUtil.getTestNameParams());
        loadingTask.execute(ExperimentsUtil.getTestNameParams());
    }

    public void gotoNextActivity(){
        startActivity(EmailInputActivity.createIntent(this));
    }


    private static class LoadingTask extends AsyncTask<String,Void,Void> {

        public interface TaskFinishListener {
            void onTaskFinished();
        }
        TaskFinishListener taskFinishListener;

        void setTaskFinishListener(TaskFinishListener taskFinishListener){
            this.taskFinishListener = taskFinishListener;
        }

        @Override
        protected Void doInBackground(String... params) {
            JSONObject response = null;
            if(fakeApi != null){
                response = fakeApi.get(params);
            }

            if(response != null){
                try{
                    JSONArray experimentArray = response.getJSONArray("experiment_results");
                    Siberi.setExperimentContents(experimentArray);
                } catch (JSONException e){
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(taskFinishListener != null){
                taskFinishListener.onTaskFinished();
            }
        }
    }

}
