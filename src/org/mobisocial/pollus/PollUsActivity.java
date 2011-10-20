package org.mobisocial.pollus;

import mobisocial.socialkit.musubi.AppObj;
import mobisocial.socialkit.musubi.Musubi;
import mobisocial.socialkit.musubi.multiplayer.FeedRenderable;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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

        mMusubi = Musubi.getInstance(this);
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

        private final String mQuestion;
        private JSONObject mJson;

        public PollObj(String question) {
            mQuestion = question;
        }

        @Override
        public JSONObject getData() {
            if (mJson == null) {
                mJson = new JSONObject();
                try {
                    mJson.put(FIELD_Q, mQuestion);
                } catch (JSONException e) {}
            }
            return mJson;
        }

        @Override
        public FeedRenderable getRenderable() {
            StringBuilder html = new StringBuilder("<p><em>" + mQuestion + "</em><p>");

            return FeedRenderable.fromHtml(html.toString());
        }
    }
}