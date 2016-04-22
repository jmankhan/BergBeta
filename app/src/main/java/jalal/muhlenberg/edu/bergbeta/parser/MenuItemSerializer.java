package jalal.muhlenberg.edu.bergbeta.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import jalal.muhlenberg.edu.bergbeta.db.MenuItem;

/**
 * Created by Jalal on 4/22/2016.
 */
public class MenuItemSerializer implements JsonSerializer<MenuItem> {
    @Override
    public JsonElement serialize(MenuItem src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject obj = new JsonObject();
        obj.addProperty("id", src.getId());
        obj.addProperty("name", src.getName());
        obj.addProperty("daymeal", src.getDaymeal());
        return obj;
    }
}
