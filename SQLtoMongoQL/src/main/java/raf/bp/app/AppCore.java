package raf.bp.app;

import lombok.Getter;
import lombok.Setter;
import raf.bp.adapter.AdapterSQLMongoQLExecutor;
import raf.bp.executor.Executor;
import raf.bp.executor.concrete.MongoQLExecutor;
import raf.bp.gui.MessageHandler;
import raf.bp.gui.table.TableModel;
import raf.bp.packager.Packager;
import raf.bp.packager.TablePackager;
import raf.bp.parser.Parser;
import raf.bp.parser.concrete.SQLParser;
import raf.bp.validator.SQLValidator;
import raf.bp.validator.Validator;

@Getter
@Setter
public class AppCore {
    /* contains instance of all components */

    private static AppCore instance;
    private MessageHandler messageHandler;
    private TableModel tableModel;
    private Parser parser;
    private Validator validator;
    private Executor executor;
    private Executor sqlExecutor;
    private Packager packager;


    private AppCore() {
        messageHandler = new MessageHandler();
        tableModel = new TableModel();
        parser = new SQLParser();
        validator = new SQLValidator();
        executor = new MongoQLExecutor();
        sqlExecutor = new AdapterSQLMongoQLExecutor(executor);
        packager = new TablePackager();
    }

    public static AppCore getInstance() {
        if (instance == null) {
            instance = new AppCore();
        }
        return instance;
    }


}
