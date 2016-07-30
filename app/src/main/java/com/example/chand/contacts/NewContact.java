package com.example.chand.contacts;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class NewContact extends Fragment implements View.OnClickListener{

    private DBManager contactsDB;
    private String email;

    public interface UpdateList{
        public void refresh();
    }

    private UpdateList update;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle getData = getActivity().getIntent().getExtras();
        email = getData.getString("email");
        View view = inflater.inflate(R.layout.fragment_new_contact, container, false);
        Button addButton = (Button) view.findViewById(R.id.add_button);
        addButton.setOnClickListener(this);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Activity activity){

        super.onAttach(activity);
        try {
            update= (UpdateList) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Add button functionality");
        }
    }

    public boolean isEmailValid(String email){

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        return emailPattern.matcher(email).find();

    }

    public boolean isPhNoValid(String phNo){

        Pattern contactPattern = Pattern.compile("^[0-9]{10}");
        return contactPattern.matcher(phNo).find() && phNo.length()== 10;

    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.add_button:

                //Add new contact

                EditText nameField = (EditText) getView().findViewById(R.id.contact_name_edit);
                String name = nameField.getText().toString();
                EditText emailField = (EditText) getView().findViewById(R.id.contact_email_edit);
                String conEmail = emailField.getText().toString();
                EditText phNoField = (EditText) getView().findViewById(R.id.contact_phone_edit);
                String phNo = phNoField.getText().toString();

                Contact con = new Contact();
                con.setFullName(name);
                con.setEmail(conEmail);
                con.setPhNumber(phNo);
                if (name.equals("")){
                    Toast.makeText(getActivity().getBaseContext(),"Fullname field cannot be empty", Toast.LENGTH_LONG).show();
                } else if (!isEmailValid(conEmail)){
                    Toast.makeText(getActivity().getBaseContext(),"Email should be of form abc@xyz.com", Toast.LENGTH_LONG).show();
                } else if (!isPhNoValid(phNo)){
                    Toast.makeText(getActivity().getBaseContext(),"Contact Number should be of form 1234567890.", Toast.LENGTH_LONG).show();
                } else {
                    contactsDB = new DBManager(getActivity().getBaseContext());
                    String conName = contactsDB.insertContact(con, email);

                    if (conName != null) {
                        AlertDialog exitMsg = new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_info).setTitle("Info")
                                .setMessage("There is already a contact with same email and phone number: " + conName)
                                .setPositiveButton("Ok", null).show();
                    }
                    FrameLayout layout1 = (FrameLayout) getView().findViewById(R.id.contactFragment);
                    layout1.setVisibility(View.GONE);
                    nameField.setText("");
                    emailField.setText("");
                    phNoField.setText("");
                    update.refresh();
                }
                break;
            case R.id.cancel_button:

                // Cancel addition of a contact

                FrameLayout layout = (FrameLayout) getView().findViewById(R.id.contactFragment);
                layout.setVisibility(View.GONE);
                break;
        }

    }
}
