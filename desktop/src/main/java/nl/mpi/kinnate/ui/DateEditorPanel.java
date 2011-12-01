package nl.mpi.kinnate.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import nl.mpi.arbil.ui.GuiHelper;
import nl.mpi.kinnate.data.KinTreeNode;
import nl.mpi.kinnate.kindata.EntityData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *  Document   : SvgElementEditor
 *  Created on : Aug 17, 2011, 1:17:13 PM
 *  Author     : Peter Withers
 */
public class DateEditorPanel extends JPanel {

    JPanel outerPanel;

    public DateEditorPanel() {
        this.setLayout(new BorderLayout());
        outerPanel = new JPanel(new GridLayout(0, 1));
        JPanel sideWrapperPanel = new JPanel(new BorderLayout());
        sideWrapperPanel.add(outerPanel, BorderLayout.PAGE_START);
        this.add(new JScrollPane(sideWrapperPanel));
    }

    private JPanel getDateSpinners(EntityData entityData) {
        JPanel rowPanel = new JPanel(new FlowLayout());
        KinTreeNode kinTreeNode = new KinTreeNode(entityData, null);
        rowPanel.add(new JLabel(kinTreeNode.toString(), kinTreeNode.getIcon(), JLabel.LEFT));
        SpinnerModel startDateModel;
        if (entityData.getDateOfBirth() != null) {
            startDateModel = new SpinnerDateModel(entityData.getDateOfBirth(), null, entityData.getDateOfDeath(), Calendar.YEAR);
        } else {
            startDateModel = new SpinnerDateModel();
        }
        JSpinner startSpinner = new JSpinner(startDateModel);
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "yyyy/MM/dd"));
        rowPanel.add(startSpinner);
        startSpinner.setEnabled(entityData.getDateOfBirth() != null);
        JCheckBox startCheckBox = new JCheckBox();
        startCheckBox.setSelected(entityData.getDateOfBirth() != null);
        rowPanel.add(startCheckBox);
        return rowPanel;
    }

    public void setEntities(ArrayList<EntityData> selectedEntities) {
        outerPanel.removeAll();
        for (EntityData entityData : selectedEntities) {
            outerPanel.add(getDateSpinners(entityData), BorderLayout.PAGE_START);
        }
    }

    private void addDeleteButton(final Element svgElement, JPanel sidePanel) {
        final Node parentElement = svgElement.getParentNode();
        final JButton unDeleteButton = new JButton("Undelete");
        final JButton deleteButton = new JButton("Delete");
        sidePanel.add(unDeleteButton);
        unDeleteButton.setEnabled(false);
        unDeleteButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reAddElement(parentElement, svgElement);
                unDeleteButton.setEnabled(false);
                deleteButton.setEnabled(true);
            }
        });
        sidePanel.add(deleteButton);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeElement(parentElement, svgElement);
                unDeleteButton.setEnabled(true);
                deleteButton.setEnabled(false);
            }
        });
    }

    private void addNumberSpinner(final Element svgElement, JPanel sidePanel, String labelString, final String attributeString, int minValue, int maxValue) {
        int initialValue = 0;
        try {
            final String initialValueString = svgElement.getAttribute(attributeString).trim();
            if (initialValueString.length() > 0) {
                initialValue = Integer.decode(initialValueString);
            }
        } catch (NumberFormatException exception) {
            GuiHelper.linorgBugCatcher.logError(exception);
        }
        sidePanel.add(new JLabel(labelString));
        SpinnerModel spinnerModel =
                new SpinnerNumberModel(initialValue, minValue, maxValue, 1);
        final JSpinner numberSpinner = new JSpinner(spinnerModel);
        numberSpinner.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                updateValue(svgElement, attributeString, numberSpinner.getValue().toString());
            }
        });
        sidePanel.add(numberSpinner);
    }

    private void addColourInput(final Element svgElement, JPanel sidePanel, final JPanel pickerPanel, final String attributeString) {
        Color initialColour = Color.white;
        try {
            final String attributeValue = svgElement.getAttribute(attributeString).trim();
            if (!attributeValue.equals("none")) {
                initialColour = Color.decode(attributeValue);
            }
        } catch (NumberFormatException exception) {
            GuiHelper.linorgBugCatcher.logError(exception);
        }
        sidePanel.add(new JLabel(attributeString));
        final JPanel colourSquare = new JPanel();
        colourSquare.setBackground(initialColour);
        colourSquare.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                pickerPanel.removeAll();
                final JColorChooser colourChooser = new JColorChooser(colourSquare.getBackground());
                final Color revertColour = colourSquare.getBackground();
                final JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
                final JButton cancelButton = new JButton("Cancel");
                buttonPanel.add(cancelButton);
                final JButton revertButton = new JButton("Revert");
                buttonPanel.add(revertButton);
                final JButton noneButton = new JButton("None (Transparent)");
                buttonPanel.add(noneButton);
                final JButton okButton = new JButton("OK");
                buttonPanel.add(okButton);
                cancelButton.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        colourSquare.setBackground(revertColour);
                        colourChooser.setColor(revertColour);
                        pickerPanel.removeAll();
                        revalidate();
                        repaint();
                    }
                });
                revertButton.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        colourSquare.setBackground(revertColour);
                        colourChooser.setColor(revertColour);
                        updateValue(svgElement, attributeString, "#" + Integer.toHexString(colourChooser.getColor().getRGB()).substring(2));
                    }
                });
                noneButton.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        updateValue(svgElement, attributeString, "none");
                        colourSquare.setBackground(Color.WHITE);
//                        colourChooser.setColor(Color.WHITE);
                    }
                });
                okButton.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        pickerPanel.removeAll();
                        revalidate();
                        repaint();
                    }
                });

                colourChooser.setPreviewPanel(new JPanel());
                colourChooser.getSelectionModel().addChangeListener(new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        colourSquare.setBackground(colourChooser.getColor());
                        updateValue(svgElement, attributeString, "#" + Integer.toHexString(colourChooser.getColor().getRGB()).substring(2));
                    }
                });
                pickerPanel.add(colourChooser.getChooserPanels()[0], BorderLayout.CENTER);
                pickerPanel.add(buttonPanel, BorderLayout.LINE_END);
                revalidate();
                repaint();
            }
        });
        sidePanel.add(colourSquare);
    }

    protected void updateValue(final Element changeTarget, final String attributeName, final String changeValue) {
    }

    protected void updateValue(final Element changeTarget, final String changeValue) {
    }

    protected void removeElement(final Node parentTarget, final Element changeTarget) {
    }

    protected void reAddElement(final Node parentTarget, final Element changeTarget) {
    }
}