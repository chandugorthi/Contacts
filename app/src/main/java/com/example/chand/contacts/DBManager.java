package com.example.chand.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chand on 7/27/2016.
 */
public class DBManager extends SQLiteOpenHelper {

    SQLiteDatabase db;
    private Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DB_NAME = "contacts.db";

    private static final String TABLE_USERS = "users";
    private static final String USERS_NAME = "Fullname";
    private static final String USERS_EMAIL = "Email";
    private static final String USERS_PHONE_NUM = "Phone";
    private static final String USERS_PASSWORD = "Password";

    private static final String TABLE_CONTACTS = "contacts";
    private static final String CONTACT_NAME = "Fullname";
    private static final String CONTACT_EMAIL = "Email";
    private static final String CONTACT_PHONE_NUM = "Phone";
    private static final String CONTACT_USER = "User";
    private static final String CONTACT_FK_CONSTRAINT = "contact_constraint";

    private static final String TABLE_USERS_CREATE = "CREATE TABLE " + TABLE_USERS + " ( " + USERS_NAME + " text NOT NULL, " + USERS_EMAIL + " text primary key NOT NULL, " + USERS_PHONE_NUM + " text NOT NULL, " + USERS_PASSWORD + " text NOT NULL);";

    private static final String TABLE_CONTACTS_CREATE = "CREATE TABLE " + TABLE_CONTACTS + " ( " + CONTACT_NAME + " text NOT NULL, " + CONTACT_EMAIL + " text NOT NULL, " + CONTACT_PHONE_NUM + " text NOT NULL, " + CONTACT_USER + " text NOT NULL, PRIMARY KEY (" + CONTACT_EMAIL + ", " + CONTACT_PHONE_NUM + "), FOREIGN KEY (" + CONTACT_USER + ") REFERENCES " + TABLE_USERS + "(" + USERS_EMAIL + "));";

    public interface DBUpdateList{
        public void dBRefreshList();
    }

    private DBUpdateList upd;

    public DBManager(Context context){
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_USERS_CREATE);
        db.execSQL(TABLE_CONTACTS_CREATE);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_contacts = "DROP TABLE IF EXISTS " + TABLE_CONTACTS;
        db.execSQL(drop_contacts);
        String drop_users = "DROP TABLE IF EXISTS " + TABLE_USERS;
        db.execSQL(drop_users);
        String drop_fk = "ALTER TABLE DROP FOREIGN KEY " + CONTACT_FK_CONSTRAINT;
        db.execSQL(drop_fk);
        this.onCreate(db);
    }

    // Register a new user
    public int insertUser(User user){

        if (!findUser(user)) {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(USERS_NAME, user.getFullName());
            values.put(USERS_EMAIL, user.getEmail());
            values.put(USERS_PHONE_NUM, user.getPhNumber());
            values.put(USERS_PASSWORD, user.getPassword());

            db.insert(TABLE_USERS, null, values);

            return 0;
        } else {
            return 1;
        }

    }

    //Check if the user is already registered
    public boolean findUser(User user){

        db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + USERS_EMAIL + " = \"" + user.getEmail() + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            if (cursor.getString(0).equals("0")){
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    // Check user login credentials
    public int userLogin(String email, String password){
        db = getReadableDatabase();
        String query = "SELECT " + USERS_PASSWORD + " FROM " + TABLE_USERS + " WHERE " + USERS_EMAIL + " = \"" + email + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            if ( cursor.getString(0).equals(password)){
                cursor.close();
                return 1;
            }
            cursor.close();
            return 0;
        } else {
            cursor.close();
            return -1;
        }
    }

    // Trying to insert a contact for a user and checking if it is already present
    public String insertContact(Contact con, String email){

        db = getReadableDatabase();
        String query = "SELECT " + CONTACT_NAME + " FROM " + TABLE_CONTACTS + " WHERE " + CONTACT_EMAIL + " = \"" + con.getEmail() + "\" AND " + CONTACT_PHONE_NUM + " = \"" + con.getPhNumber() + "\" AND " + CONTACT_USER + " = \"" + email + "\"";
        Cursor cursor = db.rawQuery(query, null);

        String name = null;

        if (cursor.moveToFirst()){

            name = cursor.getString(0);

        }

        if ( name == null ){
            insert(con, email);
        }
        cursor.close();

        return name;

    }

    // Add a new contact for the user
    public void insert(Contact con, String email){

        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CONTACT_NAME, con.getFullName());
        values.put(CONTACT_EMAIL, con.getEmail());
        values.put(CONTACT_PHONE_NUM, con.getPhNumber());
        values.put(CONTACT_USER, email);

        db.insert(TABLE_CONTACTS, null, values);

    }

    //Get all contacts of the current user
    public ArrayList<Contact> getContacts(String email){

        db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + CONTACT_USER + " = \"" + email + "\"";
        Cursor cursor = db.rawQuery(query, null);


        ArrayList<Contact> contacts = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                Contact one = new Contact();
                one.setFullName(cursor.getString(0));
                one.setEmail(cursor.getString(1));
                one.setPhNumber(cursor.getString(2));
                contacts.add(one);
            } while(cursor.moveToNext());
        }
        cursor.close();

        return contacts;
    }

    //Delete Contact info of a user
    public void deleteContact(String conEmail, String conNum, String userEmail){

        db = getWritableDatabase();

        db.delete(TABLE_CONTACTS, CONTACT_EMAIL + " = \"" + conEmail + "\" AND " + CONTACT_PHONE_NUM + " = " + conNum , null);

        upd = (DBUpdateList) context;
        upd.dBRefreshList();
    }

    //Update Contact Information
    public boolean updateCon(String conName, String conEmail, String conNum, String userEmail, String oldConEmail, String oldConNum){

        db = getReadableDatabase();

        // Update only if either the name alone is updated or if no other contact with same email and contact number combination is not found.
        boolean update = false;
        if( conEmail.equals(oldConEmail) && conNum.equals(oldConNum)){
            update = true;
        } else {
            String query = "SELECT " + CONTACT_NAME + " FROM " + TABLE_CONTACTS + " WHERE " + CONTACT_EMAIL + " = \"" + conEmail + "\" AND " + CONTACT_PHONE_NUM + " = \"" + conNum + "\" AND " + CONTACT_USER + " = \"" + userEmail + "\"";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount()==0){
                update = true;
            }
            cursor.close();
        }

        if (update) {
            db = getWritableDatabase();
            /*String query = "UPDATE " + TABLE_CONTACTS + " SET " + CONTACT_EMAIL + " = \"" + conEmail + "\", " + CONTACT_PHONE_NUM + " = " + conNum + " WHERE " + CONTACT_EMAIL + " = \"" + oldConEmail + "\" AND " + CONTACT_PHONE_NUM + " = " + oldConNum + " AND " + CONTACT_USER + " = \"" + userEmail +"\"";
            Cursor cursor = db.rawQuery(query, null);*/

            ContentValues c = new ContentValues();
            c.put(CONTACT_NAME, conName);
            c.put(CONTACT_EMAIL, conEmail);
            c.put(CONTACT_PHONE_NUM, conNum);

            db.update(TABLE_CONTACTS, c, CONTACT_EMAIL + " = \"" + oldConEmail + "\" AND " + CONTACT_PHONE_NUM + " = " + oldConNum + " AND " + CONTACT_USER + " = \"" + userEmail + "\"", null);

            Intent i = new Intent(context, DisplayContacts.class);
            Bundle data = new Bundle();
            data.putString("email", userEmail);
            i.putExtras(data);
            context.startActivity(i);

            return true;
        }

        return false;

    }

}
