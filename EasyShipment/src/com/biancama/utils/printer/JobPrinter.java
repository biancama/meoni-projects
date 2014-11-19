package com.biancama.utils.printer;

import java.io.IOException;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.views.DocumentViewController;
import org.icepdf.ri.common.PrintHelper;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;

import com.biancama.log.BiancaLogger;

public class JobPrinter {
    private JobPrinter(){
        
    }
    
    public static boolean printPdfDocument(String pdfName, int numberOfCopies) throws PDFException, PDFSecurityException, IOException, PrintException{
        /**
         * Find Available printers
         */
        
        PrintService service =
                PrintServiceLookup.lookupDefaultPrintService();
        if (service == null){
            BiancaLogger.getLogger().severe("Default Printer not set");
            return false;
        }
        /**
         * Create PrinterJob
         */
        DocPrintJob printerJob = service.createPrintJob();
        // Print and document attributes sets.
        HashPrintRequestAttributeSet printRequestAttributeSet =
                new HashPrintRequestAttributeSet();
        HashDocAttributeSet docAttributeSet = new HashDocAttributeSet();

        // unix compression attribute where applicable
//        printRequestAttributeSet.add(Compression.COMPRESS);
        printRequestAttributeSet.add(PrintQuality.NORMAL);

        // change paper
        printRequestAttributeSet.add(MediaSizeName.ISO_A4);
        docAttributeSet.add(MediaSizeName.ISO_A4);

        // setting margins to full paper size as PDF have their own margins
        MediaSize mediaSize =
                MediaSize.getMediaSizeForName(MediaSizeName.ISO_A4);
        float[] size = mediaSize.getSize(MediaSize.MM);
        printRequestAttributeSet
                .add(new MediaPrintableArea(0, 0, size[0], size[1],
                        MediaPrintableArea.MM));
        docAttributeSet.add(new MediaPrintableArea(0, 0, size[0], size[1],
                MediaPrintableArea.MM));

        // display paper size.
       
           BiancaLogger.getLogger().info("Paper Size: " + MediaSizeName.ISO_A4.getName() +
                " " + size[0] + " x " + size[1]);
       

        printRequestAttributeSet.add(new PageRanges(1, 5 ));

        // Open the document, create a PrintHelper and finally print the document
        Document pdf = new Document();
        pdf.setFile(pdfName);
        SwingController sc = new SwingController();
        DocumentViewController vc = new DocumentViewControllerImpl(sc);
        vc.setDocument(pdf);

        // create a new print helper
        PrintHelper printHelper = new PrintHelper(vc, pdf.getPageTree());
        printHelper.setupPrintService(0, pdf.getNumberOfPages(), numberOfCopies, true, false);
        // print the document
        printHelper.print();
        return true;
    }
}
