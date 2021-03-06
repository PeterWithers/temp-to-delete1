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
package nl.mpi.kinnate.kindocument;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import nl.mpi.arbil.userstorage.SessionStorage;
import nl.mpi.arbil.util.BugCatcherManager;
import nl.mpi.arbil.util.MessageDialogHandler;
import nl.mpi.kinnate.entityindexer.EntityCollection;
import nl.mpi.kinnate.entityindexer.EntityServiceException;
import nl.mpi.kinnate.gedcomimport.ImportException;
import nl.mpi.kinnate.kindata.EntityRelation;
import nl.mpi.kinnate.projects.ProjectRecord;
import nl.mpi.kinnate.uniqueidentifiers.UniqueIdentifier;
import org.w3c.dom.Node;

/**
 * Created on : Aug 29, 2011, 3:47:29 PM
 *
 * @author Peter Withers
 */
public class EntityMerger extends DocumentLoader {

    private UniqueIdentifier[] deletedIdentifiersArray;
    private UniqueIdentifier[] affectedIdentifiersArray;
    final ProjectRecord projectRecord;

    public EntityMerger(SessionStorage sessionStorage, MessageDialogHandler dialogHandler, EntityCollection entityCollection) {
        super(sessionStorage, dialogHandler, entityCollection);
        this.projectRecord = entityCollection.getProjectRecord();
    }

    public UniqueIdentifier[] getAffectedIdentifiersArray() {
        return affectedIdentifiersArray;
    }

    public UniqueIdentifier[] getDeletedIdentifiersArray() {
        return deletedIdentifiersArray;
    }

    private void mergeFields(EntityDocument leadEntityDocument, Node alterElement, int level) throws ImportException {
        Node currentChild = alterElement.getFirstChild();
        while (currentChild != null) {
            if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
                final String localName = currentChild.getLocalName();
                String nodeValue = currentChild.getTextContent();
                if (nodeValue == null) {
                    nodeValue = "";
                }
                leadEntityDocument.appendValue(localName, nodeValue, level);
                if (currentChild.hasChildNodes()) {
                    mergeFields(leadEntityDocument, currentChild, level + 1);
                }
            }
            currentChild = currentChild.getNextSibling();
        }
    }

    public UniqueIdentifier mergeEntities(UniqueIdentifier[] selectedIdentifiers) throws ImportException, EntityServiceException {
        ArrayList<EntityDocument> nonLeadEntityDocuments = new ArrayList<EntityDocument>();
        ArrayList<UniqueIdentifier> deletedIdentifiers = new ArrayList<UniqueIdentifier>();
        UniqueIdentifier leadUniqueIdentifier;
        try {
            EntityDocument leadEntityDocument = getEntityDocuments(selectedIdentifiers, nonLeadEntityDocuments);
            leadUniqueIdentifier = leadEntityDocument.getUniqueIdentifier();
            for (EntityDocument alterEntity : nonLeadEntityDocuments) {
                deletedIdentifiers.add(alterEntity.getUniqueIdentifier());
                for (EntityRelation entityRelation : alterEntity.entityData.getAllRelations()) {
                    EntityDocument relatedDocument = entityMap.get(entityRelation.alterUniqueIdentifier);
                    if (!leadEntityDocument.entityData.getUniqueIdentifier().equals(relatedDocument.entityData.getUniqueIdentifier())) { // do not add a relation to itself                        
                        // add the new relation
                        leadEntityDocument.entityData.addRelatedNode(relatedDocument.entityData, entityRelation.getRelationType(), entityRelation.lineColour, entityRelation.labelString, entityRelation.dcrType, entityRelation.customType);
                    }
                    // remove the old entity relation
                    relatedDocument.entityData.removeRelationsWithNode(alterEntity.entityData);
                    alterEntity.entityData.removeRelationsWithNode(relatedDocument.entityData);
                }
                mergeFields(leadEntityDocument, alterEntity.metadataNode, 1);
            }
            // sometimes a sibling relation is created when parent relations are added so we iterate again and make sure none are left
            for (EntityDocument alterEntity : nonLeadEntityDocuments) {
                for (EntityRelation entityRelation : alterEntity.entityData.getAllRelations()) {
                    EntityDocument relatedDocument = entityMap.get(entityRelation.alterUniqueIdentifier);
                    // remove the old entity relation
                    relatedDocument.entityData.removeRelationsWithNode(alterEntity.entityData);
                    alterEntity.entityData.removeRelationsWithNode(relatedDocument.entityData);
                }
            }
            saveAllDocuments();
            for (EntityDocument alterEntity : nonLeadEntityDocuments) {
                try {
                    deleteFromDataBase(alterEntity);
                } catch (IOException exception) {
                    BugCatcherManager.getBugCatcher().logError(exception);
                    throw new ImportException("Error: " + exception.getMessage());
                }
            }
        } catch (URISyntaxException exception) {
            BugCatcherManager.getBugCatcher().logError(exception);
            throw new ImportException("Error: " + exception.getMessage());
        }
        deletedIdentifiersArray = deletedIdentifiers.toArray(new UniqueIdentifier[0]);
        affectedIdentifiersArray = getAffectedIdentifiers();
        return leadUniqueIdentifier;
    }

    public UniqueIdentifier[] duplicateEntities(UniqueIdentifier[] selectedIdentifiers) throws ImportException, EntityServiceException {
        // todo: complete this duplicate code based on mergeEntities and DocumentLoader
        ArrayList<UniqueIdentifier> addedIdentifiers = new ArrayList<UniqueIdentifier>();
        ArrayList<EntityDocument> entityDocumentList = new ArrayList<EntityDocument>();
        try {
            getEntityDocuments(selectedIdentifiers, entityDocumentList);
            for (UniqueIdentifier uniqueIdentifier : selectedIdentifiers) {
                EntityDocument masterDocument = entityMap.get(uniqueIdentifier);
                EntityDocument duplicateEntityDocument = new EntityDocument(masterDocument, new ImportTranslator(true), sessionStorage, projectRecord);
                addedIdentifiers.add(duplicateEntityDocument.getUniqueIdentifier());
                for (EntityRelation entityRelation : masterDocument.entityData.getAllRelations()) {
                    EntityDocument relatedDocument = entityMap.get(entityRelation.alterUniqueIdentifier);
                    // copy the relations
                    duplicateEntityDocument.entityData.addRelatedNode(relatedDocument.entityData, entityRelation.getRelationType(), entityRelation.lineColour, entityRelation.labelString, entityRelation.dcrType, entityRelation.customType);
                }
                // todo: the date and any other metadata not in the metadata node will be missed by this step, it would be best to move or modify the dates location in the file
                entityMap.put(duplicateEntityDocument.getUniqueIdentifier(), duplicateEntityDocument);
            }
            saveAllDocuments();
            return addedIdentifiers.toArray(new UniqueIdentifier[]{});
        } catch (URISyntaxException exception) {
            BugCatcherManager.getBugCatcher().logError(exception);
            throw new ImportException("Error: " + exception.getMessage());
        }
    }
}
