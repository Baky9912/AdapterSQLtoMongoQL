package raf.bp.app;

import lombok.Getter;
import lombok.Setter;
import raf.bp.gui.MessageHandler;
import raf.bp.gui.table.TableModel;

@Getter
@Setter
public class AppCore {
    /* contains instance of all components */

    private static AppCore instance;
    private MessageHandler messageHandler;
    private TableModel tableModel;


    private AppCore() {
        messageHandler = new MessageHandler();
        tableModel = new TableModel();
    }

    public static AppCore getInstance() {
        if (instance == null) {
            instance = new AppCore();
        }
        return instance;
    }


}
