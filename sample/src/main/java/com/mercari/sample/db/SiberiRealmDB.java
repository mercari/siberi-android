package com.mercari.sample.db;

import android.content.Context;

import com.mercari.siberi.ExperimentContent;
import com.mercari.siberi.db.SiberiStorage;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by tomoaki on 6/6/16.
 */
public class SiberiRealmDB implements SiberiStorage {

    private static RealmConfiguration configuration;
    private Realm realm;

    public SiberiRealmDB(Context context){
        configuration = new RealmConfiguration.Builder(context).build();
    }
    @Override
    public void insert(final String testName, final int variant, final JSONObject metaData) {
        try {
            realm = Realm.getInstance(configuration);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    ExperimentObject experimentObject = realm.createObject(ExperimentObject.class);
                    experimentObject.setTestName(testName);
                    experimentObject.setVariant(variant);
                    experimentObject.setMetaData(metaData.toString());
                }
            });
        } finally {
            realm.close();
        }
    }

    @Override
    public ExperimentContent select(String testName) {
        try {
            realm = Realm.getInstance(configuration);
            RealmResults<ExperimentObject> results = realm.where(ExperimentObject.class).equalTo("testName", testName).findAll();
            if (results.size() > 0) {
                ExperimentObject experimentObject = results.get(0);
                ExperimentContent experimentContent = new ExperimentContent(testName);
                experimentContent.setVariant(experimentObject.getVariant());
                try {
                    experimentContent.setMetaData(new JSONObject(experimentObject.getMetaData()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return experimentContent;
            } else {
                return null;
            }
        } finally {
            realm.close();
        }
    }

    @Override
    public void delete(String testName) {
        try {
            realm = Realm.getInstance(configuration);
            final RealmResults<ExperimentObject> results = realm.where(ExperimentObject.class).equalTo("testName", testName).findAll();
            if (results.size() > 0) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        results.get(0).deleteFromRealm();
                    }
                });
            }
        } finally {
            realm.close();
        }
    }

    @Override
    public void clear() {
        realm = Realm.getInstance(configuration);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(ExperimentObject.class);
            }
        });
        realm.close();

    }
}
