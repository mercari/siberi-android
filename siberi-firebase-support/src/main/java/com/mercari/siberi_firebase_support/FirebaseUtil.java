package com.mercari.siberi_firebase_support;

import android.text.TextUtils;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * Utility class for Firebase
 */
public class FirebaseUtil {

    public static final String NAME = "name";
    public static final String VARIANT = "variant";
    public static final String VAL = "val";
    public static final String METADATA = "metadata";

    /**
     *
     * @param remoteConfig
     * @param testList
     * @return JSONArray of experiment data
     */
    public static JSONArray convertFirebaseDataToJsonArray(FirebaseRemoteConfig remoteConfig, List<String> testList){
        JSONArray array = new JSONArray();
        Iterator iterator = testList.iterator();
        while (iterator.hasNext()) {
            JSONObject object = new JSONObject();
            String testName = (String) iterator.next();
            try {
                object.put(NAME, testName);
                String testValue = remoteConfig.getString(testName);
                if (TextUtils.isEmpty(testValue)) {
                    object.put(VARIANT, -1);
                } else if (TextUtils.isDigitsOnly(testValue)) {
                    object.put(VARIANT, testValue);
                } else {
                    JSONObject meta = new JSONObject();
                    object.put(VARIANT, -1);
                    meta.put(VAL, testValue);
                    object.put(METADATA, meta.toString());
                }
            } catch (JSONException e) {
            }
            array.put(object);
        }
        return array;
    }
}
