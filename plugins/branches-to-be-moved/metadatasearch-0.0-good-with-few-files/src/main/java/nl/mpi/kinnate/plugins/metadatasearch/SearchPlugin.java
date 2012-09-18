package nl.mpi.kinnate.plugins.metadatasearch;

import javax.swing.JPanel;
import nl.mpi.arbil.plugin.ArbilWindowPlugin;
import nl.mpi.arbil.plugin.PluginArbilDataNodeLoader;
import nl.mpi.arbil.plugin.PluginBugCatcher;
import nl.mpi.arbil.plugin.PluginDialogHandler;
import nl.mpi.arbil.plugin.PluginException;
import nl.mpi.arbil.plugin.PluginSessionStorage;
import nl.mpi.kinnate.plugin.BasePlugin;

/**
 * Document : SearchPlugin <br> Created on Sep 10, 2012, 5:14:23 PM <br>
 *
 * @author Peter Withers <br>
 */
public class SearchPlugin implements BasePlugin, ArbilWindowPlugin {

    public String getName() {
        return "XML DB Search Plugin";
    }

    public int getMajorVersionNumber() {
        return 0;
    }

    public int getMinorVersionNumber() {
        return 0;
    }

    public int getBuildVersionNumber() {
        return 0;
    }

    public String getDescription() {
        return "A plugin for Arbil that provides a XML DB search.";
    }

    public JPanel getUiPanel(PluginDialogHandler dialogHandler, PluginSessionStorage sessionStorage, PluginBugCatcher bugCatcher, PluginArbilDataNodeLoader arbilDataNodeLoader) throws PluginException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}