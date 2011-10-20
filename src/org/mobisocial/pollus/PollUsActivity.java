package org.mobisocial.pollus;

import mobisocial.socialkit.musubi.Musubi;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

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

        }
    };
}