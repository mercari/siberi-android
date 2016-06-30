#Siberi for Android [![CircleCI](https://circleci.com/gh/mercari/siberi-android.svg?style=svg)](https://circleci.com/gh/mercari/siberi-android)

Siberi makes it easy to start A/B testing for mobile Android applications.

# Features
- **Start A/B testing on any screen or feature in the app**  
  Store experimental data to a local storage when an app is launched.
  Get experimental data from the storage whenever A/B testing is needed.
- **Annotate test related variables and methods**  
  Avoid unintentionally leaving testing codes in a project.
- **Create customizable interface**  
  Develop test runners and experimental data storage.


#Getting started
##1.Setup
Depend via Gradle:

```
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}

apply plugin: 'android-apt'

dependencies{
    compile 'com.mercari:siberi:{latest-version}'
    apt 'com.mercari:siberi-processor:{latest-version}'
}
```

{latest-version} is now: [ ![Download](https://api.bintray.com/packages/mercari-inc/maven/siberi/images/download.svg) ](https://bintray.com/mercari-inc/maven/siberi/_latestVersion)

##2.Create an experiment list
When creating an experiment list, make sure to:

- **Declare an interface.** If you declare a class, the compilation will fail. The interface class will prevent accidently calling the list in your code.
- Annotate class with `@ExperimentalList`.

```
@ExperimentalList
public interface Experiments {
    String TEST_001 = "test_001_change_button_color";
    String TEST_002 = "test_002_change_text";
}
```

Compile the project and Siberi will generate an utility class for experiments.

```
public class ExperimentsUtil {
  public static final String TEST_001_CHANGE_BUTTON_COLOR = "test_001_change_button_color";

  public static final String TEST_002_CHANGE_TEXT = "test_002_change_text";

  public static String getTestNameParams() {
    String params[] = {TEST_001_CHANGE_BUTTON_COLOR,TEST_002_CHANGE_TEXT};
    return TextUtils.join(",", params);
  }
}
```

##3.Initialize Siberi in your Application class

```
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Siberi.setUp(this);
    }
}
```

##4.Request experiments from your server

When the app is launched, Siberi should request experimental data from your server (Ex. Splash screen). Below is a simple example of how to fetch data and how to store it into Siberi. Please see a [sample project]() for detail.

```
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    MyApi = new MyApi(this);
    LoadingTask loadingTask = new LoadingTask();
    loadingTask.setTaskFinishListener(new LoadingTask.TaskFinishListener() {
        @Override
        public void onTaskFinished() {
            gotoNextActivity();
        }
    });
    loadingTask.execute(ExperimentsUtil.getTestNameParams());
}

...

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
        JSONObject response = MyApi.get(params);
        if(response != null){
            try{
                JSONArray experimentArray = response.getJSONArray("experiment_results");
                // Store experiments
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

```

`getTestNameParams()` method creates a string of experiment name(Ex. `test_001_change_button_color,test_002_change_text`).

Request the experimental data from your server by using this string:

```
https://www.hostname.com/get_experiment?params=test_001_change_button_color,test_002_change_text
```

The response should be rendered as shown below:

```
{
  "experiment_results":[
    {
      "name" : "test_001_change_button_color",
      "variant" : 1,
      "metadata" : {
      }
    },
    {
      "name" : "test_002_change_text",
      "variant" : 2,
      "metadata" : {
        "text": "Share your thought about curry!!"
      }
    }
  ]
}
```

`variant` will allocate each test group to different devices.
`metadata` is used when you want to change the content of an experiment dynamically from your server. It is not required.

##5.Run your experiment
Use `Siberi.runTest` method to start your experiment.


```
Siberi.runTest(ExperimentsList.TEST_002, new ExperimentRunner() {
    @Override
    public void run(ExperimentContent content) {
        switch (content.getVariant()) {
            case 0:
            default:
                // not in this experiment
                // do nothing
                break;
            case 1:
                // Control Group
                // Show as-is
                break;
            case 2:
                // Test Group
                // Do some code changes
                changeText("Awesome text");
                break;
            case 3:
                ...    
        }
    }
});
```

If you have field values or methods that are only required for A/B testing, add `@ForExperiment` annotation so that you won't forget to remove them from your code once the experiment is finished.

```
@ForExperiment(Experiments.TEST_002)
TextView testTv;

@ForExperiment(Experiments.TEST_002)
private void changeText(String text){
    Toast.makeText(this,"Experiment started",Toast.LENGTH_LONG).show();
    if(text != null) {
        testTv.setText(text);
    }
}
```

#Tips
##Sending Logs with Siberi
When you want to send logs each time you start A/B testing, you can avoid a boilerplate code by Overriding `ExperimentRunner`.
For example:

```
public abstract class ExperimentRunnerWithAnalytics implements ExperimentRunner {
    @CallSuper
    @Override
    public void run(ExperimentContent content) {
        if(content.containsVariant()){
            // Send Logs
            SendLog(content.getTestName(),content.getVariant()));
        }
    }
}
```

And in each experiment:

```
Siberi.runTest(ExperimentsList.TEST_002, new ExperimentRunnerWithAnalytics() {
    @Override
    public void run(ExperimentContent content) {
        super.run(content);
    ...  
    }
}    
```

##Using custom storage
If you would like to use a storage other than SQLite(ex. Realm), you can do so by implementing `SiberiStorage` and creating a new storage class.

```
public class SiberiRealmDB implements SiberiStorage {
   ...
}
```

Then, in your Application class, set up your storage.

```
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Siberi.setUp(this);
        Siberi.setUpCustomStorage(new SiberiRealmDB(this));
    }
}
```

#Contribution

Please read the CLA below carefully before submitting your contribution.  

https://www.mercari.com/cla/

#License

Copyright 2014-2016 Mercari, Inc.

Licensed under the MIT License.
