package com.blubflub.alert.ontrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by user on 11/12/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "data.db";
    private static final String TABLE_NAME = "prating_table";
    private static final String COL_2 = "DATE";
    private static final String COL_3 = "GOAL1";
    private static final String COL_4 = "GOAL2";
    private static final String COL_5 = "GOAL3";
    private static final String COL_6 = "GOAL4";
    private static final String COL_7 = "GOAL5";
    private static final String COL_8 = "GOAL6";
    private static final String COL_9 = "TOTAL_MINUTES";
    private static final String COL_10 = "PRATING";
    private static int DB_VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DATE TEXT, GOAL1 INTEGER, GOAL2 INTEGER, GOAL3 INTEGER, GOAL4 INTEGER, GOAL5 INTEGER, GOAL6 INTEGER, " +
                "TOTAL_MINUTES INTEGER, PRATING INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        DB_VERSION++;
        onCreate(db);
    }

    public void insertData(String date, int goal1, int goal2, int goal3, int goal4, int goal5, int goal6,
                           int totalMin, int pRating)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_2, date);
        cv.put(COL_3, goal1);
        cv.put(COL_4, goal2);
        cv.put(COL_5, goal3);
        cv.put(COL_6, goal4);
        cv.put(COL_7, goal5);
        cv.put(COL_8, goal6);
        cv.put(COL_9, totalMin);
        cv.put(COL_10, pRating);

        db.insert(TABLE_NAME, null, cv);
    }

    public void insertNullInto(int goal)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        switch (goal)
        {
            case 1:
                cv.put(COL_3, (String) null);
                break;
            case 2:
                cv.put(COL_4, (String) null);
                break;
            case 3:
                cv.put(COL_5, (String) null);
                break;
            case 4:
                cv.put(COL_6, (String) null);
                break;
            case 5:
                cv.put(COL_7, (String) null);
                break;
            case 6:
                cv.put(COL_8, (String) null);
                break;
        }

        db.insert(TABLE_NAME, null, cv);
    }

    //returns the avg of all past pratings
    public int getTotalMinuteAverage()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT AVG(" + COL_9 + ") FROM " + TABLE_NAME;

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();
        int i = c.getInt(0);

        return i;
    }

    public int getTotalOfColumn(int goalNum)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        String query;

        switch (goalNum)
        {
            case 1:
                query = "SELECT SUM(" + COL_3 + ") FROM " + TABLE_NAME;
                break;
            case 2:
                query = "SELECT SUM(" + COL_4 + ") FROM " + TABLE_NAME;
                break;
            case 3:
                query = "SELECT SUM(" + COL_5 + ") FROM " + TABLE_NAME;
                break;
            case 4:
                query = "SELECT SUM(" + COL_6 + ") FROM " + TABLE_NAME;
                break;
            case 5:
                query = "SELECT SUM(" + COL_7 + ") FROM " + TABLE_NAME;
                break;
            case 6:
                query = "SELECT SUM(" + COL_8 + ") FROM " + TABLE_NAME;
                break;
            default:
                query = null;
        }

        Cursor c = db.rawQuery(query, null);

        //Add in the movetofirst etc here? see SO
        c.moveToFirst();
        int i = c.getInt(0);

        return i;
    }

    //get latest row
    public int getLatestRow(int goalNum)
    {
        String query;
        SQLiteDatabase db = this.getWritableDatabase();
        int last = 0;

        switch (goalNum)
        {
            case 1:
                query = "SELECT " + COL_3 + " FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 1";
                break;
            case 2:
                query = "SELECT " + COL_4 + " FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 1";
                break;
            case 3:
                query = "SELECT " + COL_5 + " FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 1";
                break;
            case 4:
                query = "SELECT " + COL_6 + " FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 1";
                break;
            case 5:
                query = "SELECT " + COL_7 + " FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 1";
                break;
            case 6:
                query = "SELECT " + COL_8 + " FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 1";
                break;
            default:
                query = null;
        }

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
            last = cursor.getInt(0);

        cursor.close();
        return last;
    }

    /*getAllUsers() will return the list of all users*/
    public ArrayList<Cards> getAllUsers()
    {
        ArrayList<Cards> usersList = new ArrayList<Cards>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Cards user = new Cards(cursor.getString(1), cursor.getString(9)+"%", " Total Time: " +
                        cursor.getString(8) + " Min", cursor.getInt(9));
                usersList.add(user);
            } while (cursor.moveToNext());
        }
        return usersList;
    }


    public void deleteAllInColumn(String columnName)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(columnName, (String) null);
        db.update(TABLE_NAME, values, null, null);
    }

    //returns the avg of all data in param goal
    //returns the avg of all past pratings
    public int getGoalMinuteAverage(int goalNum)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query;

        switch (goalNum)
        {
            case 1:
                query = "SELECT AVG(" + COL_3 + ") FROM " + TABLE_NAME;
                break;
            case 2:
                query = "SELECT AVG(" + COL_4 + ") FROM " + TABLE_NAME;
                break;
            case 3:
                query = "SELECT AVG(" + COL_5 + ") FROM " + TABLE_NAME;
                break;
            case 4:
                query = "SELECT AVG(" + COL_6 + ") FROM " + TABLE_NAME;
                break;
            case 5:
                query = "SELECT AVG(" + COL_7 + ") FROM " + TABLE_NAME;
                break;
            case 6:
                query = "SELECT AVG(" + COL_8 + ") FROM " + TABLE_NAME;
                break;
            default:
                query = null;
        }

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();
        float i = c.getFloat(0);
        int iInt;
        iInt = Math.round(i);

        return iInt;
    }
}
