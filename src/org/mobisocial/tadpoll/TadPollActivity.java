package org.mobisocial.tadpoll;

import mobisocial.socialkit.Obj;
import mobisocial.socialkit.musubi.AppObj;
import mobisocial.socialkit.musubi.DbFeed;
import mobisocial.socialkit.musubi.DbObj;
import mobisocial.socialkit.musubi.MemObj;
import mobisocial.socialkit.musubi.Musubi;
import mobisocial.socialkit.musubi.multiplayer.FeedRenderable;

import org.json.JSONException;
import org.json.JSONObject;
import org.mobisocial.pollus.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TadPollActivity extends Activity {
    Musubi mMusubi;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.submit).setOnClickListener(mSubmitListener);

        mMusubi = Musubi.getInstance(this, getIntent());
        String selection = "type = ?";
        String[] selectionArgs = new String[] { AppObj.TYPE };
        Cursor c = mMusubi.getObj().getRelatedFeed().query(selection, selectionArgs);
        if (c.getCount() > 0) {
            c.moveToFirst();
            DbObj qObj = mMusubi.objForCursor(c);
            promptForAnswer(qObj);
        }
    }

    private void promptForAnswer(final DbObj questionObj) {
        String question = new PollObj(questionObj).question;
        new AlertDialog.Builder(this)
            .setTitle("Question")
            .setMessage(question)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendAnswer(questionObj, AnswerObj.YES);
                    TadPollActivity.this.finish();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendAnswer(questionObj, AnswerObj.NO);
                    TadPollActivity.this.finish();
                }
            })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // Refresh the view as a hack for consistency issues.
                    DbFeed parentFeed = mMusubi.getObj().getRelatedFeed();
                    parentFeed.postObj(new PollObj(mMusubi.getObj(), parentFeed));
                    TadPollActivity.this.finish();
                }
            })
            .create().show();
    }

    private void sendAnswer(DbObj question, String answer) {
        DbFeed parentFeed = mMusubi.getObj().getRelatedFeed();
        // Record answer
        Obj obj = new AnswerObj(answer);
        parentFeed.postObj(obj);

        // Refresh UI
        parentFeed.postObj(new PollObj(question, parentFeed));
    }

    private View.OnClickListener mSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String question = ((EditText)findViewById(R.id.poll)).getText().toString();
            PollObj poll = new PollObj(question);
            mMusubi.getObj().getRelatedFeed().postObj(poll);
            Toast.makeText(TadPollActivity.this, "Asked: " + question, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private static class AnswerObj extends MemObj {
        static final String TYPE = "rolypoly.answer";
        static final String FIELD_A = "a";

        static final String YES = "YES";
        static final String NO = "NO";

        AnswerObj(String answer) {
            super(TYPE, getJson(answer));
        }

        private static JSONObject getJson(String answer) {
            JSONObject json = new JSONObject();
            try {
                json.put(FIELD_A, answer);
            } catch (JSONException e) {}
            return json;
        }
    }

    private class PollObj extends AppObj {
        public static final String FIELD_Q = "q";
        public static final String FIELD_YESES = "y";
        public static final String FIELD_NOES = "n";

        private final String question;
        private JSONObject mJson;
        private int mYes;
        private int mNo;

        public PollObj(String question) {
            this.question = question;
        }

        public PollObj(DbObj obj) {
            question = obj.getJson().optString(FIELD_Q);
        }

        public PollObj(DbObj questionObj, DbFeed appFeed) {
            question = questionObj.getJson().optString(FIELD_Q);
            Cursor c = appFeed.query("type=?", new String[] { AnswerObj.TYPE } );
            if (c != null && c.moveToFirst()) {
                do  {
                    Obj ansObj = mMusubi.objForCursor(c);
                    String ans = ansObj.getJson().optString(AnswerObj.FIELD_A);
                    if (ans.equals(AnswerObj.YES)) {
                        mYes++;
                    } else if (ans.equals(AnswerObj.NO)) {
                        mNo++;
                    }
                } while (c.moveToNext());
            }   
        }

        @Override
        public JSONObject getData() {
            if (mJson == null) {
                mJson = new JSONObject();
                try {
                    mJson.put(FIELD_Q, question);
                    mJson.put(FIELD_YESES, mYes);
                    mJson.put(FIELD_NOES, mNo);
                } catch (JSONException e) {
                }
            }
            return mJson;
        }

        @Override
        public FeedRenderable getRenderable() {
            StringBuilder html = new StringBuilder(
                    "<div style=\"background-color:#e6e6fa;border:1px solid black; padding:5px;\">"
                    + "<p><em>" + question + "</em><p>");
            html.append("<p><span style='color:green;'>Yes: " + mYes + "</span>");
            html.append("<span style='margin-left:10px;color:red'>No: " + mNo + "</span></p></div>");
            return FeedRenderable.fromHtml(html.toString());
        }
    }
}