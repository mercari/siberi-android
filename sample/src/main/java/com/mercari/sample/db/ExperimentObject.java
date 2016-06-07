package com.mercari.sample.db;

import io.realm.RealmObject;

/**
 * Created by tomoaki on 6/6/16.
 */
public class ExperimentObject extends RealmObject {
    public String testName;
    public int variant;
    public String metaData;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public int getVariant() {
        return variant;
    }

    public void setVariant(int variant) {
        this.variant = variant;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }
}
