package com.mercari.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mercari.sample.R;
import com.mercari.sample.test.ExperimentsList;
import com.mercari.sample.test.ForExperiment;
import com.mercari.siberi.ExperimentContent;
import com.mercari.siberi.Siberi;

import org.json.JSONException;
import org.json.JSONObject;


public class EmailInputActivity extends AppCompatActivity {

    @ForExperiment(ExperimentsList.TEST_002_CHANGE_TEXT)
    TextView textView;

    public static Intent createIntent(Context context){
        Intent intent = new Intent(context, EmailInputActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_input);
        final Button button = (Button) findViewById(R.id.submit_button);
        textView = (TextView) findViewById(R.id.intro_message);
        Siberi.runTest(ExperimentsList.TEST_001_CHANGE_BUTTON_COLOR.getTestName(), new Siberi.ExperimentRunner() {
            @Override
            public void run(ExperimentContent content) {
                switch (content.getVariant()) {
                    case 0:
                    default:
                        // not in this experiment
                        //do nothing
                        break;
                    case 1:
                        // Control Group
                        // Show as-is
                        break;

                    case 2:
                        // Test Group
                        // Change color of button
                        button.setBackgroundColor(ContextCompat.getColor(EmailInputActivity.this,R.color.colorAccent));
                        button.setTextColor(ContextCompat.getColor(EmailInputActivity.this,R.color.colorWhite));
                        break;
                }
            }
        });

        Siberi.runTest(ExperimentsList.TEST_002_CHANGE_TEXT.getTestName(), new Siberi.ExperimentRunner() {
            @Override
            public void run(ExperimentContent content) {
                switch (content.getVariant()) {
                    case 0:
                    default:
                        // not in this experiment
                        //do nothing
                        break;
                    case 1:
                        // Control Group
                        // Show as-is
                        break;

                    case 2:
                        // Test Group
                        // Change text
                        JSONObject object = content.getMetaData();
                        try {
                            String text = object.getString("text");
                            changeText(text);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    @ForExperiment(ExperimentsList.TEST_002_CHANGE_TEXT)
    private void changeText(String text){
        Toast.makeText(this,"Experiment started",Toast.LENGTH_LONG).show();
        if(text != null) {
            textView.setText(text);
        }
    }
}
