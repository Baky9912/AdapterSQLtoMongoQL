package raf.bp.gui;

import lombok.Getter;
import lombok.Setter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

@Getter
@Setter
public class MainFrame extends JFrame {
    private static MainFrame instance = null;
    public JTable jTable;
    public RSyntaxTextArea textArea;
    public JButton runButton;

    private MainFrame() {}

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
            instance.init();
        }
        return instance;
    }

    private void init() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("SQL to MongoQL");
        BorderLayout borderLayout = new BorderLayout(10, 10);
        this.setLayout(borderLayout);

        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        textArea.setCodeFoldingEnabled(true);
        RTextScrollPane sp = new RTextScrollPane(textArea);

        runButton = new JButton("Run");
        runButton.setMaximumSize(new Dimension(100, 40));
        runButton.setSize(50, 20);

        jTable = new JTable();
        jTable.setPreferredScrollableViewportSize(new Dimension(500, 400));
        jTable.setFillsViewportHeight(true);

        JPanel panel = new JPanel();
        BorderLayout borderLayout1 = new BorderLayout(10, 10);
        panel.setLayout(borderLayout1);

        panel.add(sp, BorderLayout.CENTER);
        panel.add(runButton, BorderLayout.SOUTH);

        JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel, new JScrollPane(jTable));

        this.add(splitPane2, BorderLayout.CENTER);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
