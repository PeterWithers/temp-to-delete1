package nl.mpi.kinnate.ui;

import java.awt.BorderLayout;
import java.awt.geom.Dimension2D;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import nl.mpi.kinnate.transcoder.DiagramTranscoder;
import nl.mpi.kinnate.transcoder.DiagramTranscoder.OutputType;

/**
 * Document : DiagramTranscoderPanel
 * Created on : Apr 19, 2012, 1:04:12 PM
 * Author : Peter Withers
 */
public class DiagramTranscoderPanel extends JPanel {

    DiagramTranscoder diagramTranscoder;
    JSpinner dpiSpinner;
    JComboBox formatComboBox;
    JLabel ouputSizeLabel;

    public DiagramTranscoderPanel(DiagramTranscoder diagramTranscoder) {
        this.setLayout(new BorderLayout());
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.PAGE_AXIS));
        this.add(outerPanel, BorderLayout.PAGE_START);
        this.diagramTranscoder = diagramTranscoder;
        formatComboBox = new JComboBox();
        for (OutputType outputType : OutputType.values()) {
            formatComboBox.addItem(outputType.name());
        }
        formatComboBox.setSelectedItem(this.diagramTranscoder.getOutputType().name());
        outerPanel.add(formatComboBox);
        dpiSpinner = new JSpinner(new SpinnerNumberModel(this.diagramTranscoder.getDpi(), 10, 600, 10));
        JPanel dpiPanel = new JPanel();
        dpiPanel.add(dpiSpinner);
        dpiPanel.add(new JLabel("DPI"));
        outerPanel.add(dpiPanel);
        ouputSizeLabel = new JLabel(getSizeString(), JLabel.CENTER);
        outerPanel.add(ouputSizeLabel);
        final ChangeListener changeListener = new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                updateSettings();
            }
        };
        dpiSpinner.addChangeListener(changeListener);
    }

    protected void updateSettings() {
        diagramTranscoder.setDpi(Integer.parseInt(dpiSpinner.getValue().toString()));
        diagramTranscoder.setOutputType(OutputType.valueOf(formatComboBox.getSelectedItem().toString()));
        ouputSizeLabel.setText(getSizeString());
    }

    private String getSizeString() {
        final Dimension2D currentSize = diagramTranscoder.getCurrentSize();
        return (int) currentSize.getWidth() + " x " + (int) currentSize.getHeight();
    }
}