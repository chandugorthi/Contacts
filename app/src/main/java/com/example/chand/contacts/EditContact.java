package com.example.chand.contacts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditContact extends AppCompatActivity {

    DBManager contacts = new DBManager(this);
    private String oldConEmail;
    private String oldConNum;
    private String oldConName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Bundle getData = getIntent().getExtras();
        EditText name = (EditText) findViewById(R.id.nameValue);
        name.setText(getData.getString("conName"));
        oldConName = getData.getString("conName");
        EditText email = (EditText) findViewById(R.id.emailValue);
        email.setText(getData.getString("conEmail"));
        oldConEmail = getData.getString("conEmail");
        EditText contact = (EditText) findViewById(R.id.contactValue);
        contact.setText(getData.getString("conNum"));
        oldConNum = getData.getString("conNum");
    }

    // Update datebase with contact's new information
    public void updateDB(View v){

        Bundle getData = getIntent().getExtras();
        String conName = ((EditText) findViewById(R.id.nameValue)).getText().toString();
        String conEmail = ((EditText) findViewById(R.id.emailValue)).getText().toString();
        String conNum = ((EditText) findViewById(R.id.contactValue)).getText().toString();
        String userEmail = getData.getString("userEmail");

        //Try updating the DB only if any of the previous details of the contact is changed
        if ( conName.equals(oldConName) && conEmail.equals(oldConEmail) && conNum.equals(oldConNum)){
            Toast.makeText(this,"There is no change to be udpated." ,Toast.LENGTH_LONG).show();
        } else if (!contacts.updateCon( conName, conEmail, conNum, userEmail, oldConEmail, oldConNum)){
            Toast.makeText(this,"There is already a contact with same email and phone number.",Toast.LENGTH_LONG).show();
        }
    }

    // Cancel all changes and go back to previous screen
    public void onCancel(View v){

        Intent i = new Intent(EditContact.this, DisplayContacts.class);
        Bundle data = new Bundle();
        Bundle getData = getIntent().getExtras();
        data.putString("email", getData.getString("userEmail"));
        i.putExtras(data);
        startActivity(i);

    }
}
