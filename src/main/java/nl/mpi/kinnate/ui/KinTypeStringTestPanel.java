package nl.mpi.kinnate.ui;

import nl.mpi.kinnate.svg.GraphPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import nl.mpi.kinnate.kintypestrings.KinTypeStringConverter;
import nl.mpi.kinnate.SavePanel;

/**
 *  Document   : KinTypeStringTestPanel
 *  Created on : Sep 29, 2010, 12:52:01 PM
 *  Author     : Peter Withers
 */
public class KinTypeStringTestPanel extends JPanel implements SavePanel {

    JTextArea kinTypeStringInput;
    GraphPanel graphPanel;
    String defaultString = "This test panel should provide a kin diagram of the kintype strings entered here.\nEnter one string per line.\nEach new line (enter/return key) will update the graph.";

    public KinTypeStringTestPanel() {
        this.setLayout(new BorderLayout());
        graphPanel = new GraphPanel(null);
        kinTypeStringInput = new JTextArea(defaultString);
        kinTypeStringInput.setBorder(javax.swing.BorderFactory.createTitledBorder("Kin Type Strings"));
        this.add(kinTypeStringInput, BorderLayout.PAGE_START);
        this.add(graphPanel, BorderLayout.CENTER);
//        kinTypeStringInput.setForeground(Color.lightGray);
        kinTypeStringInput.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                if (kinTypeStringInput.getText().equals(defaultString)) {
                    kinTypeStringInput.setText("");
                    kinTypeStringInput.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (kinTypeStringInput.getText().length() == 0) {
                    kinTypeStringInput.setText(defaultString);
                    kinTypeStringInput.setForeground(Color.lightGray);
                }
            }
        });
        kinTypeStringInput.addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void keyPressed(KeyEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void keyReleased(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                KinTypeStringConverter graphData = new KinTypeStringConverter();
                graphData.readKinTypes(kinTypeStringInput.getText().split("\n"));
                graphPanel.drawNodes(graphData);
                KinTypeStringTestPanel.this.doLayout();
//                }
            }
        });
    }

    public boolean hasSaveFileName() {
        return graphPanel.hasSaveFileName();
    }

    public boolean requiresSave() {
        return graphPanel.requiresSave();
    }

    public void saveToFile() {
        graphPanel.saveToFile();
    }

    public void saveToFile(File saveFile) {
        graphPanel.saveToFile(saveFile);
    }
}
