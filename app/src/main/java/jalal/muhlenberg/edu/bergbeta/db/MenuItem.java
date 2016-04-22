package jalal.muhlenberg.edu.bergbeta.db;

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
}
