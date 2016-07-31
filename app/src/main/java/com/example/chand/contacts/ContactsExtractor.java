package com.example.chand.contacts;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chand on 7/28/2016.
 */
public class ContactsExtractor {

    // Class for populating the contacts of a user

    public static ArrayList<Contact> getInfo(Context context, String email){

        ArrayList<Contact> contacts = new ArrayList<>();

        DBManager db = new DBManager(context);
        contacts.addAll(db.getContacts(email));
        return contacts;

    }

}
