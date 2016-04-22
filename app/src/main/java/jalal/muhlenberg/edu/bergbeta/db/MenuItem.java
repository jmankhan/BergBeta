package jalal.muhlenberg.edu.bergbeta.db;

import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by Jalal on 4/20/2016.
 */
@RealmClass
public class MenuItem extends RealmObject {

    @PrimaryKey
    private String id;

    private String name;

    private int daymeal;

    private String station;

    private String calories, fatCalories, protein, sugar, fat;

    public MenuItem() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDaymeal() {
        return daymeal;
    }

    public void setDaymeal(int daymeal) {
        this.daymeal = daymeal;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getFatCalories() {
        return fatCalories;
    }

    public void setFatCalories(String fatCalories) {
        this.fatCalories = fatCalories;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getSugar() {
        return sugar;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

}
