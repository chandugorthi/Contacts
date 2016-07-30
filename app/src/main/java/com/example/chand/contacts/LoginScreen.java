package com.example.chand.contacts;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;


/**
 * A login screen that offers login via email/password.
 */
public class LoginScreen extends AppCompatActivity {

   /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    private DBManager contactsDB = new DBManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

    }

    // Go to user registration page
    public void user_signup(View v){

        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        String pswd = ((EditText) findViewById(R.id.password)).getText().toString();
        Intent i = new Intent(LoginScreen.this, SignUp.class);
        Bundle data = new Bundle();
        data.putString("email", email);
        data.putString("pswd", pswd);
        i.putExtras(data);
        startActivity(i);

    }

    // Check the credentials entered by the user
    public void user_login(View v){

        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        String pswd = ((EditText) findViewById(R.id.password)).getText().toString();
        if (!isEmailValid(email)){
            Toast.makeText(this, "Email address should be of format abc@xyz.com", Toast.LENGTH_LONG).show();
        } else if(!isPasswordValid(pswd)){
            Toast.makeText(this, "Password should be atleast 6 characters long", Toast.LENGTH_LONG).show();
        } else {

            UserLoginTask login = new UserLoginTask(email,pswd);
            login.execute();
        }



    }

    // Validate Email
    private boolean isEmailValid(String email) {

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        return emailPattern.matcher(email).find();

    }

    // Validate password
    private boolean isPasswordValid(String password) {

        return password.length() > 5;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            return contactsDB.userLogin(mEmail,mPassword);

        }

        @Override
        protected void onPostExecute(final Integer success) {

            if (success == 1){
                Intent i = new Intent(LoginScreen.this, DisplayContacts.class);
                Bundle data = new Bundle();
                data.putString("email", mEmail);
                i.putExtras(data);
                startActivity(i);
            } else if ( success == 0 ){
                Toast.makeText(LoginScreen.this,"Password Incorrect. Please Try again.", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(LoginScreen.this,"Username and password do not match. Please Try again.", Toast.LENGTH_LONG).show();
            }
        }

    }
}

