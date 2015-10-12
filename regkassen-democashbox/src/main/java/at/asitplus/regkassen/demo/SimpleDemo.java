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

package at.asitplus.regkassen.demo;

import at.asitplus.regkassen.core.DemoCashBox;
import at.asitplus.regkassen.core.base.receiptdata.RawReceiptData;
import at.asitplus.regkassen.core.base.receiptdata.ReceiptPackage;
import at.asitplus.regkassen.core.base.rksuite.RKSuite;
import at.asitplus.regkassen.core.base.util.CashBoxUtils;
import at.asitplus.regkassen.core.base.util.RandomReceiptGenerator;
import at.asitplus.regkassen.core.modules.DEP.DEPExportFormat;
import at.asitplus.regkassen.core.modules.DEP.SimpleMemoryDEPModule;
import at.asitplus.regkassen.core.modules.init.CashBoxParameters;
import at.asitplus.regkassen.core.modules.print.PrinterModule;
import at.asitplus.regkassen.core.modules.print.ReceiptPrintType;
import at.asitplus.regkassen.core.modules.print.SimplePDFPrinterModule;
import at.asitplus.regkassen.core.modules.signature.jws.ComNimbusdsJwsModule;
import at.asitplus.regkassen.core.modules.signature.jws.JWSModule;
import at.asitplus.regkassen.core.modules.signature.rawsignatureprovider.DO_NOT_USE_IN_REAL_CASHBOX_DemoSoftwareSignatureModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SimpleDemo {

    public static void main(String[] args) {
        try {

            //IMPORTANT HINT REGARDING STRING ENCODING
            //in Java all Strings have UTF-8 as default encoding
            //therefore: there are only a few references to UTF-8 encoding in this demo code
            //however, if values are retrieved from a database or another program language is used, then one needs to
            //make sure that the UTF-8 encoding is correctly implemented

            // create CMD line option object
            Options options = new Options();

            // add CMD line options
            options.addOption("o", "output-dir", true, "specify base output directory, if none is specified a new directory will be created in the current path");
            options.addOption("n", "number-of-generated-receipts", true, "specify number of receipts to be randomly generated, 15 is default");

            ///parse CMD line options
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            String outputParentDirectoryString = cmd.getOptionValue("o");
            if (outputParentDirectoryString == null) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
                outputParentDirectoryString = "./CashBoxDemoOutput" + df.format(new Date());
            }
            File OUTPUT_PARENT_DIRECTORY = new File(outputParentDirectoryString);
            OUTPUT_PARENT_DIRECTORY.mkdirs();

            String numberOfReceiptsString = cmd.getOptionValue("n");
            int NUMBER_OF_RECEIPTS = 15;
            if (numberOfReceiptsString != null) {
                NUMBER_OF_RECEIPTS = new Integer(numberOfReceiptsString);
            }

            System.out.println("Setting workdir to " + OUTPUT_PARENT_DIRECTORY.getAbsolutePath());

            //TODO add provider independent functionality
            //initialise cryptographic providers
            Security.addProvider(new BouncyCastleProvider());

            //prepare cashbox init parameters
            CashBoxParameters cashBoxParameters = new CashBoxParameters();

            //generate and set random cash box ID ("Kassen-ID")
            //REF TO SPECIFICATION: Detailspezifikation/Abs 4
            String CASH_BOX_ID = "DEMO-CASH-BOX" + Math.round(Math.random() * 1000);
            cashBoxParameters.setCashBoxID(CASH_BOX_ID);

            //set cashbox suite
            //REF TO SPECIFICATION: Detailspezifikation/Abs 2
            //AT0 is used here for demonstration purposes, see Abs 2 for details on AT0
            cashBoxParameters.setRkSuite(RKSuite.R1_AT0);

            //set initial receipt identifier
            //in this demo cashbox integer values are used as receipt identifiers ("Belegnummer"), however the specification does not
            //impose that limit. An arbitrary UTF-8 String could be used, the only requirement is that the same combination of
            //the cashBox ID ("Kassen-ID") and the receipt identifiert ("Belegnummer") is NEVER used for more than one receipt
            //using the same multiple times compromises the security of the encrypted turnover value, which might lead
            //to leaked turnover data.
            //REF TO SPECIFICATION: Detailspezifikation/Abs 4, Abs 8, Abs 9, Abs 10
            long initialReceiptIdentifier = Math.round(Math.random() * 1000000);
            cashBoxParameters.setInitialReceiptIdentifier(initialReceiptIdentifier);

            //set DEP module for storing and exporting receipts
            //REF TO SPECIFICATION: Detailspezifikation/Abs 3, 11
            cashBoxParameters.setDepModul(new SimpleMemoryDEPModule());

            //create random AES key for turnover encryption
            //REF TO SPECIFICATION: Detailspezifikation/Abs 4, Abs 8, Abs 9, Abs 10
            cashBoxParameters.setTurnoverKeyAESkey(CashBoxUtils.createAESKey());

            //set up signature module
            //the signature module is composed of an JWS module that create the JSON Web Signature (JWS) and
            //a low level signature module for signing the hash values.
            //REF TO SPECIFICATION: Detailspezifikation/Abs 2, Abs 4, Abs 5, Abs 6

            //JWSModule jwsModule = new OrgBitbucketBcJwsModule();  //requires bouncycastle provider
            JWSModule jwsModule = new ComNimbusdsJwsModule();   //allows for provider independent use cases
            jwsModule.setSignatureModule(new DO_NOT_USE_IN_REAL_CASHBOX_DemoSoftwareSignatureModule());
            cashBoxParameters.setJwsModule(jwsModule);

            //set printer module
            //REF TO SPECIFICATION: Detailspezifikation/Abs 12, Abs 13, Abs 14, Abs 15
            PrinterModule printerModule = new SimplePDFPrinterModule();
            cashBoxParameters.setPrinterModule(printerModule);

            //init the cash box with the parameters
            DemoCashBox demoCashBox = new DemoCashBox(cashBoxParameters);

            //init done, start interaction with cashbox
            //create random receipt data that will be handled by the cashbox
            List<RawReceiptData> receipts = RandomReceiptGenerator.generateRandomReceipts(NUMBER_OF_RECEIPTS);

            //store first receipt (Starbeleg) in cashbox
            //all taxtype values are set to zero (per default in this demo)
            RawReceiptData firstReceipt = new RawReceiptData();
            demoCashBox.storeReceipt(firstReceipt);

            //now store the other receipts
            for (RawReceiptData rawReceiptData : receipts) {
                //store receipt within cashbox: (prepare data-to-be-signed, sign with JWS, store signed receipt in DEP)
                demoCashBox.storeReceipt(rawReceiptData);
            }

            //dump machine readable code of receipts (this "code" is used for the QR-codes)
            //REF TO SPECIFICATION: Detailspezifikation/Abs 12
            //dump to File
            File qrCoreRepExportFile = new File(OUTPUT_PARENT_DIRECTORY, "qr-code-rep.txt");
            List<ReceiptPackage> receiptPackages = demoCashBox.getStoredReceipts();
            PrintWriter writer = new PrintWriter(new FileWriter(qrCoreRepExportFile));
            System.out.println("------------QR-CODE-REP------------");
            for (ReceiptPackage receiptPackage : receiptPackages) {
                System.out.println(receiptPackage.getQRCodeRepresentation());
                writer.println(receiptPackage.getQRCodeRepresentation());
            }
            System.out.println("");
            writer.close();


            //dump OCR code of receipts
            //REF TO SPECIFICATION: Detailspezifikation/Abs 14
            //dump to File
            File ocrCoreRepExportFile = new File(OUTPUT_PARENT_DIRECTORY, "ocr-code-rep.txt");
            writer = new PrintWriter(new FileWriter(ocrCoreRepExportFile));
            System.out.println("------------OCR-CODE-REP------------");
            for (ReceiptPackage receiptPackage : receiptPackages) {
                System.out.println(receiptPackage.getOcrCodeRepresentation());
                writer.println(receiptPackage.getOcrCodeRepresentation());
            }
            System.out.println("");
            writer.close();

            //export DEP from cashbox
            //REF TO SPECIFICATION: Detailspezifikation/Abs 3
            DEPExportFormat depExportFormat = demoCashBox.exportDEP();

            //get JSON rep and dump export format to file/std output
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String exportFormatJSONString = gson.toJson(depExportFormat);
            System.out.println("------------DEP-EXPORT-FORMAT------------");
            System.out.println(exportFormatJSONString);
            System.out.println("");

            //dump DEP export to file
            File depExportFile = new File(OUTPUT_PARENT_DIRECTORY, "dep-export.txt");
            FileOutputStream outputStream = new FileOutputStream(depExportFile);
            outputStream.write(exportFormatJSONString.getBytes());
            outputStream.close();

            //export receipts as PDF (QR-CODE)
            //REF TO SPECIFICATION: Detailspezifikation/Abs 12, Abs 13
            File qrCodeDumpDirectory = new File(OUTPUT_PARENT_DIRECTORY, "qr-code-dir");
            qrCodeDumpDirectory.mkdirs();
            List<byte[]> printedQRCodeReceipts = demoCashBox.printReceipt(receiptPackages, ReceiptPrintType.QR_CODE);
            CashBoxUtils.writeReceiptsToFiles(printedQRCodeReceipts, "QR-", qrCodeDumpDirectory);

            //export receipts as PDF (OCR)
            //REF TO SPECIFICATION: Detailspezifikation/Abs 14, Abs 15
            File ocrCodeDumpDirectory = new File(OUTPUT_PARENT_DIRECTORY, "ocr-code-dir");
            ocrCodeDumpDirectory.mkdirs();
            List<byte[]> printedOCRCodeReceipts = demoCashBox.printReceipt(receiptPackages, ReceiptPrintType.OCR);
            CashBoxUtils.writeReceiptsToFiles(printedOCRCodeReceipts, "OCR-", ocrCodeDumpDirectory);

            //store signature certificate
            X509Certificate signatureCertificate = cashBoxParameters.getJwsModule().getSignatureModule().getSigningCertificate();
            File signatureCertificateOutputFile = new File(OUTPUT_PARENT_DIRECTORY,"signatureCertificate.cer");
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(signatureCertificateOutputFile));
            ByteArrayInputStream bIn = new ByteArrayInputStream(signatureCertificate.getEncoded());
            IOUtils.copy(bIn,bufferedOutputStream);
            bufferedOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
    }

}
