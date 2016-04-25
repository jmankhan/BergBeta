package jalal.muhlenberg.edu.bergbeta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import jalal.muhlenberg.edu.bergbeta.db.MenuItem;
import jalal.muhlenberg.edu.bergbeta.db.RealmInstance;
import jalal.muhlenberg.edu.bergbeta.network.NetworkManager;
import jalal.muhlenberg.edu.bergbeta.network.VolleyCallback;
import jalal.muhlenberg.edu.bergbeta.parser.Parser;
import jalal.muhlenberg.edu.bergbeta.ui.DetailsFragment;
import jalal.muhlenberg.edu.bergbeta.ui.MenuItemClickCallback;
import jalal.muhlenberg.edu.bergbeta.ui.MenuItemListFragment;

/**
 * This class maintains the data for each weekly menu. It is responsible for managing and displaying
 * the *current* menu. If it finds that the current menu is outdated, it will request a new one.
 * A manual update feature is also available.
 *
 * This class will not handle Network calls or file management. It will request these changes from
 * other classes.
 */
public class MenuActivity extends AppCompatActivity implements VolleyCallback, MenuItemClickCallback {

    /**
     * This will provide fragments, a memory efficient component, to store and display our data
     * Since the user will likely not view more than 2 or 3 fragments, we will use a PagerAdapter
     * If they end up using more, we should switch to a PagerStateAdapter, which should be almost
     * interchangeable with this object.
     */
    private MenuDayPagerAdapter pagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     * It will handle swiping and will rely on the PagerAdapter to display the data.
     */
    private ViewPager viewPager;

    /**
     * An instance of the wrapper class for the RealmDB object. Will be recreated everytime this
     * activity is recreated
     */
    protected RealmInstance db;

    private ArrayList<MenuItem> menuItems;

    private final String saveFile = "berg_menu.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //setup the default toolbar, use the if != null to avoid a warning
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //create db instance
        initDB();

        //get the menu items needed to be displayed.
        //these may need to be loaded from a network call, which could take time
        //or, we may already have them on file, in which case just load them and display
        if(hasSaveFile()) {
            String json = readSaveFile();
            menuItems = Parser.parseFromJSON(json);

            if(menuItems == null || menuItems.isEmpty()) {
                Log.d(getPackageName(), "error: received an empty menuitem list");
            } else {
                populateViews(menuItems);
            }

        } else {

            //!!!!RETURNS IN VOLLEY CALLBACK onSuccess()!!!!
            NetworkManager manager = new NetworkManager();
            manager.getMenuItemsHTTP(this, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initDB() {
        db = new RealmInstance(this);
    }

    /**
     * Determines if the default save file exists where it should.
     * @return true if the file exists, false if not
     */
    private boolean hasSaveFile() {
        File file = getBaseContext().getFileStreamPath(saveFile);
        return file.exists();

    }
    /**
     * Reads the default save file of the weekly menu from internal storage.
     * @return String of the text file if found. Null if the default file is not found.
     * Will throw an exception if file is not found.
     */
    private String readSaveFile() {
        try {
            FileInputStream in = openFileInput(saveFile);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            //close inputstreamreader, which will also close fileinputstream
            inputStreamReader.close();

            return sb.toString();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Log.d("file read", "failed to open file output stream to berg_menu.txt");

        }
        return null;
    }

    /**
     * Create a save file of this week's menu. just write it as raw html so we can use the same
     * parse method in NetworkManager to
     * @param html
     */
    private void createSaveFile(String html) {
        try {
            FileOutputStream fos = openFileOutput(saveFile, MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(Parser.parseToJSON(menuItems));
            outputStreamWriter.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Upon finding new items, update the local db so we can avoid http calls in the future
     * @param menuItems List of MenuItem received from http call or save file
     * @param db Instance of RealmDB
     */
    private void updateDB(ArrayList<MenuItem> menuItems, RealmInstance db) {
        for(MenuItem item : menuItems) {
            if(!db.exists(item.getId())) {
                db.insert(item);
            }
        }
    }


    private void populateViews(ArrayList<MenuItem> menuItems) {
        long last = System.currentTimeMillis();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pagerAdapter = new MenuDayPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        if(viewPager != null)
            viewPager.setAdapter(pagerAdapter);


    }

    public ArrayList<MenuItem> getMenuItems() {
        return menuItems;
    }

    @Override
    public void onSuccess(String html) {
        menuItems = Parser.parseFromHTML(html);
        updateDB(menuItems, db);

        if(menuItems.isEmpty()) {
            Log.d("menu activity", "error: received an empty menuitem list");
        } else {
            createSaveFile(html);
            populateViews(menuItems);
        }

    }

    @Override
    public void onClick(String id) {
        DetailsFragment details = DetailsFragment.newInstance(id);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.viewpager_container, details);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class MenuDayPagerAdapter extends FragmentPagerAdapter {

        public MenuDayPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MenuItemListFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 21 pages (7 days * 3 meals)
            return 21;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position%3) {
                case 0:
                    return "Breakfast";
                case 1:
                    return "Lunch";
                case 2:
                    return "Dinner";
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
