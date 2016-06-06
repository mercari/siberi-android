package com.mercari.siberi.db;


import com.mercari.siberi.ExperimentContent;

import org.json.JSONObject;

public interface SiberiStorage {
    void insert(String testName, int variant, JSONObject metaData);
    ExperimentContent select(String testName);
    void delete(String testName);
    void clear();
}
