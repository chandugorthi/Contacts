package com.example.chand.contacts;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chand on 7/28/2016.
 */
public class ContactsExtractor {

    // Class for populating the contacts of a user

    public static HashMap<String,ArrayList<String>> getInfo(Context context, String email){

        HashMap<String,ArrayList<String>> contacts = new HashMap<>();

        DBManager db = new DBManager(context);
        contacts.putAll(db.getContacts(email));
        return contacts;

    }

}
