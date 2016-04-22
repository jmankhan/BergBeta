package jalal.muhlenberg.edu.bergbeta.db;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Jalal on 4/20/2016.
 * A wrapper class that holds an instance of the RealmDB. All transaction methods can be used from
 * this class to maintain easy exchange between DBs.
 *
 * Each transaction will return a boolena for the success or failure of the transaction.
 * A transaction may fail if the object passed is not a valid RealmObject, if the wrapper has not
 * been initialized properly, if the db was locked, or other possibilities.
 */
public class RealmInstance {

    Realm db;

    public RealmInstance(Context context) {
        db = Realm.getInstance(context);
    }

    /**
     * Inserts a new MenuItem into the db.
     * @param object The RealmObject to insert (this can only be a MenuItem for now)
     * @return true if the transaction succeeded, false otherwise. Error messages should be printed
     * for common transaction failures.
     */
    public boolean insert(MenuItem object) {
        if(object == null) {
            Log.d("realm transaction", "Insert failed. RealmObject was null.");
            return false;
        } else if(db == null || db.isClosed() || db.isInTransaction()) {
            Log.d("realm transaction", "Insert failed. Realm instance is null, closed, or locked.");
            return false;
        } else {
            db.beginTransaction();
            db.copyToRealm(object);
            db.commitTransaction();
            return true;
        }
    }

    /**
     * TODO: Implement this lmao
     * @param object
     */
    public void delete(MenuItem object) {
    }


    /**
     * Queries database for all items with the same id as the one requested.If there are multiple
     * results, the first one is returned. If there are no matching items in the database,
     * null is returned. This query is synchronous.
     * @param id Name of MenuItem to retrieve
     * @return MenuItem
     */
    public MenuItem getMenuItem(String id) {
        RealmResults<MenuItem> results = db.where(MenuItem.class).findAllSorted(id);

        if(results.isEmpty())
            return null;

        return results.get(0);
    }

    public RealmResults<MenuItem> getAllMenuItems() {
        return db.allObjects(MenuItem.class);
    }

    /**
     * Checks if an already exists in the db
     * @param id  the id (primary key) to identify the object
     * @return true if the object exists, false otherwise
     */
    public boolean exists(String id) {
        RealmQuery<MenuItem> query = db.where(MenuItem.class);
        query.equalTo("id", id);

        RealmResults<MenuItem> results = query.findAll();
        return !results.isEmpty();
    }
}
