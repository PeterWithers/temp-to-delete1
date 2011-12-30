package nl.mpi.kinnate.gedcomimport;

import nl.mpi.kinnate.kindocument.EntityDocument;
import nl.mpi.kinnate.kindocument.ImportTranslator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import nl.mpi.arbil.userstorage.SessionStorage;
import nl.mpi.arbil.util.ArbilBugCatcher;
import nl.mpi.kinnate.kindata.DataTypes.RelationLineType;
import nl.mpi.kinnate.kindata.DataTypes.RelationType;

/**
 *  Document   : CsvImporter
 *  Created on : May 30, 2011, 10:29:24 AM
 *  Author     : Peter Withers
 */
public class CsvImporter extends EntityImporter implements GenericImporter {

    public CsvImporter(JProgressBar progressBarLocal, JTextArea importTextAreaLocal, boolean overwriteExistingLocal, SessionStorage sessionStorage) {
        super(progressBarLocal, importTextAreaLocal, overwriteExistingLocal, sessionStorage);
    }

    @Override
    public boolean canImport(String inputFileString) {
        return (inputFileString.toLowerCase().endsWith(".csv"));
    }

    @Deprecated
    private String cleanCsvString(String valueString) {
        valueString = valueString.replaceAll("^\"", "");
        valueString = valueString.replaceAll("\"$", "");
        valueString = valueString.replaceAll("\"\"", "");
        return valueString;
    }

//    private char detectFieldDelimiter(BufferedReader bufferedReader) throws IOException {
//        int tabCounter = 0;
//        int commaCounter = 0;
//        outerLoop:
//        for (int charCount = 0; charCount < 1000; charCount++) {
//            try {
//                switch (bufferedReader.read()) {
//                    case -1:
//                    case '\n':
//                    case '\r':
//                        break outerLoop;
//                    case ',':
//                        commaCounter++;
//                        break;
//                    case '\t':
//                        tabCounter++;
//                        break;
//                }
//            } catch (IOException exception) {
//                appendToTaskOutput("Failed to test the file type");
//                throw exception;
//            }
//        }
//        char fieldSeparator;
//        if (commaCounter > tabCounter) {
//            fieldSeparator = ',';
//            appendToTaskOutput("Comma separated file detected");
//        } else {
//            fieldSeparator = '\t';
//            appendToTaskOutput("Tab separated file detected");
//        }
//        return fieldSeparator;
//    }
    protected ArrayList<String> getFieldsForLine(BufferedReader bufferedReader /*, char fieldSeparator*/) throws IOException {
        ArrayList<String> lineFields = new ArrayList<String>();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            int readChar = bufferedReader.read();
            while (readChar != -1) {
                switch (readChar) {
                    case -1:
                    case '\n':
                    case '\r':
                        if (stringBuilder.length() > 0) {
                            lineFields.add(stringBuilder.toString());
                        }
                        return lineFields;
                    case '"':
                        boolean insideQuotes = true;
                        int quotedCharsCount = 0;
                        while (insideQuotes) {
                            // ignore all the chars between quotes
                            final int readQuoted = bufferedReader.read();
                            if (readQuoted == -1) {
                                appendToTaskOutput("Warning: file ended within a quoted section");
                                return lineFields;
                            }
                            quotedCharsCount++;
                            insideQuotes = readQuoted != '"';
                            if (insideQuotes) {
                                // add the chars from within the quotes
                                stringBuilder.append((char) readQuoted);
                            }
                        }
                        if (quotedCharsCount < 1) {
                            // todo: test that escaped quotes pass into the output correctly
                            // allow "" excaped quotes to pass as a single quote into the output
                            stringBuilder.append((char) readChar);
                        }
                        break;
                    case ',':
                    case '\t':
                        lineFields.add(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        break;
                    default:
                        stringBuilder.append((char) readChar);
                }
//                if (readChar == fieldSeparator) {
//                    lineFields.add(stringBuilder.toString());
//                } else {
//                    stringBuilder.append(readChar);
//                }
                readChar = bufferedReader.read();
            }
        } catch (IOException exception) {
            appendToTaskOutput("Failed to read lines of input file");
            throw exception;
        }
        if (stringBuilder.length() > 0) {
            lineFields.add(stringBuilder.toString());
        }
        return lineFields;
    }

    @Override
    public URI[] importFile(InputStreamReader inputStreamReader) {
        ArrayList<URI> createdNodes = new ArrayList<URI>();
        try {
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            char fieldSeparator = detectFieldDelimiter(bufferedReader);
            // restart at the begining of the file
            bufferedReader = new BufferedReader(inputStreamReader);
            // create the import translator
            ImportTranslator importTranslator = new ImportTranslator(true);
            importTranslator.addTranslationEntry("Gender", "0", "Gender", "female");
            importTranslator.addTranslationEntry("Gender", "1", "Gender", "male");
            importTranslator.addTranslationEntry("Gender", "f", "Gender", "female");
            importTranslator.addTranslationEntry("Gender", "m", "Gender", "male");

            ArrayList<String> allHeadings = getFieldsForLine(bufferedReader /*, fieldSeparator */);
            super.incrementLineProgress();
//            int maxImportCount = 10;
//            "ID","Gender","Name1","Name2","Name3","Name4","Name5","Name6","Date of Birth","Date of Death","sub_group","Parents1-ID","Parents1-Gender","Parents1-Name1","Parents1-Name2","Parents1-Name3","Parents1-Name4","Parents1-Name5","Parents1-Name6","Parents1-Date of Birth","Parents1-Date of Death","Parents1-sub_group","Parents2-ID","Parents2-Gender","Parents2-Name1","Parents2-Name2","Parents2-Name3","Parents2-Name4","Parents2-Name5","Parents2-Name6","Parents2-Date of Birth","Parents2-Date of Death","Parents2-sub_group","Spouses1-ID","Spouses1-Gender","Spouses1-Name1","Spouses1-Name2","Spouses1-Name3","Spouses1-Name4","Spouses1-Name5","Spouses1-Name6","Spouses1-Date of Birth","Spouses1-Date of Death","Spouses1-sub_group","Spouses2-ID","Spouses2-Gender","Spouses2-Name1","Spouses2-Name2","Spouses2-Name3","Spouses2-Name4","Spouses2-Name5","Spouses2-Name6","Spouses2-Date of Birth","Spouses2-Date of Death","Spouses2-sub_group","Spouses3-ID","Spouses3-Gender","Spouses3-Name1","Spouses3-Name2","Spouses3-Name3","Spouses3-Name4","Spouses3-Name5","Spouses3-Name6","Spouses3-Date of Birth","Spouses3-Date of Death","Spouses3-sub_group","Spouses4-ID","Spouses4-Gender","Spouses4-Name1","Spouses4-Name2","Spouses4-Name3","Spouses4-Name4","Spouses4-Name5","Spouses4-Name6","Spouses4-Date of Birth","Spouses4-Date of Death","Spouses4-sub_group","Spouses5-ID","Spouses5-Gender","Spouses5-Name1","Spouses5-Name2","Spouses5-Name3","Spouses5-Name4","Spouses5-Name5","Spouses5-Name6","Spouses5-Date of Birth","Spouses5-Date of Death","Spouses5-sub_group","Spouses6-ID","Spouses6-Gender","Spouses6-Name1","Spouses6-Name2","Spouses6-Name3","Spouses6-Name4","Spouses6-Name5","Spouses6-Name6","Spouses6-Date of Birth","Spouses6-Date of Death","Spouses6-sub_group","Children1-ID","Children1-Gender","Children1-Name1","Children1-Name2","Children1-Name3","Children1-Name4","Children1-Name5","Children1-Name6","Children1-Date of Birth","Children1-Date of Death","Children1-sub_group","Children2-ID","Children2-Gender","Children2-Name1","Children2-Name2","Children2-Name3","Children2-Name4","Children2-Name5","Children2-Name6","Children2-Date of Birth","Children2-Date of Death","Children2-sub_group","Children3-ID","Children3-Gender","Children3-Name1","Children3-Name2","Children3-Name3","Children3-Name4","Children3-Name5","Children3-Name6","Children3-Date of Birth","Children3-Date of Death","Children3-sub_group","Children4-ID","Children4-Gender","Children4-Name1","Children4-Name2","Children4-Name3","Children4-Name4","Children4-Name5","Children4-Name6","Children4-Date of Birth","Children4-Date of Death","Children4-sub_group","Children5-ID","Children5-Gender","Children5-Name1","Children5-Name2","Children5-Name3","Children5-Name4","Children5-Name5","Children5-Name6","Children5-Date of Birth","Children5-Date of Death","Children5-sub_group","Children6-ID","Children6-Gender","Children6-Name1","Children6-Name2","Children6-Name3","Children6-Name4","Children6-Name5","Children6-Name6","Children6-Date of Birth","Children6-Date of Death","Children6-sub_group","Children7-ID","Children7-Gender","Children7-Name1","Children7-Name2","Children7-Name3","Children7-Name4","Children7-Name5","Children7-Name6","Children7-Date of Birth","Children7-Date of Death","Children7-sub_group","Children8-ID","Children8-Gender","Children8-Name1","Children8-Name2","Children8-Name3","Children8-Name4","Children8-Name5","Children8-Name6","Children8-Date of Birth","Children8-Date of Death","Children8-sub_group","Children9-ID","Children9-Gender","Children9-Name1","Children9-Name2","Children9-Name3","Children9-Name4","Children9-Name5","Children9-Name6","Children9-Date of Birth","Children9-Date of Death","Children9-sub_group","Children10-ID","Children10-Gender","Children10-Name1","Children10-Name2","Children10-Name3","Children10-Name4","Children10-Name5","Children10-Name6","Children10-Date of Birth","Children10-Date of Death","Children10-sub_group","Children11-ID","Children11-Gender","Children11-Name1","Children11-Name2","Children11-Name3","Children11-Name4","Children11-Name5","Children11-Name6","Children11-Date of Birth","Children11-Date of Death","Children11-sub_group","Children12-ID","Children12-Gender","Children12-Name1","Children12-Name2","Children12-Name3","Children12-Name4","Children12-Name5","Children12-Name6","Children12-Date of Birth","Children12-Date of Death","Children12-sub_group","Children13-ID","Children13-Gender","Children13-Name1","Children13-Name2","Children13-Name3","Children13-Name4","Children13-Name5","Children13-Name6","Children13-Date of Birth","Children13-Date of Death","Children13-sub_group","Children14-ID","Children14-Gender","Children14-Name1","Children14-Name2","Children14-Name3","Children14-Name4","Children14-Name5","Children14-Name6","Children14-Date of Birth","Children14-Date of Death","Children14-sub_group","Children15-ID","Children15-Gender","Children15-Name1","Children15-Name2","Children15-Name3","Children15-Name4","Children15-Name5","Children15-Name6","Children15-Date of Birth","Children15-Date of Death","Children15-sub_group","Children16-ID","Children16-Gender","Children16-Name1","Children16-Name2","Children16-Name3","Children16-Name4","Children16-Name5","Children16-Name6","Children16-Date of Birth","Children16-Date of Death","Children16-sub_group","Children17-ID","Children17-Gender","Children17-Name1","Children17-Name2","Children17-Name3","Children17-Name4","Children17-Name5","Children17-Name6","Children17-Date of Birth","Children17-Date of Death","Children17-sub_group","Children18-ID","Children18-Gender","Children18-Name1","Children18-Name2","Children18-Name3","Children18-Name4","Children18-Name5","Children18-Name6","Children18-Date of Birth","Children18-Date of Death","Children18-sub_group","Children19-ID","Children19-Gender","Children19-Name1","Children19-Name2","Children19-Name3","Children19-Name4","Children19-Name5","Children19-Name6","Children19-Date of Birth","Children19-Date of Death","Children19-sub_group","Children20-ID","Children20-Gender","Children20-Name1","Children20-Name2","Children20-Name3","Children20-Name4","Children20-Name5","Children20-Name6","Children20-Date of Birth","Children20-Date of Death","Children20-sub_group"
//            String headingLine = bufferedReader.readLine();
            try {
                boolean continueReading = true;
                while (continueReading) {
                    ArrayList<String> lineFields = getFieldsForLine(bufferedReader);
                    continueReading = lineFields.size() > 0;
                    EntityDocument currentEntity = null;
                    EntityDocument relatedEntity = null;
                    String relatedEntityPrefix = null;
                    int valueCount = 0;
                    for (String entityField : lineFields) {
//                            String cleanValue = cleanCsvString(entityLineString);
                        String headingString;
                        if (allHeadings.size() > valueCount) {
                            headingString = allHeadings.get(valueCount);
                        } else {
                            headingString = "-unnamed-field-";
                            appendToTaskOutput("Warning " + headingString + " for value: " + entityField);
                        }
                        if (currentEntity == null) {
                            currentEntity = getEntityDocument(createdNodes, "Entity", entityField, importTranslator);
                        } else if (entityField.length() > 0) {
                            if (headingString.matches("Spouses[\\d]*-ID")) {
                                relatedEntity = getEntityDocument(createdNodes, "Entity", entityField, importTranslator);
                                currentEntity.entityData.addRelatedNode(relatedEntity.entityData, RelationType.union, RelationLineType.sanguineLine, null, null);
                                relatedEntityPrefix = headingString.substring(0, headingString.length() - "ID".length());
                            } else if (headingString.matches("Parents[\\d]*-ID")) {
                                relatedEntity = getEntityDocument(createdNodes, "Entity", entityField, importTranslator);
                                currentEntity.entityData.addRelatedNode(relatedEntity.entityData, RelationType.ancestor, RelationLineType.sanguineLine, null, null);
                                relatedEntityPrefix = headingString.substring(0, headingString.length() - "ID".length());
                            } else if (headingString.matches("Children[\\d]*-ID")) {
                                relatedEntity = getEntityDocument(createdNodes, "Entity", entityField, importTranslator);
                                currentEntity.entityData.addRelatedNode(relatedEntity.entityData, RelationType.descendant, RelationLineType.sanguineLine, null, null);
                                relatedEntityPrefix = headingString.substring(0, headingString.length() - "ID".length());
                            } else if (relatedEntityPrefix != null && headingString.startsWith(relatedEntityPrefix)) {
                                relatedEntity.insertValue(headingString.substring(relatedEntityPrefix.length()), entityField);
//                                    appendToTaskOutput("Setting value in related entity: " + allHeadings.get(valueCount) + " : " + cleanValue);
//                                    appendToTaskOutput(importTextArea, "Ignoring: " + allHeadings.get(valueCount) + " : " + cleanValue);
                            } else {
                                currentEntity.insertValue(headingString, entityField);
//                                    appendToTaskOutput("Setting value: " + allHeadings.get(valueCount) + " : " + cleanValue);
                            }
                        }
                        valueCount++;
                    }
//                        if (maxImportCount-- < 0) {
//                            appendToTaskOutput(importTextArea, "Aborting import due to max testing limit");
//                            break;
//                        }
                    super.incrementLineProgress();
                }
            } catch (ImportException exception) {
                appendToTaskOutput(exception.getMessage());
            }
            saveAllDocuments();
        } catch (IOException exception) {
            new ArbilBugCatcher().logError(exception);
            appendToTaskOutput("Error: " + exception.getMessage());
        }
        return createdNodes.toArray(new URI[]{});
    }
}
