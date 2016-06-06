package com.mercari.sample.api;

import android.content.Context;

import com.mercari.sample.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tomoaki on 6/3/16.
 */
public class FakeApi {

    Context context;
    public FakeApi(Context context) {
        this.context = context;
    }

    public JSONObject get(String ... request){
        //Sleep to show that the request is taking time
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            return new JSONObject(readTextFile(context.getResources().openRawResource(R.raw.experiments_response)));
        } catch (JSONException e) {
            return null;
        }
    }

    private String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }
}
