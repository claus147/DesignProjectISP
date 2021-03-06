
package com.dp1415.ips;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 3000; //1000 is 1 second

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        /* New Handler to start the Menu-Activity 
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mapIntent = new Intent(Splash.this,MapViewActivity.class);
                Splash.this.startActivity(mapIntent);
                Splash.this.finish();
                //overridePendingTransition(R.anim.fade_out, 0);//doesnt seem to work
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}