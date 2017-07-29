package com.example.CourseForum;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Nishai on 2017/05/04.
 */
public class CreateUser extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createuser);
    }

    public void create(View v){
        String user = ((EditText)findViewById(R.id.userEdit)).getText().toString();
        String pass = ((EditText)findViewById(R.id.passEdit)).getText().toString();
        String name = ((EditText)findViewById(R.id.nameEdit)).getText().toString();
        String sname = ((EditText)findViewById(R.id.surnameEdit)).getText().toString();

        ContentValues params = new ContentValues();
        params.put("", "");
        String address = "http://lamp.ms.wits.ac.za/~s1354477/create.php?username="+user+"&password="+pass+
                "&name="+name+"&surname="+sname;

        AsyncHTTPPost asyncHttpPost = new AsyncHTTPPost(address,params) {
            @Override
            protected void onPostExecute(String output) {
                if (output.charAt(output.length()-1) == '0'){
                    Toast.makeText(getApplicationContext(), "user successfully created! :)", Toast.LENGTH_SHORT).show();
                }
                else {Toast.makeText(getApplicationContext(), "user was not able to be created. :(", Toast.LENGTH_SHORT).show();}
            }
        };
        asyncHttpPost.execute();
    }
}