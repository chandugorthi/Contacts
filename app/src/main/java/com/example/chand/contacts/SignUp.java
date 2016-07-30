package com.example.chand.contacts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SignUp extends AppCompatActivity {

    private DBManager contactsDB = new DBManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

    }

    public void registerUser(View v){
        String name = ((EditText) findViewById(R.id.name_text)).getText().toString();
        String email = ((EditText) findViewById(R.id.email_text)).getText().toString();
        String phNo = ((EditText) findViewById(R.id.contact_text)).getText().toString();
        String pswd = ((EditText) findViewById(R.id.password_text)).getText().toString();
        EditText cnfmPswdField = (EditText) findViewById(R.id.cnfrm_password_text);
        String cnfmPswd = cnfmPswdField.getText().toString();

        //Regular Expressions for matching email, phone number and password
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Pattern contactPattern = Pattern.compile("^[0-9]{10}");
        Pattern pswdPattern = Pattern.compile("[A-Za-z0-9._!@#%&*+-]{6,}");

        if (name.equals("")){

            // Checking if FullName field is empty
            Toast.makeText(SignUp.this,"Full Name field cannot be empty.", Toast.LENGTH_LONG).show();

        }else if(email.equals("")){

            // Checking if Email field is empty
            Toast.makeText(SignUp.this,"Email field cannot be empty.", Toast.LENGTH_LONG).show();

        }else if(phNo.equals("")){

            // Checking if Phone field is empty
            Toast.makeText(SignUp.this,"Contact Number field cannot be empty.", Toast.LENGTH_LONG).show();

        }else if(pswd.equals("")){

            // Checking if Password field is empty
            Toast.makeText(SignUp.this,"Password field cannot be empty.", Toast.LENGTH_LONG).show();

        }else if(cnfmPswd.equals("")){

            // Checking if confirm password field is empty
            Toast.makeText(SignUp.this,"Confirm Password field cannot be empty.", Toast.LENGTH_LONG).show();

        }else if(!emailPattern.matcher(email).find()){

            // Checking email pattern
            Toast.makeText(SignUp.this,"Email should be of form abc@xyz.com", Toast.LENGTH_LONG).show();

        }else if(!contactPattern.matcher(phNo).find() || phNo.length()!= 10){

            // Checking Phone number pattern
            Toast.makeText(SignUp.this,"Contact Number should be of form 1234567890.", Toast.LENGTH_LONG).show();

        }else if(!pswdPattern.matcher(pswd).find()){

            // Checking password pattern
            Toast.makeText(SignUp.this,"Password should be atleast 6 characters long.", Toast.LENGTH_LONG).show();

        } else if( !cnfmPswd.equals(pswd)){

            // Checking if password and confirm password fields match
            Toast.makeText(SignUp.this,"Passwords dont match.", Toast.LENGTH_LONG).show();
            cnfmPswdField.setText("");
        } else {

            User user = new User();
            user.setFullName(name);
            user.setPhNumber(phNo);
            user.setEmail(email);
            user.setPassword(pswd);

            /*Trying to insert user information to database
            * return value is integer instead of boolean, to accomodate for future purposes if any.
             */
            int ret = contactsDB.insertUser(user);
            if( ret == 0 ){
                //Success
                Intent i = new Intent(SignUp.this, LoginScreen.class);
                startActivity(i);
            } else {
                //Failed to insert
                Toast.makeText(this,"Account exists with this email.",Toast.LENGTH_LONG).show();
            }

        }
    }
}
