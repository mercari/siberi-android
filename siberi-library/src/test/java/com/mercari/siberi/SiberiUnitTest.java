package com.mercari.siberi;

import android.os.Build;

import com.mercari.siberi.db.SiberiSQLStorage;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

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

    @Test
    public void testSetExperimentContents() throws InterruptedException, JSONException{
        siberiStorage.clear();
        JSONObject object = createExperimentData();
        Siberi.setExperimentContents(object.optJSONArray("experiment_results"));
        Thread.sleep(500);
        ExperimentContent result = siberiStorage.select("test_001_change_button_color");
        assertThat(result.getTestName(),is("test_001_change_button_color"));
    }

    @Test
    public void testRunTest() throws InterruptedException, JSONException {
        siberiStorage.clear();
        siberiStorage.insert("test_001_change_button_color", 2, createMetaData());

        final ExperimentContent result = new ExperimentContent("test");
        Siberi.runTest("test_001_change_button_color", new Siberi.ExperimentRunner() {
            @Override
            public void run(ExperimentContent content) {
                result.setTestName(content.testName);
                result.setVariant(content.variant);
                result.setMetaData(content.metaData);
            }
        });

        Thread.sleep(500); //wait for run test action to be finished
        Robolectric.flushForegroundThreadScheduler();
        Thread.sleep(500); //wait for applying test result
        assertThat(result.getTestName(),is("test_001_change_button_color"));
        assertThat(result.getVariant(), is(2));

    }

    @Test
    public void testRunTestOnWorkerThread() throws InterruptedException, JSONException {
        siberiStorage.clear();
        siberiStorage.insert("test_002_change_text", 1, createMetaData());

        final ExperimentContent result = new ExperimentContent("test");
        Siberi.runTestOnWorkerThread("test_002_change_text", new Siberi.ExperimentRunner() {
            @Override
            public void run(ExperimentContent content) {
                result.setTestName(content.testName);
                result.setVariant(content.variant);
                result.setMetaData(content.metaData);
            }
        });

        Thread.sleep(500); //wait for applying test result
        assertThat(result.getTestName(),is("test_002_change_text"));
        assertThat(result.getVariant(), is(1));

    }

    @Test
    public void testRunTestOnUiThread() throws InterruptedException, JSONException {
        siberiStorage.clear();
        siberiStorage.insert("test_003_change_value", 4, createMetaData());

        final ExperimentContent result = new ExperimentContent("test");
        Siberi.runTestOnUiThread("test_003_change_value", new Siberi.ExperimentRunner() {
            @Override
            public void run(ExperimentContent content) {
                result.setTestName(content.testName);
                result.setVariant(content.variant);
                result.setMetaData(content.metaData);
            }
        });

        Thread.sleep(500); //wait for applying test result
        assertThat(result.getTestName(),is("test_003_change_value"));
        assertThat(result.getVariant(), is(4));

    }

    @Test
    public void testClearExperimentTest() throws InterruptedException, JSONException {
        siberiStorage.insert("test_001_change_button_color", 2, createMetaData());

        final ExperimentContent result = new ExperimentContent("test");
        Siberi.clearExperimentContent();
        Thread.sleep(500); //wait for clear content task to end

        Siberi.ExperimentRunner runner = new Siberi.ExperimentRunner() {
            @Override
            public void run(ExperimentContent content) {
                result.setTestName(content.testName);
                result.setVariant(content.variant);
                result.setMetaData(content.metaData);
            }
        };

        Siberi.runTest("test_001_change_button_color", runner);
        Thread.sleep(500); //wait for run test action to be finished
        Robolectric.flushForegroundThreadScheduler();
        Thread.sleep(500); //wait for applying test result
        assertThat(result.getTestName(),is("test_001_change_button_color"));
        assertFalse(result.containsVariant());
    }

    @Test
    public void testDeleteExperimentContent() throws InterruptedException, JSONException {
        siberiStorage.clear();
        siberiStorage.insert("test_003_change_value", 4, createMetaData());

        Thread.sleep(500); //wait for applying test result
        Siberi.deleteExperimentContent("test_003_change_value");
        Thread.sleep(500); //wait for applying test result
        final ExperimentContent result = siberiStorage.select("test_003_change_value");

        Thread.sleep(500); //wait for applying test result
        assertThat(result.getTestName(),is("test_003_change_value"));
        assertThat(result.containsVariant(), is(false));
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