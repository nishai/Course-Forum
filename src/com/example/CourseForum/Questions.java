package com.example.CourseForum;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.zip.Inflater;

/**
 * Created by Nishai on 2017/05/05.
 */
public class Questions extends Activity {
    public static LayoutInflater inflater;
    public static RelativeLayout rel;
    public static int usrID;
    public static LinearLayout lin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions);


        ((TextView)findViewById(R.id.questionHeader)).setText(
                extras.getString("name") + " " + extras.getString("surname"));
        usrID = extras.getInt("userID");

        inflater = getLayoutInflater();
        rel = (RelativeLayout) findViewById(R.id.qrl);
        lin = (LinearLayout) findViewById(R.id.linLay);

        refreshScrollView(lin, inflater, usrID, rel);
    }

    public static void refreshScrollView(LinearLayout l, LayoutInflater inflater, int userID, RelativeLayout qrl){
        ContentValues params = new ContentValues();
        params.put("", "");

        AsyncHTTPPost h = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1354477/questionlist.php", params) {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray arr = new JSONArray(output);
                    l.removeAllViews();
                    for (int i = 0; i < arr.length(); i++) {
                        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.questionlayout, qrl);
                        JSONObject j = arr.getJSONObject(i);

                        String question = j.getString("Question_Title");
                        int numVotes = j.getInt("Votes");
                        String username = j.getString("User");
                        String answer = j.getInt("Answered") == 1? "Answered" : "Not Answered";
                        String body = j.getString("Question_Body");
                        int id = j.getInt("Question_ID");

                        TextView t = (TextView)rl.findViewById(R.id.questionText);
                        t.setText(question);
                        t = (TextView)rl.findViewById(R.id.votes);
                        t.setText(Integer.toString(numVotes));
                        t = (TextView)rl.findViewById(R.id.userText);
                        t.setText(username);
                        t = (TextView)rl.findViewById(R.id.answered);
                        t.setText(answer);

                        rl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(v.getContext(), ViewQuestion.class);
                                i.putExtra("user", username);
                                i.putExtra("answered", answer);
                                i.putExtra("body", body);
                                i.putExtra("id", id);
                                i.putExtra("votes", numVotes);
                                i.putExtra("title", question);
                                i.putExtra("userID", userID);
                                v.getContext().startActivity(i);
                            }
                        });

                        l.addView(rl);
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        };

        h.execute();
    }

    public void newQuestion(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog, null);
        final EditText title = (EditText) dialogView.findViewById(R.id.qTitle);
        final EditText body = (EditText) dialogView.findViewById(R.id.qBody);

        builder.setTitle("Ask a question");
        builder.setView(dialogView);
        builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                ContentValues params = new ContentValues();
                params.put("", "");

                AsyncHTTPPost asyncHttpPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1354477/newQuestion.php?username=" + usrID + "&title="
                        + title.getText().toString() + "&body=" + body.getText().toString(), params) {
                    @Override
                    protected void onPostExecute(String output) {
                        if (output.charAt(output.length()-1) == '0'){
                            Toast.makeText(getApplicationContext(), "Your question has been posted!", Toast.LENGTH_SHORT).show();
                        }
                        else {Toast.makeText(getApplicationContext(), "Your question was not able to be posted. :(", Toast.LENGTH_SHORT).show();}
                    }
                };
                asyncHttpPost.execute();

                refreshScrollView(lin, inflater, usrID, rel);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog d = builder.create();
        d.show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Do you want to log out?")
                .setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Questions.this.finish();
                    }
                })
                .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}