package com.example.CourseForum;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void login(View v) throws NoSuchAlgorithmException {
        ContentValues params = new ContentValues();
        params.put("", "");
        String enteredPassword = ((EditText)findViewById(R.id.passEdit)).getText().toString();
        String hashedPassword = MD5Hash(enteredPassword);

        AsyncHTTPPost asyncHttpPost = new AsyncHTTPPost(
                "http://lamp.ms.wits.ac.za/~s1354477/login.php?username="+((EditText)findViewById(R.id.userEdit)).getText().toString(), params)
        {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray arr = new JSONArray(output);
                    String name = arr.getJSONObject(0).getString("Name");
                    String sname = arr.getJSONObject(0).getString("Surname");
                    String password = arr.getJSONObject(0).getString("Password");
                    int id = arr.getJSONObject(0).getInt("ID");

                    if (password.equals(hashedPassword)) {
                        Toast.makeText(getBaseContext(), "Login successful :)", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getBaseContext(), Questions.class);
                        i.putExtra("name",name);
                        i.putExtra("surname", sname);
                        i.putExtra("userID", id);
                        startActivity(i);
                    }
                    else {Toast.makeText(getBaseContext(), "Password incorrect :(", Toast.LENGTH_SHORT).show();}
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "user doesn't exist, please create a post one", Toast.LENGTH_SHORT).show();
                }
            }
        };
        asyncHttpPost.execute();
    }

    public void newUserClick(View v){
        Intent i = new Intent(v.getContext(), CreateUser.class);
        startActivity(i);
    }

    public String MD5Hash(String s) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        m.update(s.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        return bigInt.toString(16);
    }

}
