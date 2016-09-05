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

import at.asitplus.regkassen.common.MachineCodeValue;
import at.asitplus.regkassen.common.TaxType;
import at.asitplus.regkassen.common.util.CashBoxUtils;
import at.asitplus.regkassen.core.base.receiptdata.ReceiptPackage;

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

    @Override
    public List<byte[]> printReceipt(final java.util.List<ReceiptPackage> receiptPackages, final ReceiptPrintType receiptPrintType) {
        final List<byte[]> pdfReceipts = new ArrayList<byte[]>();
        for (final ReceiptPackage receiptPackage : receiptPackages) {
            pdfReceipts.add(printReceipt(receiptPackage, receiptPrintType));
        }
        return pdfReceipts;
    }

    @Override
    public byte[] printReceipt(final ReceiptPackage receiptPackage, final ReceiptPrintType receiptPrintType) {
       //TODO Training/Storno!
        try {
            //init PDF document
            final PDDocument document = new PDDocument();
            final PDPage page = new PDPage(PDPage.PAGE_SIZE_A6);
            document.addPage(page);

            //init content objects
            final PDRectangle rect = page.getMediaBox();
            final PDPageContentStream cos = new PDPageContentStream(document, page);

            //add taxtype-sums
            int line = 1;
            //get string that will be encoded as QR-Code
            final String qrCodeRepresentation = CashBoxUtils.getQRCodeRepresentationFromJWSCompactRepresentation(receiptPackage.getJwsCompactRepresentation());

            addTaxTypeToPDF(cos, rect, line++, CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,MachineCodeValue.SUM_TAX_SET_NORMAL),TaxType.SATZ_NORMAL);
            addTaxTypeToPDF(cos, rect, line++, CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,MachineCodeValue.SUM_TAX_SET_ERMAESSIGT1),TaxType.SATZ_ERMAESSIGT_1);
            addTaxTypeToPDF(cos, rect, line++, CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,MachineCodeValue.SUM_TAX_SET_ERMAESSIGT2),TaxType.SATZ_ERMAESSIGT_2);
            addTaxTypeToPDF(cos, rect, line++, CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,MachineCodeValue.SUM_TAX_SET_BESONDERS),TaxType.SATZ_BESONDERS);
            addTaxTypeToPDF(cos, rect, line++, CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,MachineCodeValue.SUM_TAX_SET_NULL),TaxType.SATZ_NULL);

            final String signatureValue = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation, MachineCodeValue.SIGNATURE_VALUE);
            final String decodedSignatureValue = new String(CashBoxUtils.base64Decode(signatureValue, false));
            final boolean secDeviceWasDamaged = "Sicherheitseinrichtung ausgefallen".equals(decodedSignatureValue);
            if (secDeviceWasDamaged) {
                final PDFont fontPlain = PDType1Font.HELVETICA;
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
                final BufferedImage image = createQRCode(receiptPackage);
                //add QRCode to PDF document
                final PDXObjectImage ximage = new PDPixelMap(document, image);
                final float scale = 2f; // alter this value to set the image size
                cos.drawXObject(ximage, 25, 0, ximage.getWidth() * scale, ximage.getHeight() * scale);
            }
            cos.close();

            final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            document.save(bOut);
            document.close();
            return bOut.toByteArray();

        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final COSVisitorException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void addTaxTypeToPDF(final PDPageContentStream cos, final PDRectangle rect, final int line, final String sum, final TaxType taxType) {
        final PDFont fontPlain = PDType1Font.HELVETICA;
        try {
            cos.beginText();
            cos.setFont(fontPlain, 8);
            cos.moveTextPositionByAmount(20, rect.getHeight() - 20 * (line));
            cos.drawString(taxType.getTaxTypeString() + ": " + sum);
            cos.endText();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    protected void addOCRCodeToPDF(final PDDocument doc, final PDPageContentStream cos, final PDRectangle rect, int line, final ReceiptPackage receiptPackage) {
        try {
            //load OCR-A font
            final InputStream inputStreamAFM = this.getClass().getClassLoader().getResourceAsStream("OCRA.afm");
            final InputStream inputStreamPFB = this.getClass().getClassLoader().getResourceAsStream("OCRA.pfb");
            final PDFont font = new PDType1AfmPfbFont(doc, inputStreamAFM, inputStreamPFB);

            //get machine code as OCR representation
            String ocrRepresentation = CashBoxUtils.getOCRCodeRepresentationFromJWSCompactRepresentation(receiptPackage.getJwsCompactRepresentation());

            //print OCR rep to PDF document
            final int CHARS_PER_LINE = 40;
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


        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    protected BufferedImage createQRCode(final ReceiptPackage receiptPackage) {
        try {

            final String qrCodeRepresentation = CashBoxUtils.getQRCodeRepresentationFromJWSCompactRepresentation(receiptPackage.getJwsCompactRepresentation());

            //create QR-Code
            final int size = 128;

            final Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            final QRCodeWriter qrCodeWriter = new QRCodeWriter();

            final BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeRepresentation, BarcodeFormat.QR_CODE, size, size, hintMap);

            final int crunchifyWidth = byteMatrix.getWidth();
            final BufferedImage image = new BufferedImage(crunchifyWidth, crunchifyWidth, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            final Graphics2D graphics = (Graphics2D) image.getGraphics();
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
        } catch (final WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
