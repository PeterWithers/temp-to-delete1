package nl.mpi.kinnate.ui;

import java.awt.BorderLayout;
import nl.mpi.arbil.ArbilIcons;
import nl.mpi.arbil.ui.ArbilWindowManager;
import nl.mpi.arbil.util.ApplicationVersionManager;
import nl.mpi.kinnate.KinOathVersion;

/*
 *  Document   : MainFrame
 *  Author     : Peter Withers
 *  Created on : Aug 16, 2010, 5:20:20 PM
 */
public class MainFrame extends javax.swing.JFrame {

    /** Creates new form MainFrame */
    public MainFrame() {
        initComponents();
        DiagramWindowManager diagramWindowManager = new DiagramWindowManager(this);
        ((EditMenu) editMenu).enableMenuKeys();
        final ApplicationVersionManager versionManager = new ApplicationVersionManager(new KinOathVersion());
        nl.mpi.kinnate.KinnateArbilInjector.injectHandlers(versionManager);
        this.add(jTabbedPane1, BorderLayout.CENTER);
        diagramWindowManager.newDiagram();
        jMenuBar1.add(new FileMenu(diagramWindowManager));
        jMenuBar1.add(new DiagramPanelsMenu(diagramWindowManager));
        jMenuBar1.add(new KinTermsMenu(diagramWindowManager));
        jMenuBar1.add(new ArchiveMenu(diagramWindowManager));
        this.doLayout();
        this.pack();
        ArbilWindowManager.getSingleInstance().setMessagesCanBeShown(true);
        setTitle(versionManager.getApplicationVersion().applicationTitle + " " + versionManager.getApplicationVersion().compileDate);
        // set the icon for the application (if this is still required for the various OSs). This is not required for Mac but might be needed for windows or linux.
        setIconImage(ArbilIcons.getSingleInstance().linorgIcon.getImage());
//	if (arbilMenuBar.checkNewVersionAtStartCheckBoxMenuItem.isSelected()) {
        // todo: Ticket #1066 add the check for updates and check now menu items
        versionManager.checkForUpdate();
//	}
    }

    public void loadAllTrees() {
        Object selectedComponent = jTabbedPane1.getSelectedComponent();
        if (selectedComponent instanceof KinDiagramPanel) {
            ((KinDiagramPanel) selectedComponent).loadAllTrees();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        editMenu = new EditMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.PAGE_START);

        editMenu.setText("Edit");
        editMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                editMenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        editMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMenuActionPerformed(evt);
            }
        });
        jMenuBar1.add(editMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void editMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMenuActionPerformed
    }//GEN-LAST:event_editMenuActionPerformed

    private void editMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_editMenuMenuSelected
//        // init the menu and pass it the current save panel
////        int tabIndex = Integer.valueOf(jTabbedPane1.getSelectedIndex());
////        SavePanel savePanel = getSavePanel(tabIndex);
// todo: pass the current tab selection to the menu         ((EditMenu) editMenu).initMenu();
    }//GEN-LAST:event_editMenuMenuSelected

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                final MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                mainFrame.loadAllTrees();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
