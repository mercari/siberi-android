package com.mercari.siberi.db;

import android.database.sqlite.SQLiteException;
import android.os.Build;

import com.mercari.siberi.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.KITKAT)
public class SiberiSQLStorageTest {

    SiberiSQLStorage siberiStorage;

    @Before
    public void setup(){
        siberiStorage = new SiberiSQLStorage(RuntimeEnvironment.application);
    }

    @After
    public void tearDown(){
        siberiStorage.clear();
        siberiStorage.close();
    }

    @Test(expected = SQLiteException.class)
    public void testUniqueInsert() throws JSONException {
        siberiStorage.insert("test_001_change_button_color", 4, createMetaData());
        siberiStorage.insert("test_001_change_button_color", 2, createMetaData());
    }

    @Test(expected = SQLiteException.class)
    public void testTestNameNonnullInsert() throws JSONException {
        siberiStorage.insert(null, 4, createMetaData());
    }

    private JSONObject createMetaData() throws JSONException {
        String str = "    {\n" +
                "      \"metadata\" : {\n" +
                "        \"text\": \"Share your thought about curry!!\"\n" +
                "      }\n" +
                "    }\n";
        return new JSONObject(str);
    }


}
