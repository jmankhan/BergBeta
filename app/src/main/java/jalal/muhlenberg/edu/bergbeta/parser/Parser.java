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
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.RealmObject;
import jalal.muhlenberg.edu.bergbeta.db.MenuItem;

/**
 * Created by Jalal on 4/21/2016.
 */
public class Parser {

    public static ArrayList<MenuItem> parseFromHTML(String html) {
        final ArrayList<MenuItem> menuItems = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        String title = doc.select(".titlecell").text();
        String date = title.substring(title.indexOf("day")+3);

        final StringBuilder station = new StringBuilder();
        final StringBuilder meal = new StringBuilder();

        Elements days = doc.select(".dayinner");
        for (int i = 0; i < days.size(); i++) {
            final int finalI = i;
            days.get(i).traverse(new NodeVisitor() {
                @Override
                public void head(Node node, int depth) {
                    if(node.hasAttr("class")) {
                        String nodeClass = node.attr("class");
                        if(nodeClass.equalsIgnoreCase("mealname")) {
                            meal.setLength(0);
                            meal.append(((Element) node).text());
                        } else if(nodeClass.equalsIgnoreCase("station")) {
                            station.setLength(0);
                            station.append(((Element) node).text());
                        } else if(nodeClass.equalsIgnoreCase("menuitem")
                                && node.nodeName().equalsIgnoreCase("td")) {
                            String text = ((TextNode) node).text();
                            String id = "";
                            for(Node n:node.childNode(1).childNodes()) {
                                if(n.nodeName().equalsIgnoreCase("span"))
                                    id = n.attr("onclick");
                            }

                            id = id.substring(id.indexOf("\'")+1, id.lastIndexOf("\'"));
                            MenuItem item = new MenuItem();
                            item.setName(text);
                            item.setId(id);
                            item.setMeal(meal.toString());
                            item.setDay(finalI);
                            menuItems.add(item);
                        }
                    }
                }

                @Override
                public void tail(Node node, int depth) {

                }
            });
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

    private void nutritionToMap(String nutritionScript) {
        Map<String, String[]> facts = new HashMap<>();
        int serving_size = 0;
        int calories = 1;
        int calfat = 2;
        int fat = 3;
        int sugar = 16;
        int protein = 17;
        final int id_size = 17;

        Pattern pattern = Pattern.compile("(?!aData=)aData(.*?);");
        Matcher matcher = pattern.matcher(nutritionScript);
        while (matcher.find()) {
            String line = matcher.group();
            String id = line.substring(line.indexOf("\'") + 1, line.indexOf("\'") + id_size);
            String[] nuts = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).split(",");

            String[] f = new String[6];
            f[0] = nuts[serving_size];
            f[1] = nuts[calories];
            f[2] = nuts[calfat];
            f[3] = nuts[fat];
            f[4] = nuts[sugar];
            f[5] = nuts[protein];

            facts.put(id, f);
        }


    }
}
