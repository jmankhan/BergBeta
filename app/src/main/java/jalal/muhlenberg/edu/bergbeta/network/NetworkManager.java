package jalal.muhlenberg.edu.bergbeta.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import jalal.muhlenberg.edu.bergbeta.db.MenuItem;

/**
 * Created by Jalal on 4/20/2016.
 *
 * This class will manage network calls, make sure that data is received and disposed of properly,
 * and serves as a standalone network called that should be able to be replaced by another library
 * if necessary. This manager currently uses Volley internally to make and handle network calls.
 */
public class NetworkManager {

    /**
     * Base url to connect to Dining Services
     */
    private final String url = "http://dining.muhlenberg.edu/WeeklyMenu.htm";

    /**
     * Response from server
     */
    private String html;
    private ArrayList<MenuItem> menuItems = new ArrayList<>();

    /**
     * Default constructor. Instantiates fields to null
     */
    public NetworkManager() {
        html = null;
    }

    /**
     * Helper method that makes the actualy network call. This is internalized to ensure that
     * multiple network calls are properly made and disposed of. The client itself should only
     * receive one response, and should not get old data.
     * @param context Context to return call to
     */
    private void getMenuHTMLHelper(Context context, final VolleyCallback callback) {
        Debug.startMethodTracing("http");
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        html = response;
                        callback.onSuccess(html);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("network manager", "there was an error connecting to the dining server");
            }
        });

        Volley.newRequestQueue(context).add(request);
    }

    /**
     * This wrapper method manages the most recent response from the network. It ensures the client
     * does not receive the same data twice or receive old data.
     * @param context Context to return call to
     * @return String of raw html received
     */
    private String getMenuHTML(Context context, VolleyCallback callback) {
        getMenuHTMLHelper(context, callback);
        if(html == null)
            return null;
        else {
            String copy = html;
            html = null;
            return copy;
        }
    }


    /**
     * Uses helper methods to queue up requests, get a raw html response, and parse the response
     * into an ArrayList of MenuItems for use by the client.
     * @param context The context to perform all operations on
     * @return List of MenuItems, may be empty if something failed.
     */
    public void getMenuItemsHTTP(Context context, VolleyCallback callback) {

        if(html == null) {
            html = getMenuHTML(context, callback);
        }
    }
}
