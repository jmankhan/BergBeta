package jalal.muhlenberg.edu.bergbeta;

import android.test.InstrumentationTestCase;
import android.util.JsonReader;
import android.util.Log;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Scanner;

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
        while(s.hasNextLine()) {
            builder.append(s.nextLine());
        }
        String html = builder.toString();
        ArrayList<MenuItem> menuItems = Parser.parseFromHTML(html);
        assertNotNull(menuItems);
    }

    @Test
    public void testParseToJSON() {
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        MenuItem control = new MenuItem();
        control.setName("control");
        control.setId("012345");
        control.setDaymeal(0);

        MenuItem a = new MenuItem();
        a.setId("a000000");
        a.setDaymeal(1);

        MenuItem b = new MenuItem();
        b.setName("b");
        b.setDaymeal(2);

        MenuItem c = new MenuItem();
        c.setName("c");
        c.setId("c000000");

        menuItems.add(control);
        menuItems.add(a);
        menuItems.add(b);
        menuItems.add(c);

        Type type = new TypeToken<ArrayList<MenuItem>>(){}.getType();
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
        Type type = new TypeToken<ArrayList<MenuItem>>(){}.getType();
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
}