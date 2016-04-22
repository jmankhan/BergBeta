package jalal.muhlenberg.edu.bergbeta.parser;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.realm.RealmObject;
import jalal.muhlenberg.edu.bergbeta.db.MenuItem;

/**
 * Created by Jalal on 4/21/2016.
 */
public class Parser {

    public static ArrayList<MenuItem> parseFromHTML(String html) {
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        String title = doc.select(".titlecell").text();
        String date = title.substring(title.indexOf("day")+3);

        Elements days = doc.select(".dayinner");
        for(int i=0; i<days.size(); i++) {
            Elements meals = days.get(i).select(".mealname");
            String station = meals.select(".station").text();

            for(int j=0; j<meals.size(); j++) {
                Elements itemBlocks = days.get(i).select("td[class=menuitem]");
                for(Element itemBlock : itemBlocks) {
                    MenuItem item = new MenuItem();
                    item.setName(itemBlock.text());
                    item.setId(itemBlock.select("input").attr("id"));
                    item.setDaymeal(i+j);
                    item.setStation(station);
                    menuItems.add(item);
                }
            }
        }
        return menuItems;
    }

    public static String parseToJSON(ArrayList<MenuItem> menuItems) {
        return getGSON().toJson(menuItems);
    }

    public static ArrayList<MenuItem> parseFromJSON(String json) {
        return getGSON().fromJson(json, new TypeToken<ArrayList<MenuItem>>(){}.getType()); //hax
    }

    public static void saveFile(Context ctx, String json) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        prefs.putString("saveFile", json);
        prefs.apply();

    }

    public static void saveDate(Context context, String date) {
        String month = date.split(" ")[0];
        String day   = date.split(" ")[1];
        String year  = date.split(" ")[2];

        //i couldn't find a simple solution in 5 minutes of Googling
        switch(month) {
            case "January":     month="01"; break;
            case "February":    month="02"; break;
            case "March":       month="03"; break;
            case "April":       month="04"; break;
            case "May":         month="05"; break;
            case "June":        month="06"; break;
            case "July":        month="07"; break;
            case "August":      month="08"; break;
            case "September":   month="09"; break;
            case "October":     month="10"; break;
            case "November":    month="11"; break;
            case "December":    month="12"; break;
            default: month="12";
        }

        String currentDate = year + "/" + month + "/" + day;

        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putString("lastUpdate", currentDate);
        prefs.apply();
    }

    private static Gson getGSON() {
        Gson gson = null;
        try {
            gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .registerTypeAdapter(Class.forName("io.realm.MenuItemRealmProxy"), new MenuItemSerializer())
                    .create();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        return gson;
    }

}
