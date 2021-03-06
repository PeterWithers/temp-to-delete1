/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package nl.mpi.kinnate.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import nl.mpi.arbil.ui.ArbilWindowManager;
import nl.mpi.arbil.userstorage.SessionStorage;
import nl.mpi.kinnate.entityindexer.EntityCollection;
import nl.mpi.kinnate.export.EntityUploader;

/**
 *  Document   : UploadWindow
 *  Created on : Jun 29, 2011, 2:08:34 PM
 *  Author     : Peter Withers
 */
public class EntityUploadPanel extends JPanel implements ActionListener {

//    private JList uploadList;
    private JTextArea uploadText;
    private JButton searchNewButton;
    private JButton searchModifiedButton;
    private JButton uploadButton;
    private JButton viewUploadButton;
    private JButton createWorkspaceButton;
    private JTextField workspaceName;
//    private JCheckBox createWorkspace;
    private JPasswordField passwordText;
    private JProgressBar uploadProgress;
    private EntityUploader entityUploader;
    private JPanel workspacePanel;
    private JPanel passwordPanel;
    private ArbilWindowManager dialogHandler;

    public EntityUploadPanel(SessionStorage sessionStorage, EntityCollection entityCollection, ArbilWindowManager dialogHandler) {
        this.dialogHandler = dialogHandler;
        entityUploader = new EntityUploader(sessionStorage, entityCollection);
//        uploadList = new JList();
        uploadText = new JTextArea();
        searchNewButton = new JButton("Search New Entities");
        searchModifiedButton = new JButton("Search Modified Entities");
        uploadButton = new JButton("Upload Selected");
        viewUploadButton = new JButton("View Uploaded");
        createWorkspaceButton = new JButton("Create Workspace");
        workspaceName = new JTextField();
        workspaceName.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent keyEvent) {
                char keyChar = keyEvent.getKeyChar();
                if (!Character.isLetterOrDigit(keyChar)) {
                    // prevent non url chars being entered
                    keyEvent.consume();
                }
            }
        });
//        createWorkspace=new JCheckBox();
        passwordText = new JPasswordField();
        uploadProgress = new JProgressBar();
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(searchNewButton);
        controlPanel.add(searchModifiedButton);
        controlPanel.add(uploadButton);
        controlPanel.add(viewUploadButton);

        workspacePanel = new JPanel();
        workspacePanel.setLayout(new BorderLayout());
        workspacePanel.add(new JLabel("Target Workspace Name"), BorderLayout.LINE_START);
        workspacePanel.add(workspaceName, BorderLayout.CENTER);
        workspacePanel.add(createWorkspaceButton, BorderLayout.LINE_END);

        passwordPanel = new JPanel();
        passwordPanel.setLayout(new BorderLayout());
        passwordPanel.add(new JLabel("Workspace Password"), BorderLayout.LINE_START);
        passwordPanel.add(passwordText, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));
        topPanel.add(controlPanel);
        topPanel.add(workspacePanel);
        topPanel.add(passwordPanel);

        this.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.PAGE_START);
//        this.add(uploadList, BorderLayout.CENTER);
        this.add(new JScrollPane(uploadText), BorderLayout.CENTER);
        this.add(uploadProgress, BorderLayout.PAGE_END);
        uploadButton.setEnabled(false);
        viewUploadButton.setEnabled(false);
//        workspaceName.setEnabled(false);
//        passwordText.setEnabled(false);
        workspacePanel.setVisible(false);
        passwordPanel.setVisible(false);
        searchNewButton.addActionListener(this);
        searchModifiedButton.addActionListener(this);
        uploadButton.addActionListener(this);
        viewUploadButton.addActionListener(this);
        createWorkspaceButton.addActionListener(this);
        searchNewButton.setActionCommand("searchnew");
        searchModifiedButton.setActionCommand("searchmodified");
        uploadButton.setActionCommand("upload");
        viewUploadButton.setActionCommand("view");
        createWorkspaceButton.setActionCommand("create");
    }

    public void actionPerformed(ActionEvent e) {
        searchNewButton.setEnabled(false);
        searchModifiedButton.setEnabled(false);
        uploadButton.setEnabled(false);
        viewUploadButton.setEnabled(false);
        if (e.getActionCommand().equals("searchnew")) {
            uploadText.setText("Searching for local entities that do not exist on the server\n");
            uploadProgress.setIndeterminate(true);
            entityUploader.findLocalEntities(this);
        } else if (e.getActionCommand().equals("searchmodified")) {
            uploadText.setText("Searching for modified entities that require upload to the server\n");
            uploadProgress.setIndeterminate(true);
            entityUploader.findModifiedEntities(this);
        } else if (e.getActionCommand().equals("upload")) {
            if (!workspaceName.getText().isEmpty()) {
                uploadText.append("Uploading entities to the server\n");
                entityUploader.uploadLocalEntites(this, uploadProgress, uploadText, workspaceName.getText(), passwordText.getPassword() /*, createWorkspace.isSelected()*/);
            } else {
                uploadText.append("Please enter a workspace name\n");
            }
        } else if (e.getActionCommand().equals("seachcomplete")) {
            uploadText.append(entityUploader.getFoundMessage());
            uploadProgress.setIndeterminate(false);
            uploadText.append("Done\n");
            searchNewButton.setEnabled(true);
            searchModifiedButton.setEnabled(true);
        } else if (e.getActionCommand().equals("uploadaborted")) {
            uploadProgress.setIndeterminate(false);
            uploadText.append("Error on upload, does the specified workspace exist?\n");
        } else if (e.getActionCommand().equals("view")) {
            dialogHandler.openFileInExternalApplication(entityUploader.getWorkspaceUri());
        } else if (e.getActionCommand().equals("create")) {
            URI createUri = entityUploader.getCreateUrl(workspaceName.getText());
            dialogHandler.openFileInExternalApplication(createUri);
        }

//        workspaceName.setEnabled(entityUploader.canUpload());
//        passwordText.setEnabled(entityUploader.canUpload());
        workspacePanel.setVisible(entityUploader.canUpload());
        passwordPanel.setVisible(entityUploader.canUpload());
        uploadButton.setEnabled(entityUploader.canUpload());
        viewUploadButton.setEnabled(entityUploader.isUploadComplete());
    }
}
