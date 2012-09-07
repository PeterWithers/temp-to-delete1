package nl.mpi.kinnate.plugins.metadatasearch.db;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import nl.mpi.arbil.data.ArbilDataNodeLoader;

/**
 * Document : AbstractDbTreeNode <br> Created on Sep 6, 2012, 4:20:23 PM <br>
 *
 * @author Peter Withers <br>
 */
abstract public class AbstractDbTreeNode implements TreeNode {

    protected DbTreeNode parentDbTreeNode = null;
    protected DefaultTreeModel defaultTreeModel = null;
    protected ArbilDataNodeLoader arbilDataNodeLoader;

    public void setParentDbTreeNode(DbTreeNode parentDbTreeNode, DefaultTreeModel defaultTreeModel, ArbilDataNodeLoader arbilDataNodeLoader) {
        this.parentDbTreeNode = parentDbTreeNode;
        this.defaultTreeModel = defaultTreeModel;
        this.arbilDataNodeLoader = arbilDataNodeLoader;
    }
}