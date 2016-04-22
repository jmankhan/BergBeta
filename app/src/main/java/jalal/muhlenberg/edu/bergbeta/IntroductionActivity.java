package jalal.muhlenberg.edu.bergbeta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class IntroductionActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);

        super.onCreate(savedInstanceState);

        setSkipEnabled(true);
        setFinishEnabled(true);

        addSlide(new SimpleSlide.Builder()
                .title("Title 1")
                .description("This is a test")
                .image(R.drawable.muhlenberg_logo)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Title 2")
                .description("This is a second test")
                .image(R.drawable.muhlenberg_logo)
                .background(R.color.mi_button_background)
                .backgroundDark(R.color.mi_text_color_primary_dark)
                .build());
    }


    @Override
    public void finish() {
        super.finish();

        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        prefs.putBoolean("firstRun", false);
        prefs.apply();

        Intent i = new Intent(this, MenuActivity.class);
        startActivity(i);
    }
}
