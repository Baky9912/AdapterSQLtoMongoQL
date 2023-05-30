package raf.bp.app;

import lombok.Getter;
import lombok.Setter;
import raf.bp.database.Database;
import raf.bp.database.MongoDB;

@Getter
@Setter
public class AppCore {
    /* contains instance of all components */

    private static AppCore instance;
    private Database database;


    private AppCore() {
        database = new MongoDB();
    }

    public static AppCore getInstace() {
        if (instance == null) {
            instance = new AppCore();
        }
        return instance;
    }


}
