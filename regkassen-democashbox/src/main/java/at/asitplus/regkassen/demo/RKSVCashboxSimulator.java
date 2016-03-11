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
import at.asitplus.regkassen.core.base.cashboxsimulation.CashBoxSimulation;
import at.asitplus.regkassen.core.base.cashboxsimulation.CertificateOrPublicKeyContainer;
import at.asitplus.regkassen.core.base.cashboxsimulation.CryptographicMaterialContainer;
import at.asitplus.regkassen.core.base.cashboxsimulation.SignatureDeviceType;
import at.asitplus.regkassen.core.base.receiptdata.ReceiptPackage;
import at.asitplus.regkassen.core.base.rksuite.RKSuite;
import at.asitplus.regkassen.core.base.util.CashBoxUtils;
import at.asitplus.regkassen.core.base.util.CryptoUtil;
import at.asitplus.regkassen.core.modules.DEP.DEPExportFormat;
import at.asitplus.regkassen.core.modules.DEP.SimpleMemoryDEPModule;
import at.asitplus.regkassen.core.modules.init.CashBoxParameters;
import at.asitplus.regkassen.core.modules.print.ReceiptPrintType;
import at.asitplus.regkassen.core.modules.print.SimplePDFPrinterModule;
import at.asitplus.regkassen.core.modules.signature.jws.JWSModule;
import at.asitplus.regkassen.core.modules.signature.jws.ManualJWSModule;
import at.asitplus.regkassen.core.modules.signature.rawsignatureprovider.NEVER_USE_IN_A_REAL_SYSTEM_SoftwareCertificateOpenSystemSignatureModule;
import at.asitplus.regkassen.core.modules.signature.rawsignatureprovider.NEVER_USE_IN_A_REAL_SYSTEM_SoftwareKeySignatureModule;
import at.asitplus.regkassen.core.modules.signature.rawsignatureprovider.SignatureModule;
import at.asitplus.regkassen.demo.testsuites.TestSuiteGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RKSVCashboxSimulator {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static boolean VERBOSE;
    public static boolean CLOSED_SYSTEM;


    public static void main(String[] args) {
        try {
            //IMPORTANT HINT REGARDING STRING ENCODING
            //in Java all Strings have UTF-8 as default encoding
            //therefore: there are only a few references to UTF-8 encoding in this demo code
            //however, if values are retrieved from a database or another program language is used, then one needs to
            //make sure that the UTF-8 encoding is correctly implemented

            //this demo cashbox does not implement error handling
            //it should only demonstrate the core elements of the RKSV and any boilerplate code is avoided as much as possible
            //if an error occurs, only the stacktraces are logged
            //obviously this needs to be adapted in a productive cashbox


            //----------------------------------------------------------------------------------------------------
            //basic inits
            //add bouncycastle provider
            Security.addProvider(new BouncyCastleProvider());


            //----------------------------------------------------------------------------------------------------
            //check if unlimited strength policy files are installed, they are required for strong crypto algorithms ==> AES 256
            if (!CryptoUtil.isUnlimitedStrengthPolicyAvailable()) {
                System.out.println("Your JVM does not provide the unlimited strength policy. However, this policy is required to enable strong cryptography (e.g. AES with 256 bits). Please install the required policy files.");
                System.exit(0);
            }


            //----------------------------------------------------------------------------------------------------
            //parse cmd line options
            Options options = new Options();

            // add CMD line options
            options.addOption("o", "output-dir", true, "specify base output directory, if none is specified, a new directory will be created in the current working directory");
            //options.addOption("i", "simulation-file-or-directory", true, "cashbox simulation (file) or multiple cashbox simulation files (directory), if none is specified the internal test suites will be executed (can also be considered as demo mode)");
            options.addOption("v", "verbose", false, "dump demo receipts to cmd line");
            options.addOption("c","closed system", false, "simulate closed system");


            ///parse CMD line options
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            //setup inputs from cmd line
            //verbose
            VERBOSE = cmd.hasOption("v");
            CLOSED_SYSTEM = cmd.hasOption("c");


            //output directory
            String outputParentDirectoryString = cmd.getOptionValue("o");
            if (outputParentDirectoryString == null) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
                outputParentDirectoryString = "./CashBoxDemoOutput" + df.format(new Date());
            }
            File OUTPUT_PARENT_DIRECTORY = new File(outputParentDirectoryString);
            OUTPUT_PARENT_DIRECTORY.mkdirs();


            //----------------------------------------------------------------------------------------------------
            //external simulation runs... not implemented yet, currently only the internal test suites can be executed
            //String simulationFileOrDirectoryPath = cmd.getOptionValue("i");
            //handling of arbitrary input simulation files will be possible in 0.7
            //if (simulationFileOrDirectoryPath == null) {
            //} else {
//                File simulationFileOrDirectory = new File(simulationFileOrDirectoryPath);
//                cashBoxSimulationList = readCashBoxSimulationFromFile(simulationFileOrDirectory);
            //}

            List<CashBoxSimulation> cashBoxSimulationList = TestSuiteGenerator.getSimulationRuns();

            //setup simulation and execute
            int index = 1;
            for (CashBoxSimulation cashboxSimulation : cashBoxSimulationList) {
                System.out.println("Executing simulation run " + index + "/" + cashBoxSimulationList.size());
                System.out.println("Simulation run: " + cashboxSimulation.getSimulationRunLabel());
                index++;

                File testSetDirectory = new File(OUTPUT_PARENT_DIRECTORY, cashboxSimulation.getSimulationRunLabel());
                testSetDirectory.mkdirs();

                CashBoxParameters cashBoxParameters = new CashBoxParameters();
                cashBoxParameters.setCashBoxId(cashboxSimulation.getCashBoxId());
                cashBoxParameters.setTurnOverCounterAESKey(CryptoUtil.convertBase64KeyToSecretKey(cashboxSimulation.getBase64AesKey()));
                cashBoxParameters.setDepModul(new SimpleMemoryDEPModule());
                cashBoxParameters.setPrinterModule(new SimplePDFPrinterModule());
                cashBoxParameters.setCompanyID(cashboxSimulation.getCompanyID());

                //create pre-defined number of signature devices
                for (int i = 0; i < cashboxSimulation.getNumberOfSignatureDevices(); i++) {
                    JWSModule jwsModule = new ManualJWSModule();
                    SignatureModule signatureModule;
                    if (!CLOSED_SYSTEM) {
                        signatureModule = new NEVER_USE_IN_A_REAL_SYSTEM_SoftwareCertificateOpenSystemSignatureModule(RKSuite.R1_AT100, null);
                    } else {
                        signatureModule = new NEVER_USE_IN_A_REAL_SYSTEM_SoftwareKeySignatureModule(cashboxSimulation.getCompanyID() + "-" + "K" + i);
                    }
                    jwsModule.setOpenSystemSignatureModule(signatureModule);
                    cashBoxParameters.getJwsSignatureModules().add(jwsModule);
                }

                //init cashbox
                DemoCashBox demoCashBox = new DemoCashBox(cashBoxParameters);

                //exceute simulation run
                demoCashBox.executeSimulation(cashboxSimulation.getCashBoxInstructionList());


                //----------------------------------------------------------------------------------------------------
                //export DEP
                DEPExportFormat depExportFormat = demoCashBox.exportDEP();
                //get JSON rep and dump export format to file/std output
                File depExportFile = new File(testSetDirectory, "dep-export.json");
                dumpJSONRepOfObject(depExportFormat,depExportFile,true,"------------DEP-EXPORT-FORMAT------------");


                //----------------------------------------------------------------------------------------------------
                //store signature certificates and AES key (so that they can be used for verification purposes)
                CryptographicMaterialContainer cryptographicMaterialContainer = new CryptographicMaterialContainer();
                HashMap<String, CertificateOrPublicKeyContainer> certificateContainerMap = new HashMap<>();
                cryptographicMaterialContainer.setCertificateOrPublicKeyMap(certificateContainerMap);

                //store AES key as BASE64 String
                //ATTENTION, this is only for demonstration purposes, the AES key must be stored in a secure location
                cryptographicMaterialContainer.setBase64AESKey(cashboxSimulation.getBase64AesKey());
                List<JWSModule> jwsSignatureModules = demoCashBox.getCashBoxParameters().getJwsSignatureModules();
                for (JWSModule jwsSignatureModule : jwsSignatureModules) {
                    CertificateOrPublicKeyContainer certificateOrPublicKeyContainer = new CertificateOrPublicKeyContainer();
                    certificateOrPublicKeyContainer.setId(jwsSignatureModule.getSerialNumberOfKeyID());
                    certificateContainerMap.put(jwsSignatureModule.getSerialNumberOfKeyID(), certificateOrPublicKeyContainer);
                    X509Certificate certificate = (X509Certificate) jwsSignatureModule.getSignatureModule().getSigningCertificate();
                    if (certificate == null) {
                        //must be public key based... (closed system)
                        PublicKey publicKey = jwsSignatureModule.getSignatureModule().getSigningPublicKey();
                        certificateOrPublicKeyContainer.setSignatureCertificateOrPublicKey(CashBoxUtils.base64Encode(publicKey.getEncoded(), false));
                        certificateOrPublicKeyContainer.setSignatureDeviceType(SignatureDeviceType.PUBLIC_KEY);
                    } else {
                        certificateOrPublicKeyContainer.setSignatureCertificateOrPublicKey(CashBoxUtils.base64Encode(certificate.getEncoded(), false));
                        certificateOrPublicKeyContainer.setSignatureDeviceType(SignatureDeviceType.CERTIFICATE);
                    }
                }

                File cryptographicMaterialContainerFile = new File(testSetDirectory, "cryptographicMaterialContainer.json");
                dumpJSONRepOfObject(cryptographicMaterialContainer,cryptographicMaterialContainerFile,true,"------------CRYPTOGRAPHIC MATERIAL------------");


                //----------------------------------------------------------------------------------------------------
                //export QR codes to file
                //dump machine readable code of receipts (this "code" is used for the QR-codes)
                //REF TO SPECIFICATION: Detailspezifikation/Abs 12
                //dump to File
                File qrCoreRepExportFile = new File(testSetDirectory, "qr-code-rep.json");
                List<ReceiptPackage> receiptPackages = demoCashBox.getStoredReceipts();
                List<String> qrCodeRepList = new ArrayList<>();
                for (ReceiptPackage receiptPackage : receiptPackages) {
                    qrCodeRepList.add(CashBoxUtils.getQRCodeRepresentationFromJWSCompactRepresentation(receiptPackage.getJwsCompactRepresentation()));
                }
                dumpJSONRepOfObject(qrCodeRepList,qrCoreRepExportFile,true,"------------QR-CODE-REP------------");


                //----------------------------------------------------------------------------------------------------
                //export OCR codes to file
                //dump machine readable code of receipts (this "code" is used for the OCR-codes)
                //REF TO SPECIFICATION: Detailspezifikation/Abs 14
                //dump to File
                File ocrCoreRepExportFile = new File(testSetDirectory, "ocr-code-rep.json");
                List<String> ocrCodeRepList = new ArrayList<>();
                for (ReceiptPackage receiptPackage : receiptPackages) {
                    ocrCodeRepList.add(CashBoxUtils.getOCRCodeRepresentationFromJWSCompactRepresentation(receiptPackage.getJwsCompactRepresentation()));
                }
                dumpJSONRepOfObject(ocrCodeRepList,ocrCoreRepExportFile,true,"------------OCR-CODE-REP------------");


                //----------------------------------------------------------------------------------------------------
                //create PDF receipts and print to directory
                //REF TO SPECIFICATION: Detailspezifikation/Abs 12
                File qrCodeDumpDirectory = new File(testSetDirectory, "qr-code-dir-pdf");
                qrCodeDumpDirectory.mkdirs();
                List<byte[]> printedQRCodeReceipts = demoCashBox.printReceipt(receiptPackages, ReceiptPrintType.QR_CODE);
                CashBoxUtils.writeReceiptsToFiles(printedQRCodeReceipts, "QR-", qrCodeDumpDirectory);


                //----------------------------------------------------------------------------------------------------
                //export receipts as PDF (OCR)
                //REF TO SPECIFICATION: Detailspezifikation/Abs 14
                File ocrCodeDumpDirectory = new File(testSetDirectory, "ocr-code-dir-pdf");
                ocrCodeDumpDirectory.mkdirs();
                List<byte[]> printedOCRCodeReceipts = demoCashBox.printReceipt(receiptPackages, ReceiptPrintType.OCR);
                CashBoxUtils.writeReceiptsToFiles(printedOCRCodeReceipts, "OCR-", ocrCodeDumpDirectory);


                //----------------------------------------------------------------------------------------------------
                //dump executed testsuite
                File testSuiteDumpFile = new File(testSetDirectory, cashboxSimulation.getSimulationRunLabel()+".json");
                dumpJSONRepOfObject(cashboxSimulation,testSuiteDumpFile,true,"------------CASHBOX Simulation------------");
            }
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * helper method to parse cashbox simulation files or directories containing multiple simulation files
     * @param inputFileOrDirectory simulation input file/simulation input directory
     * @return simulation package used to run the cashbox simulation
     */
    public static List<CashBoxSimulation> readCashBoxSimulationFromFile(File inputFileOrDirectory) {
        List<CashBoxSimulation> cashBoxSimulationList = new ArrayList<>();
        try {
            if (inputFileOrDirectory.isDirectory()) {
                File[] inputFileList = inputFileOrDirectory.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.getName().endsWith(".testrun");
                    }
                });
                for (File simulationFile : inputFileList) {
                    cashBoxSimulationList.addAll(readCashBoxSimulationFromFile(simulationFile));
                }
            } else {
                BufferedInputStream bIn = new BufferedInputStream(new FileInputStream(inputFileOrDirectory));
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                IOUtils.copy(bIn, bOut);
                CashBoxSimulation cashBoxSimulation = (CashBoxSimulation) gson.fromJson(new String(bOut.toByteArray()), CashBoxSimulation.class);
                cashBoxSimulationList.add(cashBoxSimulation);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return cashBoxSimulationList;
    }

    /**
     * helper method for writing JSON rep of objects to files/stdout
     * @param object arbitrary object, that should be converted to JSON string
     * @param outputFile output file for storing JSON rep of object
     * @param stdout output to stdout?
     * @param stdoutHeadLine if output to stdout, use this header line
     */
    public static void dumpJSONRepOfObject(Object object,File outputFile,boolean stdout,String stdoutHeadLine) {
        try {
            String jsonString = gson.toJson(object);
            if (stdout && VERBOSE) {
                System.out.println();
                System.out.println(stdoutHeadLine);
                System.out.println(jsonString);
                System.out.println();
            }
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
