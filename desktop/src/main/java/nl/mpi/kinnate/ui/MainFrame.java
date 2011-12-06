package nl.mpi.kinnate.ui;

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

    DiagramWindowManager diagramWindowManager;

    /** Creates new form MainFrame */
    public MainFrame() {
        initComponents();
        diagramWindowManager = new DiagramWindowManager(this);
        final ApplicationVersionManager versionManager = new ApplicationVersionManager(new KinOathVersion());
        nl.mpi.kinnate.KinnateArbilInjector.injectHandlers(versionManager);
        diagramWindowManager.newDiagram();
        jMenuBar1.add(new FileMenu(diagramWindowManager));
        jMenuBar1.add(new EditMenu(diagramWindowManager));
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
        diagramWindowManager.loadAllTrees();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JMenuBar jMenuBar1;
    // End of variables declaration//GEN-END:variables
}
