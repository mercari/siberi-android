package com.mercari.siberi;

import android.os.Build;

import com.mercari.siberi.db.SiberiSQLStorage;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.KITKAT)
public class SiberiUnitTest {

    SiberiSQLStorage siberiStorage;
    @Before
    public void setup(){
        siberiStorage = new SiberiSQLStorage(RuntimeEnvironment.application);
        Siberi.setUpCustomStorage(siberiStorage);
    }

    @After
    public void teardown() {
        resetDB();
    }

    //FIX: test fails when test1SetExperimentContents runs after test3ClearExperimentTest
    @Test
    public void test1SetExperimentContents() throws JSONException{
        siberiStorage.clear();
        JSONObject object = createExperimentData();
        Siberi.setExperimentContents(object.optJSONArray("experiment_results"));
        ExperimentContent result = siberiStorage.select("test_001_change_button_color");
        assertThat(result.getTestName(),is("test_001_change_button_color"));
    }

    @Test
    public void test2RunTest() throws InterruptedException, JSONException {
        siberiStorage.clear();
        siberiStorage.insert("test_001_change_button_color", 2, createMetaData());
        final CountDownLatch latch = new CountDownLatch(1);
        final ExperimentContent result = new ExperimentContent("test");
        Siberi.runTest("test_001_change_button_color", new Siberi.ExperimentRunner() {
            @Override
            public void run(ExperimentContent content) {
                result.setTestName(content.testName);
                result.setVariant(content.variant);
                result.setMetaData(content.metaData);
                latch.countDown();
            }
        });

        latch.await();
        assertThat(result.getTestName(),is("test_001_change_button_color"));
        assertThat(result.getVariant(), is(2));
    }

    @Test
    public void test3ClearExperimentTest() throws InterruptedException, JSONException {
        siberiStorage.insert("test_001_change_button_color", 2, createMetaData());
        final CountDownLatch latch = new CountDownLatch(1);
        final ExperimentContent result = new ExperimentContent("test");
        Siberi.clearExperimentContent();
        Siberi.runTest("test_001_change_button_color", new Siberi.ExperimentRunner() {
            @Override
            public void run(ExperimentContent content) {
                result.setTestName(content.testName);
                result.setVariant(content.variant);
                result.setMetaData(content.metaData);
                latch.countDown();
            }
        });

        latch.await();
        assertThat(result.getTestName(),is("test_001_change_button_color"));
        assertFalse(result.containsVariant());
    }

    private void resetDB(){
        Field storage;
        try {
            storage = Siberi.class.getDeclaredField("sStorage");
            storage.setAccessible(true);
            storage.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Error! "+ e);
        }
        siberiStorage.close();
    }

    private JSONObject createExperimentData() throws JSONException {
        String str = "{\n" +
                "  \"experiment_results\":[\n" +
                "    {\n" +
                "      \"name\" : \"test_001_change_button_color\",\n" +
                "      \"variant\" : 2,\n" +
                "      \"metadata\" : {\n" +
                "\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\" : \"test_002_change_text\",\n" +
                "      \"variant\" : 2,\n" +
                "      \"metadata\" : {\n" +
                "        \"text\": \"Share your thought about curry!!\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        return new JSONObject(str);
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