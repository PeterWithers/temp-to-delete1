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
package nl.mpi.kinnate.entityindexer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Document : IndexParameters Created on : Feb 14, 2011, 11:47:34 AM
 *
 * @author Peter Withers
 */
public class IndexerParameters {

    @XmlTransient
    public boolean valuesChanged = false;
    @XmlTransient
    public String linkPath = "/Kinnate/Relation/Link";
//    public IndexerParam relevantEntityData = new IndexerParam(new String[][]{{"Kinnate/Gedcom/Entity/NoteText"}, {"Kinnate/Gedcom/Entity/SEX"}, {"Kinnate/Gedcom/Entity/GedcomType"}, {"Kinnate/Gedcom/Entity/NAME/NAME"}, {"Kinnate/Gedcom/Entity/NAME/NPFX"}}); // todo: the relevantData array comes from the user via the svg
//    @XmlTransient
//    public IndexerParam relevantLinkData = new IndexerParam(new String[][]{{"Type"}}, "%s");
    @XmlElement(name = "DateOfBirthFields", namespace = "http://mpi.nl/tla/kin") // todo:. allow the user to edit the date of birth and date of death fields
    public IndexerParam dateOfBirthField = new IndexerParam(new String[][]{{"*:Kinnate/*:CustomData/*:DateOfBirth"}}, "*:Kinnate/*:CustomData/*:%s");
    @XmlElement(name = "DateOfDeathFields", namespace = "http://mpi.nl/tla/kin")
    public IndexerParam dateOfDeathField = new IndexerParam(new String[][]{{"*:Kinnate/*:CustomData/*:DateOfDeath"}}, "*:Kinnate/*:CustomData/*:%s");
    @XmlElement(name = "LabelFields", namespace = "http://mpi.nl/tla/kin")
    public IndexerParam labelFields = new IndexerParam(new String[][]{
                {"*:Kinnate/*:CustomData/*:Type"},
                {"*:Kinnate/*:CustomData/*[starts-with(local-name(), 'Name')]"},
                {"*:Kinnate/*:CustomData/*[ends-with(local-name(), 'name')]"}
            }, "*:Kinnate/*:CustomData/*:%s");
    @XmlElement(name = "SymbolFieldsFields", namespace = "http://mpi.nl/tla/kin")
    public IndexerParam symbolFieldsFields = new IndexerParam(new String[][]{
                //        {"*:Kinnate/*:Gedcom/*:Entity[*:sex='male']", "triangle"},
                {"*:Kinnate/*:CustomData[*:Gender='Male']", "triangle"} //        , {"*:Kinnate/*:Gedcom/*:Entity[*:sex='female']", "circle"}
                , {"*:Kinnate/*:CustomData[*:Gender='Female']", "circle"},
                {"*:Kinnate/*:CustomData[*:Gender='']", "square"},
                {"*:Kinnate/*:CustomData[*:DateOfDeath!='']", "blackstrikethrough"}, // the use of != is correct because if there is any node value in the DateOfBirth node or nodes then the symbol should be used
                {"*:Kinnate/*:CustomData[*:Chromosome='Xx']", "redmarker"},
                {"*:Kinnate/*:CustomData[*:Chromosome='xY']", "redmarker"}
            //                , {"*:Kinnate/*:CustomData/*:Individual[*:Gender='']", "square"},
            //                {"*:Kinnate/*:CustomData/*:Individual[*:Gender='Male']", "triangle"},
            //                {"*:Kinnate/*:CustomData/*:Individual[*:Gender='Female']", "circle"}
            //        , {"*:Kinnate/*:Gedcom/*:Entity[*:GedcomType='FAM']", "union"}
            }, "*:Kinnate/*:CustomData[*:%s='']");
//    @XmlElement(name = "DefaultSymbol", namespace = "http://mpi.nl/tla/kin")
//    public String defaultSymbol = "square";
//    @XmlElement(name = "AncestorFields", namespace = "http://mpi.nl/tla/kin")
//    public IndexerParam ancestorFields = new IndexerParam(new String[][]{{"Kinnate.Gedcom.Entity.FAMC"}, {"Kinnate.Gedcom.Entity.BIRT.FAMC"}, {"Kinnate.Gedcom.Entity.CHR.FAMC"}, {"Kinnate.Gedcom.Entity.ADOP.FAMC"}, {"Kinnate.Gedcom.Entity.SLGC.FAMC"}, {"Kinnate.Gedcom.Entity.HUSB"}, {"Kinnate.Gedcom.Entity.WIFE"}, {"ancestor"}});
//    public IndexerParam siblingFields = new IndexerParam(new String[]{{"Kinnate.Gedcom.Entity.CHIL"}, {"Kinnate.Gedcom.Entity.FAMS"}});
//    @XmlElement(name = "DecendantFields", namespace = "http://mpi.nl/tla/kin")
//    public IndexerParam decendantFields = new IndexerParam(new String[][]{{"Kinnate.Gedcom.Entity.CHIL"}, {"Kinnate.Gedcom.Entity.FAMS"}, {"descendant"}});
//    sibling, , union, none
    //    public IndexerParam showEntityFields = new IndexerParam(new String[][]{{"Kinnate/Gedcom/Entity/GedcomType=INDI"}, {"Kinnate/Gedcom/Entity/GedcomType=FAM"}}); // todo: add fields that can be used to controll which nodes are shown
//    @Deprecated // I think this is no longer used or needed
//    private String[][] relevantEntityData = null;
//
//    @Deprecated // I think this is no longer used or needed
//    public String[][] getRelevantEntityData() {
//        if (relevantEntityData == null) {
//            ArrayList<String[]> relevantDataList = new ArrayList<String[]>();
//            for (IndexerParam currentIndexerParam : new IndexerParam[]{labelFields, symbolFieldsFields/*, showEntityFields*/}) {
//                for (String[] currentData : currentIndexerParam.getValues()) {
//                    relevantDataList.add(currentData);
//                }
//            }
//            relevantEntityData = relevantDataList.toArray(new String[][]{});
//        }
//        return relevantEntityData;
//    }
}
