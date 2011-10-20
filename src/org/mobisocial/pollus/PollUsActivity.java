package org.mobisocial.pollus;

import mobisocial.socialkit.musubi.AppObj;
import mobisocial.socialkit.musubi.DbObj;
import mobisocial.socialkit.musubi.Musubi;
import mobisocial.socialkit.musubi.multiplayer.FeedRenderable;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PollUsActivity extends Activity {
    Musubi mMusubi;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.submit).setOnClickListener(mSubmitListener);

        mMusubi = Musubi.getInstance(this, getIntent());
        if (mMusubi.hasObj()) {
            promptForAnswer(mMusubi.getObj());
        }
    }

    private void promptForAnswer(DbObj obj) {
        String question = new PollObj(obj).question;
        new AlertDialog.Builder(this)
            .setTitle("Question")
            .setMessage(question)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PollUsActivity.this.finish();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PollUsActivity.this.finish();
                }
            })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    PollUsActivity.this.finish();
                }
            })
            .create().show();
    }

    private View.OnClickListener mSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String question = ((EditText)findViewById(R.id.poll)).getText().toString();
            PollObj poll = new PollObj(question);
            mMusubi.getFeed().postObj(poll);
            Toast.makeText(PollUsActivity.this, "Asked: " + question, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private class PollObj extends AppObj {
        /** TODO: Publish the spec of pollus.poll **/
        public static final String FIELD_Q = "q";

        private final String question;
        private JSONObject mJson;

        public PollObj(String question) {
            this.question = question;
        }

        public PollObj(DbObj obj) {
            question = obj.getJson().optString(FIELD_Q);
        }

        @Override
        public JSONObject getData() {
            if (mJson == null) {
                mJson = new JSONObject();
                try {
                    mJson.put(FIELD_Q, question);
                } catch (JSONException e) {}
            }
            return mJson;
        }

        @Override
        public FeedRenderable getRenderable() {
            StringBuilder html = new StringBuilder("<p><em>" + question + "</em><p>");

            return FeedRenderable.fromHtml(html.toString());
        }
    }
}