package jalal.muhlenberg.edu.bergbeta;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jalal.muhlenberg.edu.bergbeta.db.MenuItem;
import jalal.muhlenberg.edu.bergbeta.db.RealmInstance;

@SuppressWarnings("ALL")
public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        String menuitemId = getIntent().getStringExtra(getResources().getString(R.string.menuitem_id));
        RealmInstance db = new RealmInstance(this);
        MenuItem item = db.getMenuItem(menuitemId);

        getSupportActionBar().setTitle(item.getName());
        setupViews(item);
    }

    public void setupViews(MenuItem item) {
        ImageView header = (ImageView) findViewById(R.id.header);
        Picasso.with(this)
                .load(R.drawable.pizza)
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(header);


        ((TextView) findViewById(R.id.calories_view)).setText(item.getCalories());
        ((TextView) findViewById(R.id.fat_calories_view)).setText(item.getFatCalories());
        ((TextView) findViewById(R.id.fat_view)).setText(item.getFat());
        ((TextView) findViewById(R.id.sugar_view)).setText(item.getSugar());
        ((TextView) findViewById(R.id.protein_view)).setText(item.getProtein());


    }
}
