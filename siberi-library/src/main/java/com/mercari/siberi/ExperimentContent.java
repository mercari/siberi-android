package com.mercari.siberi;

import org.json.JSONObject;


public class ExperimentContent {
    int id;
    String testName;
    int variant = -1;  //if variant is -1, variant has not changed.
    JSONObject metaData;
    public ExperimentContent(String testName){
        this.testName = testName;
    }

    public boolean containsVariant() {
        return variant != -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public JSONObject getMetaData() {
        return metaData;
    }

    public void setMetaData(JSONObject metaData) {
        this.metaData = metaData;
    }
}

