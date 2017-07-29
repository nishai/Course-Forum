package com.example.CourseForum;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nishai on 2017/05/06.
 */
public class ViewQuestion extends Activity {
    int Voted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewquestion);

        Bundle extras = getIntent().getExtras();
        ((TextView)findViewById(R.id.qTitle)).setText(extras.getString("title"));
        ((TextView)findViewById(R.id.answeredBy)).setText(extras.getString("user"));
        ((TextView)findViewById(R.id.body)).setText(extras.getString("body"));
        ((TextView)findViewById(R.id.votesText)).setText(Integer.toString(extras.getInt("votes")));
        /*if (extras.getBoolean("answered")){((TextView)findViewById(R.id.body)).setText("ANSWERED");}
        else {((TextView)findViewById(R.id.body)).setText("NOT ANSWERED");}*/

        checkVote();
        listAnswers(extras.getInt("id"), false, -1);
    }

    public void updateVote(View v){
        ContentValues params = new ContentValues();
        params.put("", "");
        Bundle extras = getIntent().getExtras();

        AsyncHTTPPost h = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1354477/updateVote.php?type=" + Integer.toString(Voted)
                + "&userid="+ extras.getInt("userID") + "&qid="+ extras.getInt("id"), params) {
            @Override
            protected void onPostExecute(String output) {
            }
        };

        h.execute();
        checkVote();

        h = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1354477/getVotesQ.php?qid=" + extras.getInt("id"), params) {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray j = new JSONArray(output);
                    int i = j.getJSONObject(0).getInt("Votes");
                    ((TextView) findViewById(R.id.votesText)).setText(Integer.toString(i));
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        h.execute();

        listAnswers(extras.getInt("id"), false, -1);
    }

    public void checkVote(){
        ContentValues params = new ContentValues();
        params.put("", "");
        Bundle extras = getIntent().getExtras();

        AsyncHTTPPost h = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1354477/checkVoteQ.php?userid=" + extras.getInt("userID")
                + "&qid=" + extras.getInt("id"), params) {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray arr = new JSONArray(output);
                    Voted = arr.getJSONObject(0).getInt("Voted");
                    if (Voted==1){
                        ImageView img = (ImageView)findViewById(R.id.votedImage);
                        img.setImageDrawable(getResources().getDrawable(R.drawable.filled));
                    }
                    else {
                        ImageView img = (ImageView)findViewById(R.id.votedImage);
                        img.setImageDrawable(getResources().getDrawable(R.drawable.unfilled));
                    }

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        h.execute();

    }

    @Override
    public void onBackPressed() {
        Bundle extras = getIntent().getExtras();
        Questions.refreshScrollView(Questions.lin, Questions.inflater , extras.getInt("userID"), Questions.rel);
        super.onBackPressed();
    }

    public void listAnswers(int question, Boolean clicked, int aidclicked){
        ContentValues params = new ContentValues();
        params.put("", "");

        AsyncHTTPPost h = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1354477/answerlist.php?qid="
                + Integer.toString(question), params) {
            @Override
            protected void onPostExecute(String output) {
                LinearLayout l = (LinearLayout) findViewById(R.id.answerLinLayout);
                l.removeAllViews();
                Toast.makeText(getBaseContext(), output, Toast.LENGTH_LONG);
                try {
                    JSONArray arr = new JSONArray(output);

                    for (int i = 0; i < arr.length(); i++) {
                        RelativeLayout rl = (RelativeLayout) getLayoutInflater().inflate(R.layout.answerlayout, null);

                        JSONObject j = arr.getJSONObject(i);
                        int aid = j.getInt("Answer_ID");

                        updateAnswerVote((ImageView)rl.findViewById(R.id.votedimg), aid,
                                getResources().getDrawable(R.drawable.filled), getResources().getDrawable(R.drawable.unfilled),
                                clicked, aidclicked, (TextView)rl.findViewById(R.id.voteText));

                        String username = j.getString("User");
                        String answer = j.getString("Answer");
                        int correct = j.getInt("Correct");
                        int votes = j.getInt("Votes");

                        TextView t = ((TextView)rl.findViewById(R.id.answeredBy));
                        t.setText(username);
                        t = ((TextView)rl.findViewById(R.id.fullanswer));
                        t.setText(answer);
                        t = ((TextView)rl.findViewById(R.id.voteText));
                        t.setText(Integer.toString(votes));
                        rl.findViewById(R.id.votedimg).setTag(aid);

                        if (correct == 1){
                            rl.findViewById(R.id.correctimg).setVisibility(View.VISIBLE);
                        } else{
                            rl.findViewById(R.id.correctimg).setVisibility(View.INVISIBLE);
                        }

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

    public void checkAnswerVotes(final ImageView img, int aid, Drawable d1, Drawable d2, TextView t){
        ContentValues params = new ContentValues();
        params.put("", "");
        Bundle extras = getIntent().getExtras();

        AsyncHTTPPost h = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1354477/checkVoteA.php?userid=" + extras.getInt("userID")
                + "&aid=" + aid, params) {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray arr = new JSONArray(output);
                    if (arr.getJSONObject(0).getInt("Voted") == 1){
                        img.setImageDrawable(d1);
                    } else {
                        img.setImageDrawable(d2);
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        h.execute();

        h = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1354477/getVotesA.php?aid=" + aid, params) {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray arr = new JSONArray(output);
                    t.setText(arr.getJSONObject(0).getString("Votes"));
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        h.execute();

    }

    public void updateAnswerVote(final ImageView img, int aid, Drawable d1, Drawable d2, Boolean clicked, int aidclicked, TextView t){
        if (clicked) {
            if (aid == aidclicked) {
                ContentValues params = new ContentValues();
                params.put("", "");
                Bundle extras = getIntent().getExtras();

                AsyncHTTPPost h = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1354477/updateAnswerVote.php?&userid="
                        + extras.getInt("userID") + "&aid=" + aid, params) {
                    @Override
                    protected void onPostExecute(String output) {
                        Toast.makeText(getBaseContext(), output, Toast.LENGTH_SHORT);
                    }
                };
                h.execute(); //Runs query to insert/delete vote
            }
        }
        checkAnswerVotes(img, aid, d1, d2, t);
    }

    public void doList(View v) {
        Bundle extras  = getIntent().getExtras();
        int q = extras.getInt("id");
        listAnswers(q, true, Integer.parseInt(v.getTag().toString()));
    }

    public void postAnswer(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dView = inflater.inflate(R.layout.answerdialog, null);
        final EditText e = (EditText) dView.findViewById(R.id.answerEdit);
        builder.setTitle("Enter your answer");
        builder.setView(dView);

        builder.setPositiveButton("Post answer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ContentValues params = new ContentValues();
                params.put("","");
                Bundle extras = getIntent().getExtras();
                AsyncHTTPPost h = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1354477/newAnswer.php?username="
                        + extras.getInt("userID")+ "&answer=" + e.getText().toString() + "&qid=" + extras.getInt("id"), params) {
                    @Override
                    protected void onPostExecute(String output) {
                    }
                };
                h.execute();
                listAnswers(extras.getInt("id"), false, -1);
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

}