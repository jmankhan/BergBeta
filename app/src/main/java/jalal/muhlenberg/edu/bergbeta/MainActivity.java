package jalal.muhlenberg.edu.bergbeta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //If first time running app, start the intro activity
        if(isFirstRun()) {
            Intent i = new Intent(this, IntroductionActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, MenuActivity.class);
            startActivity(i);
            finish();
        }
    }


    /**
     * Determines if this activity cycle is the first time the user has opened the app
     * @return true if first time using app, false otherwise
     */
    public boolean isFirstRun() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("firstRun", true);
    }
}
