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
package nl.mpi.kinnate.svg;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import nl.mpi.arbil.util.BugCatcherManager;
import nl.mpi.kinnate.SavePanel;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.XMLAbstractTranscoder;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.apache.fop.svg.PDFTranscoder;

/**
 * Document : DiagramTranscoder
 * Created on : May 10, 2011, 9:03:47 PM
 * Author : Peter Withers
 */
public class DiagramTranscoder {

    public enum OutputType {

        PDF, JPEG, PNG, TIFF
    };
//    private int dpi = 300;
    private OutputType outputType = OutputType.PDF;
    private File outputFile;
    private SavePanel savePanel;
    private Dimension2D diagramSize;

    public DiagramTranscoder(SavePanel savePanel) {
        this.savePanel = savePanel;
        diagramSize = savePanel.getGraphPanel().getDiagramSize();
//        if (!outputFile.getName().toLowerCase().endsWith(".jpg")) {
//            outputType = OutputType.JPEG;
//        }
//        if (!outputFile.getName().toLowerCase().endsWith(".pdf")) {
//            outputType = OutputType.PDF;
//        }
    }

    public Dimension getCurrentSize() {
        if (diagramSize != null) {
//            return new Dimension((int) (diagramSize.getWidth() / 25.4 * dpi), (int) (diagramSize.getHeight() / 25.4 * dpi));
            return new Dimension((int) (diagramSize.getWidth()), (int) (diagramSize.getHeight()));
        } else {
            return null;
        }
    }

//    public int getDpi() {
//        return dpi;
//    }
//    public void setDpi(int dpi) {
//        this.dpi = dpi;
//    }
    public OutputType getOutputType() {
        return outputType;
    }

    public void setOutputType(OutputType outputType) {
        this.outputType = outputType;
    }

    public void exportDiagram(File outputFile) throws IOException, TranscoderException {
        this.outputFile = outputFile;
        Transcoder transcoder;
        switch (outputType) {
            case JPEG:
                fixSuffix(".jpg");
                transcoder = new JPEGTranscoder();
                transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.8));
                break;
            case PNG:
                fixSuffix(".png");
                transcoder = new PNGTranscoder();
                break;
            case TIFF:
                fixSuffix(".tif");
                transcoder = new TIFFTranscoder();
                break;
            default:
                fixSuffix(".pdf");
                transcoder = new PDFTranscoder();
                transcoder.addTranscodingHint(PDFTranscoder.KEY_STROKE_TEXT, Boolean.FALSE);
                break;
        }
        transcoder.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, java.awt.Color.WHITE);
        transcoder.addTranscodingHint(XMLAbstractTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
//        transcoder.addTranscodingHint(ImageTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, new Float((float) (25.4 / dpi)));
        transcodeDom(transcoder);
    }

    private void fixSuffix(String requiredSuffix) {
        if (!outputFile.getName().toLowerCase().endsWith(requiredSuffix)) {
            String fileName = outputFile.getName();
            fileName = fileName.replaceFirst("\\....$", "");
            outputFile = new File(outputFile.getParentFile(), fileName + requiredSuffix);
        }
    }

    private void transcodeDom(Transcoder transcoder) throws IOException, TranscoderException {
        TranscoderInput transcoderInput = new TranscoderInput(savePanel.getGraphPanel().doc);
        OutputStream outputStream = new java.io.FileOutputStream(outputFile);
        outputStream = new java.io.BufferedOutputStream(outputStream);
        try {
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
            // todo: resolve the issue here when transcoding to jpg
            transcoder.transcode(transcoderInput, transcoderOutput);
        } finally {
            outputStream.close();
        }
    }
}
