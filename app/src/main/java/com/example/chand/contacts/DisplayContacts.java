package com.example.chand.contacts;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DisplayContacts extends AppCompatActivity implements NewContact.UpdateList, DBManager.DBUpdateList {

    private String email;
    ArrayList<Contact> contacts = new ArrayList<>();
    ExpandableListView list;
    ContactsAdapter adap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle getData = getIntent().getExtras();
        email = getData.getString("email");
        setContentView(R.layout.activity_display_contacts);
        update("");
        init();
    }

    // Initializing the text listener on search box
    private void init(){
        EditText search = (EditText) findViewById(R.id.search_text);
        if (search != null) {
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    update(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    // Display fragment to add a contact for the user
    public void addNewContact(View v){

        //Create a new instance of fragment and replace existing one
        NewContact newCon = new NewContact();
        Bundle data = new Bundle();
        data.putString("email", email);
        newCon.setArguments(data);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.contactFragment, newCon);
        ft.addToBackStack(null);
        //NewContact con = (NewContact) getFragmentManager().findFragmentById(R.id.contactFragment);
        FrameLayout layout = (FrameLayout) findViewById(R.id.contactFragment);
        layout.setVisibility(View.VISIBLE);
        ft.commit();
    }

    // Update the list to find the contacts containing the string entered in the search box
    // We search using contact name or email or phone number
    // This search case-sensitive
    public void update(String s){

        contacts.clear();
        list = (ExpandableListView) findViewById(R.id.listView);
        contacts.addAll(ContactsExtractor.getInfo(getBaseContext(),email));
        Collections.sort(contacts);

        if (s.equals("")) {
            TextView num = (TextView) findViewById(R.id.numcontacts);
            num.setText( contacts.size() + "/" + contacts.size());
            adap = new ContactsAdapter(this, contacts,email);
        } else {
            ArrayList<Contact> tempCon = new ArrayList<>();
            // Search if string matches contact name or email or phone number
            for (Contact one : contacts){
                if(one.getFullName().contains(s) || one.getEmail().contains(s) || one.getPhNumber().contains(s)){
                    tempCon.add(one);
                }
            }

            TextView num = (TextView) findViewById(R.id.numcontacts);
            num.setText( tempCon.size() + "/" + contacts.size());
            adap = new ContactsAdapter(this,tempCon,email);
        }
        list.setAdapter(adap);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(DisplayContacts.this, EditContact.class);
                Bundle data = new Bundle();
                data.putString("name", contacts.get(position).getFullName());
                data.putString("email", contacts.get(position).getEmail());
                data.putString("num", contacts.get(position).getPhNumber());
                i.putExtras(data);
                startActivity(i);
            }
        });

    }

    // Refresh the list whenever there is an attempt to change the user's contacts info
    @Override
    public void refresh() {
        update("");
    }

    @Override
    public void dBRefreshList() {
        update("");
    }

    @Override
    public void onBackPressed(){
        FrameLayout layout = (FrameLayout) findViewById(R.id.contactFragment);
        if ( layout.getVisibility() == View.VISIBLE ) {
            layout.setVisibility(View.GONE);
        } else {
            AlertDialog exitMsg = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                    .setMessage("Are you sure you want to logout ?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();

                        }
                    }).setNegativeButton("no", null).show();
        }
    }
}
