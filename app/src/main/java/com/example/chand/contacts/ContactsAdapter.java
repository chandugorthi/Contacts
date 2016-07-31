package com.example.chand.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chand on 7/28/2016.
 */
public class ContactsAdapter extends BaseExpandableListAdapter {

    // Defining a List adapter to display the items in the style we want

    private Context context;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private ArrayList<String> contactDetails = new ArrayList<>();
    private String email;

    public ContactsAdapter(Context context, ArrayList<Contact> contacts, String email){
        this.context = context;
        this.contacts.addAll(contacts);
        //this.contactDetails.addAll(contacts.keySet());
        this.email = email;
    }

    @Override
    public int getGroupCount() {
        return contacts.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 2;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return contacts.get(groupPosition).getFullName();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (childPosition == 1){
            return contacts.get(groupPosition).getEmail();
        } else {
            return contacts.get(groupPosition).getPhNumber();
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        final String contactName = (String) getGroup(groupPosition);
        if ( convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_item, parent, false);
        }

        TextView nameField = (TextView) convertView.findViewById(R.id.contact_item);
        nameField.setTypeface(null, Typeface.BOLD);
        nameField.setText(contactName);
        Button edit = (Button) convertView.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,EditContact.class);
                Bundle data = new Bundle();
                data.putString("conName", contactName);
                data.putString("conEmail",contacts.get(groupPosition).getEmail());
                data.putString("conNum", contacts.get(groupPosition).getPhNumber());
                data.putString("userEmail", email);
                i.putExtras(data);
                context.startActivity(i);
            }
        });
        Button delete = (Button) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog exitMsg = new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete")
                        .setMessage("Are you sure do you want to delete ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DBManager db = new DBManager(context);
                                db.deleteContact(contacts.get(groupPosition).getEmail(), contacts.get(groupPosition).getEmail(), email);
                            }
                        }).setNegativeButton("no", null).show();
            }
        });

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String contactDetail = (String) getChild(groupPosition,childPosition);
        if ( convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_item_child, parent, false);
        }

        if (!isLastChild) {
            TextView detailField1 = (TextView) convertView.findViewById(R.id.contact_child_item_1);
            detailField1.setText("Email: ");
        } else {
            TextView detailField1 = (TextView) convertView.findViewById(R.id.contact_child_item_1);
            detailField1.setText("Phone: ");
        }
        TextView detailField2 = (TextView) convertView.findViewById(R.id.contact_child_item_2);
        detailField2.setText(contactDetail);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
