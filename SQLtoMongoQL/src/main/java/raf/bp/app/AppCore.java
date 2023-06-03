package raf.bp.app;

import lombok.Getter;
import lombok.Setter;
import raf.bp.database.Database;
import raf.bp.database.MongoDB;
import raf.bp.gui.MessageHandler;
import raf.bp.gui.table.TableModel;

@Getter
@Setter
public class AppCore {
    /* contains instance of all components */

    private static AppCore instance;
    private Database database;
    private MessageHandler messageHandler;
    private TableModel tableModel;


    private AppCore() {
        database = new MongoDB();
        messageHandler = new MessageHandler();
        tableModel = new TableModel();
    }

    public static AppCore getInstace() {
        if (instance == null) {
            instance = new AppCore();
        }
        return instance;
    }


}
