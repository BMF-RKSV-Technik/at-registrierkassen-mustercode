/*
 * Copyright (C) 2015
 * A-SIT Plus GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.asitplus.regkassen.core.modules.print;

import at.asitplus.regkassen.core.base.receiptdata.ReceiptPackage;
import at.asitplus.regkassen.core.base.receiptdata.TaxType;
import at.asitplus.regkassen.core.base.util.CashBoxUtils;
import at.asitplus.regkassen.core.base.util.MachineCodeValue;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1AfmPfbFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class SimplePDFPrinterModule implements PrinterModule {

    public List<byte[]> printReceipt(java.util.List<ReceiptPackage> receiptPackages, ReceiptPrintType receiptPrintType) {
        List<byte[]> pdfReceipts = new ArrayList<>();
        for (ReceiptPackage receiptPackage : receiptPackages) {
            pdfReceipts.add(printReceipt(receiptPackage, receiptPrintType));
        }
        return pdfReceipts;
    }

    public byte[] printReceipt(ReceiptPackage receiptPackage, ReceiptPrintType receiptPrintType) {
        try {
            //init PDF document
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDPage.PAGE_SIZE_A6);
            document.addPage(page);

            //init content objects
            PDRectangle rect = page.getMediaBox();
            PDPageContentStream cos = new PDPageContentStream(document, page);

            //add taxtype-sums
            int line = 1;
            addTaxTypeToPDF(cos, rect, line++, receiptPackage.getReceiptRepresentationForSignature().getSumTaxSetNormal(), TaxType.SATZ_NORMAL);
            addTaxTypeToPDF(cos, rect, line++, receiptPackage.getReceiptRepresentationForSignature().getSumTaxSetErmaessigt1(), TaxType.SATZ_ERMAESSIGT_1);
            addTaxTypeToPDF(cos, rect, line++, receiptPackage.getReceiptRepresentationForSignature().getSumTaxSetErmaessigt2(), TaxType.SATZ_ERMAESSIGT_2);
            addTaxTypeToPDF(cos, rect, line++, receiptPackage.getReceiptRepresentationForSignature().getSumTaxSetNull(), TaxType.SATZ_NULL);
            addTaxTypeToPDF(cos, rect, line++, receiptPackage.getReceiptRepresentationForSignature().getSumTaxSetBesonders(), TaxType.SATZ_BESONDERS);

            //get string that will be encoded as QR-Code
            String qrCodeRepresentation = receiptPackage.getQRCodeRepresentation();

            String signatureValue = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation, MachineCodeValue.SIGNATURE_VALUE);
            String decodedSignatureValue = new String(CashBoxUtils.base64Decode(signatureValue, false));
            boolean secDeviceWasDamaged = "Sicherheitseinrichtung ausgefallen".equals(decodedSignatureValue);
            if (secDeviceWasDamaged) {
                PDFont fontPlain = PDType1Font.HELVETICA;
                cos.beginText();
                cos.setFont(fontPlain, 8);
                cos.moveTextPositionByAmount(20, rect.getHeight() - 20 * (line));
                cos.drawString("SICHERHEITSEINRICHTUNG AUSGEFALLEN");
                cos.endText();
                line++;
            }

            //add OCR code or QR-code
            if (receiptPrintType == ReceiptPrintType.OCR) {
                addOCRCodeToPDF(document, cos, rect, line++, receiptPackage);
            } else {
                //create QRCode
                BufferedImage image = createQRCode(receiptPackage);
                //add QRCode to PDF document
                PDXObjectImage ximage = new PDPixelMap(document, image);
                float scale = 2f; // alter this value to set the image size
                cos.drawXObject(ximage, 25, 0, ximage.getWidth() * scale, ximage.getHeight() * scale);
            }
            cos.close();

            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            document.save(bOut);
            document.close();
            return bOut.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void addTaxTypeToPDF(PDPageContentStream cos, PDRectangle rect, int line, double sum, TaxType taxType) {
        PDFont fontPlain = PDType1Font.HELVETICA;
        try {
            cos.beginText();
            cos.setFont(fontPlain, 8);
            cos.moveTextPositionByAmount(20, rect.getHeight() - 20 * (line));
            cos.drawString(taxType.getTaxTypeString() + ": " + sum);
            cos.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void addOCRCodeToPDF(PDDocument doc, PDPageContentStream cos, PDRectangle rect, int line, ReceiptPackage receiptPackage) {
        try {
            //load OCR-A font
            InputStream inputStreamAFM = this.getClass().getClassLoader().getResourceAsStream("OCRA.afm");
            InputStream inputStreamPFB = this.getClass().getClassLoader().getResourceAsStream("OCRA.pfb");
            PDFont font = new PDType1AfmPfbFont(doc, inputStreamAFM, inputStreamPFB);

            //get machine code as OCR representation
            String ocrRepresentation = receiptPackage.getOcrCodeRepresentation();

            //print OCR rep to PDF document
            int CHARS_PER_LINE = 40;
            int index = 0;
            while (index >= 0) {
                String partOCR;
                if (ocrRepresentation.length() > CHARS_PER_LINE) {
                    partOCR = ocrRepresentation.substring(0, CHARS_PER_LINE);
                    ocrRepresentation = ocrRepresentation.substring(CHARS_PER_LINE);
                } else {
                    partOCR = ocrRepresentation.substring(0);
                    index = -1;
                }
                cos.beginText();
                cos.setFont(font, 8);
                cos.moveTextPositionByAmount(20, rect.getHeight() - 20 * (line));
                cos.drawString(partOCR);
                cos.endText();

                line++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected BufferedImage createQRCode(ReceiptPackage receiptPackage) {
        try {

            String qrCodeRepresentation = receiptPackage.getQRCodeRepresentation();
            //create QR-Code
            int size = 128;

            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeRepresentation, BarcodeFormat.QR_CODE, size, size, hintMap);

            int crunchifyWidth = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(crunchifyWidth, crunchifyWidth, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, crunchifyWidth, crunchifyWidth);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < crunchifyWidth; i++) {
                for (int j = 0; j < crunchifyWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            return image;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
