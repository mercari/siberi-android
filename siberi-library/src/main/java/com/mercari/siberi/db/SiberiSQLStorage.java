package com.mercari.siberi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.mercari.siberi.ExperimentContent;

import org.json.JSONException;
import org.json.JSONObject;

public class SiberiSQLStorage extends SQLiteOpenHelper implements SiberiStorage {
    private static final String DATABASE_NAME = "abtest.db";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_VARIANT = "variant";
    private static final String COLUMN_META = "meta";
    private static final String TABLE_NAME = "tests";
    private final SQLiteDatabase mDb;

    static String getDataBaseName(Context context) {
        return context.getDatabasePath(DATABASE_NAME).getPath();
    }

    public SiberiSQLStorage(Context context) {
        super(context, getDataBaseName(context), null, DATABASE_VERSION);
        mDb = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT UNIQUE NOT NULL," +
                COLUMN_VARIANT + " INTEGER NOT NULL," +
                COLUMN_META + " TEXT" +
                ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void insert(String testName, int variant, JSONObject metaData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, testName);
        contentValues.put(COLUMN_VARIANT, variant);
        if(metaData != null) {
            contentValues.put(COLUMN_META, metaData.toString());
        }
        mDb.insertOrThrow(TABLE_NAME, null, contentValues);
    }

    @Override
    public ExperimentContent select(String testName) {
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_NAME + " = ? limit 1";
        Cursor cursor = mDb.rawQuery(query, new String[]{testName});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        ExperimentContent content = convertFromCursor(cursor);
        cursor.close();

        return content;
    }

    @Override
    public void delete(String testName) {
        String query = "DELETE FROM " + TABLE_NAME +
                " WHERE " + COLUMN_NAME + " = '" + testName + "'";
        mDb.execSQL(query);
    }

    @Override
    public void clear() {
        String query = "DELETE FROM " + TABLE_NAME;
        mDb.execSQL(query);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mDb.close();
    }

    ExperimentContent convertFromCursor(Cursor cursor) {
        cursor.moveToFirst();
        ExperimentContent experimentContent = new ExperimentContent(cursor.getString(1));
        experimentContent.setId(cursor.getInt(0));
        experimentContent.setVariant(cursor.getInt(2));
        try {
            String meta = cursor.getString(3);
            if(!TextUtils.isEmpty(meta)) {
                experimentContent.setMetaData(new JSONObject(meta));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return experimentContent;
    }

}
