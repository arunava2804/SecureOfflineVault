package com.arunava.securevault.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.arunava.securevault.model.VaultCredential;
import java.util.ArrayList;
import java.util.List;

public class VaultDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "secure_vault.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_VAULT = "vault_items";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_ACCOUNT = "account_name";
    private static final String COL_SECRET = "encrypted_secret";
    private static final String COL_CATEGORY = "category";

    public VaultDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_VAULT + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_ACCOUNT + " TEXT, " +
                COL_SECRET + " TEXT, " +
                COL_CATEGORY + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VAULT);
        onCreate(db);
    }

    public long insertCredential(String title, String account, String encryptedSecret, String category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_ACCOUNT, account);
        cv.put(COL_SECRET, encryptedSecret);
        cv.put(COL_CATEGORY, category);
        return db.insert(TABLE_VAULT, null, cv);
    }

    public List<VaultCredential> getAllCredentials() {
        List<VaultCredential> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_VAULT + " ORDER BY " + COL_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                String account = cursor.getString(cursor.getColumnIndexOrThrow(COL_ACCOUNT));
                String secret = cursor.getString(cursor.getColumnIndexOrThrow(COL_SECRET));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY));
                list.add(new VaultCredential(id, title, account, secret, category));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteCredential(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_VAULT, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
