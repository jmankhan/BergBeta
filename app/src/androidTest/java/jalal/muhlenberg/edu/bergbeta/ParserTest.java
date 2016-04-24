package jalal.muhlenberg.edu.bergbeta;

import android.test.InstrumentationTestCase;
import android.util.JsonReader;
import android.util.Log;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.RealmObject;
import jalal.muhlenberg.edu.bergbeta.db.MenuItem;
import jalal.muhlenberg.edu.bergbeta.parser.MenuItemSerializer;
import jalal.muhlenberg.edu.bergbeta.parser.Parser;

/**
 * Created by Jalal on 4/21/2016.
 */
public class ParserTest extends InstrumentationTestCase {

    @Test
    public void testParseFromHTML() {
        Scanner s = new Scanner(getInstrumentation().getTargetContext().getResources()
                .openRawResource(R.raw.test_html));
        StringBuilder builder = new StringBuilder();
        while (s.hasNextLine()) {
            builder.append(s.nextLine());
        }

        final ArrayList<MenuItem> menuItems = new ArrayList<>();

        String html = builder.toString();
        Document doc = Jsoup.parse(html);
        Elements days = doc.select(".dayinner");

        final StringBuilder station = new StringBuilder();
        final StringBuilder meal = new StringBuilder();

        for (int i = 0; i < days.size(); i++) {
            //java kinda sucks
            final int finalI = i;
            days.get(i).traverse(new NodeVisitor() {
                @Override
                public void head(Node node, int depth) {
                    if (node.hasAttr("class")) {
                        if (node.attr("class").equalsIgnoreCase("mealname")) {
                            meal.setLength(0);
                            meal.append(((Element) node).text());
                        }
                        else if (node.attr("class").equalsIgnoreCase("station")) {
                            station.setLength(0);
                            station.append(((Element) node).text());
                        }
                        else if (node.attr("class").equalsIgnoreCase("menuitem")
                                && node.nodeName().equalsIgnoreCase("td")) {

                            String text = ((TextNode) node.childNode(0)).text();
                            String id = "";
                            for (Node n : node.childNode(1).childNodes())
                                if (n.nodeName().equalsIgnoreCase("span"))
                                    id = n.attr("onclick");

                            id = id.substring(id.indexOf("\'") + 1, id.lastIndexOf("\'"));
                            MenuItem item = new MenuItem();
                            item.setName(text);
                            item.setId(id);
                            item.setDay(finalI);
                            item.setMeal(meal.toString());
                            menuItems.add(item);
                        }
                    }
                }

                @Override
                public void tail(Node node, int depth) {

                }
            });
        }

        assertNotNull(menuItems);
        assertTrue(menuItems.size() > 500);
        assertNotNull(menuItems.get(0).getName());
        assertNotNull(menuItems.get(0).getId());
        assertNotNull(menuItems.get(0).getDay());
        assertNotNull(menuItems.get(0).getMeal());


    }

    @Test
    public void testParseToJSON() {
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        MenuItem control = new MenuItem();
        control.setName("control");
        control.setId("012345");
        control.setDay(0);

        MenuItem a = new MenuItem();
        a.setId("a000000");
        a.setDay(1);

        MenuItem b = new MenuItem();
        b.setName("b");
        b.setDay(2);

        MenuItem c = new MenuItem();
        c.setName("c");
        c.setId("c000000");

        menuItems.add(control);
        menuItems.add(a);
        menuItems.add(b);
        menuItems.add(c);

        Type type = new TypeToken<ArrayList<MenuItem>>() {
        }.getType();
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
        assertNotNull(gson);

        String json = gson.toJson(menuItems, type);
        assertNotNull(json);
    }

    @Test
    public void testParseFromJSON() {
        Type type = new TypeToken<ArrayList<MenuItem>>() {
        }.getType();
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

        String json = "[{\"name\":\"control\",\"id\":\"012345\",\"daymeal\":0},{\"id\":\"a000000\",\"daymeal\":1},{\"name\":\"b\",\"daymeal\":2},{\"name\":\"c\",\"id\":\"c000000\",\"daymeal\":0}]";
        ArrayList<MenuItem> menuItems = Parser.parseFromJSON(json);
        assertNotNull(menuItems);
        assertEquals(menuItems.size(), 4);
        assertEquals(menuItems.get(0).getName(), "control");
    }

    @Test
    public void testParseNutrition() {
        Scanner s = new Scanner(getInstrumentation().getTargetContext().getResources()
                .openRawResource(R.raw.test_html));
        StringBuilder builder = new StringBuilder();
        while (s.hasNextLine()) {
            builder.append(s.nextLine());
        }

        String id = "0000051871_18435";
        Document doc = Jsoup.parse(builder.toString());
        Elements scripts = doc.select("script");
        String nutrition_script = scripts.get(scripts.size() - 2).toString();

        int serving_size = 0;
        int calories = 1;
        int calfat = 2;
        int fat = 3;
        int sugar = 16;
        int protein = 17;

        final int id_size = 17;
        //regex always looks cool
        //search for all lines of aData, except where it initially gets assigned
        Pattern pattern = Pattern.compile("(?!aData=)aData(.*?);");
        Matcher matcher = pattern.matcher(nutrition_script);
        while(matcher.find()) {
            String line = matcher.group();
            String fid = line.substring(line.indexOf("\'")+1, line.indexOf("\'")+id_size);
            Log.d("ayy", "fid: " + fid);
            if(fid.equalsIgnoreCase(id)) {
                String[] facts = line.substring(line.indexOf("(")+1, line.lastIndexOf(")")).split(",");
                assertEquals("1 Salad (405g)", facts[serving_size]);
                assertEquals("368", facts[calories]);
                assertEquals("183", facts[calfat]);
                assertEquals("20.40", facts[fat]);
                assertEquals("11.70", facts[sugar]);
                assertEquals("18.20" , facts[protein]);
            }
        }
    }
}