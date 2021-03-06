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
package nl.mpi.kinnate.ui.entityprofiles;

import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import nl.mpi.kinnate.kindocument.ProfileManager;

/**
 * Document : ProfileTableModel Created on : Jan 19, 2012, 4:57:20 PM
 *
 * @author Peter Withers
 */
public class ProfileTableModel extends AbstractTableModel {
    private static final ResourceBundle widgets = ResourceBundle.getBundle("nl/mpi/kinoath/localisation/Widgets");

    private String[] columnNames = new String[]{widgets.getString("ProfileTable_NAME"), widgets.getString("ProfileTable_DESCRIPTION"), widgets.getString("ProfileTable_REGISTRATION DATE"), widgets.getString("ProfileTable_CREATOR NAME"), widgets.getString("ProfileTable_USE ENTITY TYPE")}; //, "ID", "href"};
    private ProfileManager profileManager;

    public void setProfileManager(ProfileManager profileManager) {
        this.profileManager = profileManager;
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        if (profileManager == null) {
            return 0;
        }
        return profileManager.getProfileCount();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 4);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 4:
                return Boolean.class;
            default:
                return super.getColumnClass(columnIndex);
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 4) {
            final nl.mpi.arbil.clarin.profiles.CmdiProfileReader.CmdiProfile selectedProfile = profileManager.getProfileAt(rowIndex);
            if (aValue.equals(true)) {
                profileManager.addProfileSelection(selectedProfile.id, selectedProfile.name);
            } else {
                profileManager.removeProfileSelection(selectedProfile.id);
            }
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        final nl.mpi.arbil.clarin.profiles.CmdiProfileReader.CmdiProfile selectedProfile = profileManager.getProfileAt(rowIndex);
        switch (columnIndex) {
            case 0:
                return selectedProfile.name;
            case 1:
                return selectedProfile.description;
            case 2:
                return selectedProfile.registrationDate.substring(0, 10);
            case 3:
                return selectedProfile.creatorName;
            case 4:
                return profileManager.profileIsSelected(selectedProfile.id);
//            case 4:
//                return cmdiProfileArray.get(rowIndex).id;
//            case 5:
//                return cmdiProfileArray.get(rowIndex).href;
            default:
                return "";
        }
    }
}
