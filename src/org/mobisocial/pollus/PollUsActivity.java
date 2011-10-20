package org.mobisocial.pollus;

import mobisocial.socialkit.musubi.Musubi;
import android.app.Activity;
import android.os.Bundle;

public class PollUsActivity extends Activity {
    Musubi mMusubi;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mMusubi = Musubi.getInstance(this);
    }
}