package com.mobile.otrcapital.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mobile.otrcapital.Models.Broker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jawad on 9/21/2015.
 */
public class BrokerDatabase extends SQLiteOpenHelper
{
    public static final String KEY_MC_NUMBER = "mcNumber";
    public static final String KEY_BROKER_NAME = "brokerName";
    public static final String KEY_PKEY = "pKey";
    public static final int READONLY = 1;
    public static final int WRITEONLY = 2;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "BrokerDB";
    // table names
    private static final String TABLE_BROKER_DB = "table_broker_db";
    private static final String KEY_ID = "_id";

    public BrokerDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_BROKERNAME_DB = "CREATE TABLE " + TABLE_BROKER_DB +  "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_MC_NUMBER + " INTEGER, " +
                KEY_BROKER_NAME + " TEXT," +
                KEY_PKEY + " TEXT" +
                ");";

        db.execSQL(CREATE_BROKERNAME_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BROKER_DB);

        // Create tables again
        onCreate(db);

    }

    public List<Broker> GetNewBrokersList(List<Broker> brokers)
    {
        //first check for duplicate values and empty MCnumbers
        List<Broker> newBrokerList = new ArrayList<Broker>();

        if (brokers.size() > 100)
            newBrokerList = brokers;
        else
        {
            SQLiteDatabase dbRead = this.getReadableDatabase();
            for (Broker b : brokers)
            {
                if (!IsMCNumberExist(b.get_mcnumber(), dbRead)){
                    newBrokerList.add(b);}
            }
            dbRead.close();
        }

        return newBrokerList;
    }

    public SQLiteDatabase OpenDB(int type)
    {
        switch (type)
        {
            case READONLY:
                return this.getReadableDatabase();
            case WRITEONLY:
                return this.getWritableDatabase();
            default:
                return this.getReadableDatabase();

        }

    }

    public void PutBrokerName(SQLiteDatabase db,ContentValues values)
    {
        // Inserting Row
        db.insert(TABLE_BROKER_DB, null, values);

    }

    private List<Broker> RemoveUselessValues(List<Broker> OrigList)
    {
        List<Broker> refinedList = new ArrayList<Broker>();
        Set<Broker> set1 = new HashSet();

        for (Broker b : OrigList)
        {
            if (!set1.contains(b))
            {
                if (b.get_mcnumber() !=null && !b.get_mcnumber().isEmpty()){
                    refinedList.add(b);}
                set1.add(b);
            }
        }
        return refinedList;

    }

    private boolean IsMCNumberExist(String MCNumber,SQLiteDatabase db)
    {
        int count = -1;
        Cursor c = null;

        try {
            String query = "SELECT COUNT(*) FROM "
                    + TABLE_BROKER_DB + " WHERE " + KEY_MC_NUMBER + " = ?";
            c = db.rawQuery(query, new String[] {MCNumber});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            if (c != null) {
                c.close();
            }
            return count > 0;
        }
        catch (IllegalArgumentException e1)
        {
            if (c != null) {
                c.close();
            }
            Log.d(ActivityTags.TAG_LOG,e1.toString());
            return false;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }

    //method for retrieval of brokers
    public List<Broker> GetBrokerList()
    {
        Log.d(ActivityTags.TAG_LOG,"Get broker list");
        // Select All Query
        String query = "SELECT  * FROM " + TABLE_BROKER_DB;
        List<Broker> brokerList = new ArrayList<Broker>();
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            Cursor cursor = db.rawQuery(query, null);

            try
            {
                // looping through all rows and adding to list
                if (cursor.moveToFirst())
                {
                    do
                    {
                        Broker broker = new Broker(cursor.getString(1), cursor.getString(2),cursor.getString(3));
                        brokerList.add(broker);
                    } while (cursor.moveToNext());
                }
            }
            finally
            {
                try { cursor.close(); } catch (Exception ignore) {}
            }
        }
        finally
        {
            try { db.close(); } catch (Exception ignore) {}
        }

        return brokerList;

    }

}
