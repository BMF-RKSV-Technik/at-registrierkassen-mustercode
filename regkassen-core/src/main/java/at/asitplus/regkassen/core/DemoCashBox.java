/*
 * Copyright (C) 2015, 2016
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

package at.asitplus.regkassen.core;

import at.asitplus.regkassen.common.RKSuite;
import at.asitplus.regkassen.common.TypeOfReceipt;
import at.asitplus.regkassen.common.util.CashBoxUtils;
import at.asitplus.regkassen.common.util.CryptoUtil;
import at.asitplus.regkassen.core.base.cashboxsimulation.CashBoxInstruction;
import at.asitplus.regkassen.core.base.receiptdata.ReceiptPackage;
import at.asitplus.regkassen.core.base.receiptdata.ReceiptRepresentationForSignature;
import at.asitplus.regkassen.core.base.receiptdata.SimplifiedReceipt;
import at.asitplus.regkassen.core.modules.DEP.DEPExportFormat;
import at.asitplus.regkassen.core.modules.init.CashBoxParameters;
import at.asitplus.regkassen.core.modules.print.ReceiptPrintType;
import at.asitplus.regkassen.core.modules.signature.jws.JWSModule;
import org.apache.commons.math3.util.Precision;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Simple demonstration CashBox, can be initialized with different modules (signature, DEP, print)
 */
public class DemoCashBox {
    //parameters for cashbox initialisation (AES key, cashbox ID etc.)
    protected CashBoxParameters cashBoxParameters;

    //internal turnover counter... starts with 0
    protected long turnoverCounter = 0;

    public DemoCashBox(CashBoxParameters cashBoxParameters) {
        this.cashBoxParameters = cashBoxParameters;
    }

    /**
     * @return cashboxparameter set, used to setup/initialize the cashbox
     */
    public CashBoxParameters getCashBoxParameters() {
        return cashBoxParameters;
    }

    /**
     * since the 0.6 version of the demo code, this is the only way to run the cashbox,
     *
     * @param cashBoxInstructions list of instructions which tell the cashbox to create the specified receipts
     */
    public void executeSimulation(List<CashBoxInstruction> cashBoxInstructions) {
        for (CashBoxInstruction cashBoxInstruction : cashBoxInstructions) {
            createStoreAndSignReceiptPackage(cashBoxInstruction);
        }
    }

    /**
     * export of the DEP
     * @return DEP Export
     */
    public DEPExportFormat exportDEP() {
        return cashBoxParameters.getDepModul().exportDEP();
    }

    /**
     * get all receipts from DEP
     *
     * @return receipts stored in DEP
     */
    public List<ReceiptPackage> getStoredReceipts() {
        return cashBoxParameters.getDepModul().getStoredReceipts();
    }

    /**
     * print a given receipt
     *
     * @param receiptPackage   receipt data structure
     * @param receiptPrintType type of printed receipt (QR-code, OCR-code)
     * @return receipt as PDF-blob
     */
    public byte[] printReceipt(ReceiptPackage receiptPackage, ReceiptPrintType receiptPrintType) {
        return cashBoxParameters.getPrinterModule().printReceipt(receiptPackage, receiptPrintType);
    }

    public List<byte[]> printReceipt(List<ReceiptPackage> receiptPackageList, ReceiptPrintType receiptPrintType) {
        return cashBoxParameters.getPrinterModule().printReceipt(receiptPackageList, receiptPrintType);
    }

    /**
     * create and store a receipt according to the data in the instruction
     *
     * @param cashBoxInstruction
     */
    protected synchronized void createStoreAndSignReceiptPackage(CashBoxInstruction cashBoxInstruction) {
        //get signature device and used RKSUITE
        //as of version 6 the cashbox can be instructed to use a specific signature device
        JWSModule signatureDevice = cashBoxParameters.getJwsSignatureModules().get(cashBoxInstruction.getUsedSignatureDevice());
        RKSuite rkSuiteOfSignatureDevice = signatureDevice.getRKSuite();

        //prepare elements of the machine code representation
        RKSuite el1_rkSuite;
        String el2_cashboxID;
        String el3_receiptID;
        Date el4_timeAndData;
        double el5_taxSet_NORMAL;
        double el6_taxSet_ERMAESSIGT1;
        double el7_taxSet_ERMAESSIGT2;
        double el8_taxSet_NULL;
        double el9_taxSet_BESONDERS;
        String el10_encryptedTurnOverValue;
        String el11_certificateSerialNumberOrCompanyAndKeyID;
        String el12_chainValue = "";

        //setup common values that need to be set for every type of receipt
        el1_rkSuite = signatureDevice.getRKSuite();
        el2_cashboxID = cashBoxParameters.getCashBoxId();
        el3_receiptID = cashBoxInstruction.getReceiptIdentifier();
        el4_timeAndData = null;
        
        if (cashBoxInstruction.getDateToUse() != null) {
            try {
                el4_timeAndData = CashBoxUtils.convertISO8601toDate(cashBoxInstruction.getDateToUse());
            } catch (ParseException e) {
                System.err.println("Fatal error, cannot parse date from cashbox instruction file: " + cashBoxInstruction.getDateToUse() +  " is not a valid date");
                System.exit(-1);
            }
       } else {
            el4_timeAndData = new Date();
       }


        //if we have an open system, the serial number of the certificate is used
        //for a closed system, the companyID and the key id of the signing key is used
        el11_certificateSerialNumberOrCompanyAndKeyID = signatureDevice.getSerialNumberOfKeyID();

        //extract type of receipt that needs to be generated
        TypeOfReceipt typeOfReceipt = cashBoxInstruction.getTypeOfReceipt();

        //get basic receipt data (tax sets)
        SimplifiedReceipt simplifiedReceipt = cashBoxInstruction.getSimplifiedReceipt();

        //generate receipt according to its type
        //this demo cash box is highly simplified and does not execute validity checks (e.g. it is not checked whether all tax sets of a START_BELEG are set to 0,00)
        //wrong cashbox instructions might lead to a crash, or a a wrong result
        //all checks are carried out by the validation tool
        //reason: this code as been strongly simplified in version 0.6 to highlight the essential procedures for each type of receipt
        if (typeOfReceipt == TypeOfReceipt.START_BELEG) {
            //in case of a START_BELEG all tax sets are set to 0.0
            el5_taxSet_NORMAL = 0;
            el6_taxSet_ERMAESSIGT1 = 0;
            el7_taxSet_ERMAESSIGT2 = 0;
            el8_taxSet_NULL = 0;
            el9_taxSet_BESONDERS = 0;

            //no need to update the turnover counter

            el10_encryptedTurnOverValue = encryptTurnOverCounter(cashBoxParameters.getCashBoxId(), el3_receiptID, rkSuiteOfSignatureDevice,cashBoxParameters.getTurnOverCounterLengthInBytes());
            //there is no previous receipt for chain value calculation, the cashboxID is used instead
            el12_chainValue = calculateChainValue(null, rkSuiteOfSignatureDevice);
        } else if (typeOfReceipt == TypeOfReceipt.STANDARD_BELEG) {
            el5_taxSet_NORMAL = Precision.round(simplifiedReceipt.getTaxSetNormal(), 2);
            el6_taxSet_ERMAESSIGT1 = Precision.round(simplifiedReceipt.getTaxSetErmaessigt1(), 2);
            el7_taxSet_ERMAESSIGT2 = Precision.round(simplifiedReceipt.getTaxSetErmaessigt2(), 2);
            el8_taxSet_NULL = Precision.round(simplifiedReceipt.getTaxSetNull(), 2);
            el9_taxSet_BESONDERS = Precision.round(simplifiedReceipt.getTaxSetBesonders(), 2);

            //update turnover counter by adding all tax sets
            updateTurnOverCounter(simplifiedReceipt);
            el10_encryptedTurnOverValue = encryptTurnOverCounter(cashBoxParameters.getCashBoxId(), el3_receiptID, rkSuiteOfSignatureDevice,cashBoxParameters.getTurnOverCounterLengthInBytes());
            if (getStoredReceipts().size() > 0) {
                el12_chainValue = calculateChainValue(getStoredReceipts().get(getStoredReceipts().size() - 1).getJwsCompactRepresentation(), rkSuiteOfSignatureDevice);
            }
        } else if (typeOfReceipt == TypeOfReceipt.STORNO_BELEG) {
            el5_taxSet_NORMAL = Precision.round(simplifiedReceipt.getTaxSetNormal(), 2);
            el6_taxSet_ERMAESSIGT1 = Precision.round(simplifiedReceipt.getTaxSetErmaessigt1(), 2);
            el7_taxSet_ERMAESSIGT2 = Precision.round(simplifiedReceipt.getTaxSetErmaessigt2(), 2);
            el8_taxSet_NULL = Precision.round(simplifiedReceipt.getTaxSetNull(), 2);
            el9_taxSet_BESONDERS = Precision.round(simplifiedReceipt.getTaxSetBesonders(), 2);

            updateTurnOverCounter(simplifiedReceipt);
            el10_encryptedTurnOverValue = CashBoxUtils.base64Encode("STO".getBytes(), false);
            if (getStoredReceipts().size() > 0) {
                el12_chainValue = calculateChainValue(getStoredReceipts().get(getStoredReceipts().size() - 1).getJwsCompactRepresentation(), rkSuiteOfSignatureDevice);
            }
        } else if (typeOfReceipt == TypeOfReceipt.TRAINING_BELEG) {
            el5_taxSet_NORMAL = Precision.round(simplifiedReceipt.getTaxSetNormal(), 2);
            el6_taxSet_ERMAESSIGT1 = Precision.round(simplifiedReceipt.getTaxSetErmaessigt1(), 2);
            el7_taxSet_ERMAESSIGT2 = Precision.round(simplifiedReceipt.getTaxSetErmaessigt2(), 2);
            el8_taxSet_NULL = Precision.round(simplifiedReceipt.getTaxSetNull(), 2);
            el9_taxSet_BESONDERS = Precision.round(simplifiedReceipt.getTaxSetBesonders(), 2);

            //turnover counter is not updated!

            el10_encryptedTurnOverValue = CashBoxUtils.base64Encode("TRA".getBytes(), false);
            if (getStoredReceipts().size() > 0) {
                el12_chainValue = calculateChainValue(getStoredReceipts().get(getStoredReceipts().size() - 1).getJwsCompactRepresentation(), rkSuiteOfSignatureDevice);
            }
        } else if (typeOfReceipt == TypeOfReceipt.NULL_BELEG) {
            //no need to update the turnover counter
            el5_taxSet_NORMAL = 0;
            el6_taxSet_ERMAESSIGT1 = 0;
            el7_taxSet_ERMAESSIGT2 = 0;
            el8_taxSet_NULL = 0;
            el9_taxSet_BESONDERS = 0;

            //turnover counter is not updated, since all tax-sets of a Nullbeleg are set to 0

            el10_encryptedTurnOverValue = encryptTurnOverCounter(cashBoxParameters.getCashBoxId(), el3_receiptID, rkSuiteOfSignatureDevice,cashBoxParameters.getTurnOverCounterLengthInBytes());
            if (getStoredReceipts().size() > 0) {
                el12_chainValue = calculateChainValue(getStoredReceipts().get(getStoredReceipts().size() - 1).getJwsCompactRepresentation(), rkSuiteOfSignatureDevice);
            }
        } else {
            return;
        }

        //create data structure that contains the data that needs to be signed
        ReceiptRepresentationForSignature receiptRepresentationForSignature = new ReceiptRepresentationForSignature();
        receiptRepresentationForSignature.setCashBoxID(el2_cashboxID);
        receiptRepresentationForSignature.setReceiptIdentifier(el3_receiptID);
        receiptRepresentationForSignature.setReceiptDateAndTime(el4_timeAndData);
        receiptRepresentationForSignature.setSumTaxSetNormal(el5_taxSet_NORMAL);
        receiptRepresentationForSignature.setSumTaxSetErmaessigt1(el6_taxSet_ERMAESSIGT1);
        receiptRepresentationForSignature.setSumTaxSetErmaessigt2(el7_taxSet_ERMAESSIGT2);
        receiptRepresentationForSignature.setSumTaxSetNull(el8_taxSet_NULL);
        receiptRepresentationForSignature.setSumTaxSetBesonders(el9_taxSet_BESONDERS);
        receiptRepresentationForSignature.setEncryptedTurnoverValue(el10_encryptedTurnOverValue);
        receiptRepresentationForSignature.setSignatureCertificateSerialNumber(el11_certificateSerialNumberOrCompanyAndKeyID);
        receiptRepresentationForSignature.setSignatureValuePreviousReceipt(el12_chainValue);

        //prepare signature data
        String plainData = receiptRepresentationForSignature.getDataToBeSigned(el1_rkSuite);

        //sign receipt
        String signedJWSCompactRep = signatureDevice.signMachineCodeRepOfReceipt(plainData, cashBoxInstruction.isSignatureDeviceDamaged());

        //store receipt to DEP
        ReceiptPackage receiptPackage = new ReceiptPackage();
        receiptPackage.setCertificateChain(signatureDevice.getSignatureModule().getCertificateChain());
        receiptPackage.setSigningCertificate(signatureDevice.getSignatureModule().getSigningCertificate());
        receiptPackage.setJwsCompactRepresentation(signedJWSCompactRep);
        cashBoxParameters.getDepModul().storeReceipt(receiptPackage);
    }

    /**
     * update turnover counter
     *
     * @param SimplifiedReceipt
     */
    protected void updateTurnOverCounter(SimplifiedReceipt SimplifiedReceipt) {
        //if we have a receipt for training purposes, we don't change the turnover counter
        double sumTaxTypeNormal = Precision.round(SimplifiedReceipt.getTaxSetNormal(), 2);
        double sumTaxTypeErmaessigt1 = Precision.round(SimplifiedReceipt.getTaxSetErmaessigt1(), 2);
        double sumTaxTypeErmaessigt2 = Precision.round(SimplifiedReceipt.getTaxSetErmaessigt2(), 2);
        double sumTaxTypeNull = Precision.round(SimplifiedReceipt.getTaxSetNull(), 2);
        double sumTaxTypeBesonders = Precision.round(SimplifiedReceipt.getTaxSetBesonders(), 2);

        //add all taxset sums
        long tempSum = 0;
        tempSum += Precision.round(sumTaxTypeNormal*100,0);
        tempSum += Precision.round(sumTaxTypeErmaessigt1*100,0);
        tempSum += Precision.round(sumTaxTypeErmaessigt2*100,0);
        tempSum += Precision.round(sumTaxTypeNull*100,0);
        tempSum += Precision.round(sumTaxTypeBesonders*100,0);


        //convert sum to €-cent and add to turnover counter
        turnoverCounter += (tempSum);
    }

    /**
     * encrypt the current turnover counter
     *
     * @param cashBoxIDUTF8String
     * @param receiptIdentifierUTF8String
     * @param rkSuite
     * @return
     */
    protected String encryptTurnOverCounter(String cashBoxIDUTF8String, String receiptIdentifierUTF8String, RKSuite rkSuite,int turnOverCounterLengthInBytes) {
        try {
            //encrypt turnover counter and store the encrypted value in the data-to-be-signed package

            //prepare IV for encryption process, the Initialisation Vector (IV) is calculating by concatenating and then
            //hashing the
            //receipt-identifier (Belegnummer) and
            //the cashbox-ID (Kassen-ID)

            //Get UTF-8 String representation of cashBox-ID (Kassen-ID), STRING in Java are already UTF-8 encoded, thus no
            //encoding transformation is done here
            //IMPORTANT HINT: NEVER EVER use the same "Kassen-ID" and "Belegnummer" for different receipts!!!!
            String IVUTF8StringRepresentation = cashBoxIDUTF8String + receiptIdentifierUTF8String;

            ///hash the String with the hash-algorithm defined in the cashbox-algorithm-suite
            MessageDigest messageDigest = MessageDigest.getInstance(rkSuite.getHashAlgorithmForPreviousSignatureValue());
            byte[] hashValue = messageDigest.digest(IVUTF8StringRepresentation.getBytes());
            byte[] concatenatedHashValue = new byte[16];
            System.arraycopy(hashValue, 0, concatenatedHashValue, 0, 16);

            //encrypt the turnover counter using the AES key
            //Note: 3 AES encryption methods are provided for demonstration purposes,
            //which all use a different mode of operation (CTR, CFB, or ECB).
            //All three methods provided yield the same result. Still, they are provided here to
            //demonstrate the use of different modes of operation for encryption. This can be useful,
            //if AES functionality is re-implemented in another programming language that does
            //support selected AES modes of operation only. Please refer to
            //https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation for more details
            //on different modes of operation for block ciphers
            String base64EncryptedTurnOverValue1 = null;

            base64EncryptedTurnOverValue1 = CryptoUtil.encryptCTR(concatenatedHashValue, turnoverCounter, cashBoxParameters.getTurnOverCounterAESKey(),turnOverCounterLengthInBytes);

            String base64EncryptedTurnOverValue2 = CryptoUtil.encryptCFB(concatenatedHashValue, turnoverCounter, cashBoxParameters.getTurnOverCounterAESKey(),turnOverCounterLengthInBytes);
            String base64EncryptedTurnOverValue3 = CryptoUtil.encryptECB(concatenatedHashValue, turnoverCounter, cashBoxParameters.getTurnOverCounterAESKey(),turnOverCounterLengthInBytes);
            if (!base64EncryptedTurnOverValue1.equals(base64EncryptedTurnOverValue2)) {
                System.out.println("ENCRYPTION ERROR IN METHOD updateTurnOverCounter, MUST NOT HAPPEN");
                System.exit(-1);
            }
            if (!base64EncryptedTurnOverValue1.equals(base64EncryptedTurnOverValue3)) {
                System.out.println("ENCRYPTION ERROR IN METHOD updateTurnOverCounter, MUST NOT HAPPEN");
                System.exit(-1);
            }


            //THE FOLLOWING CODE IS ONLY FOR DEMONSTRATION PURPOSES
            //decryption and reconstruction of the turnover value
            //this is just here for demonstration purposes (so that the whole encryption/decryption process can be found in one place)
            //and not needed for that function
            //IV needs to be setup the same way as above
            //encryptedTurnOverValue needs to be reconstructed as described in the used utility method
            //Note: 3 AES decryption methods are provided for demonstration purposes,
            //which all use a different mode of operation (CTR, CFB, or ECB).
            //All three methods provided yield the same result. Still, they are provided here to
            //demonstrate the use of different modes of operation for decryption. This can be useful, if
            //AES functionality is re-implemented in another programming language that does
            //support selected AES modes of operation only. Please refer to
            //https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation for more details
            //on different modes of operation for block ciphers
            long testPlainOverTurnOverReconstructed1 = CryptoUtil.decryptCTR(concatenatedHashValue, base64EncryptedTurnOverValue1, cashBoxParameters.getTurnOverCounterAESKey());
            long testPlainOverTurnOverReconstructed2 = CryptoUtil.decryptCFB(concatenatedHashValue, base64EncryptedTurnOverValue2, cashBoxParameters.getTurnOverCounterAESKey());
            long testPlainOverTurnOverReconstructed3 = CryptoUtil.decryptECB(concatenatedHashValue, base64EncryptedTurnOverValue3, cashBoxParameters.getTurnOverCounterAESKey());
            if (testPlainOverTurnOverReconstructed1 != testPlainOverTurnOverReconstructed2) {
                System.out.println("DECRYPTION ERROR IN METHOD updateTurnOverCounter, MUST NOT HAPPEN");
                System.exit(-1);
            }

            if (testPlainOverTurnOverReconstructed1 != testPlainOverTurnOverReconstructed3) {
                System.out.println("DECRYPTION ERROR IN METHOD updateTurnOverCounter, MUST NOT HAPPEN");
                System.exit(-1);
            }

            if (turnoverCounter != testPlainOverTurnOverReconstructed1) {
                System.out.println("DECRYPTION ERROR IN METHOD updateTurnOverCounter, MUST NOT HAPPEN");
                System.exit(-1);
            }

            return base64EncryptedTurnOverValue1;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * calculate cryptographic chain value
     *
     * @param previousReceiptJWSRepresentation previous receipt for chain value calculation, if null, the cashboxID is used
     * @param rkSuite                          rksuite that contains information of the to-be-used HASH-algorithm
     * @return
     */
    protected String calculateChainValue(String previousReceiptJWSRepresentation, RKSuite rkSuite) {
        try {
            String inputForChainCalculation;

            //Detailspezifikation Abs 4 "Sig-Voriger-Beleg"
            //if the first receipt is stored, then the cashbox-identifier is hashed and is used as chaining value
            //otherwise the complete last receipt is hased and the result is used as chaining value
            if (previousReceiptJWSRepresentation == null) {
                inputForChainCalculation = cashBoxParameters.getCashBoxId();
            } else {
                inputForChainCalculation = previousReceiptJWSRepresentation;
            }

            //set hash algorithm from RK suite, in this case SHA-256
            MessageDigest md = MessageDigest.getInstance(rkSuite.getHashAlgorithmForPreviousSignatureValue());

            //calculate hash value
            md.update(inputForChainCalculation.getBytes());
            byte[] digest = md.digest();

            //extract number of bytes (N, defined in RKsuite) from hash value
            int bytesToExtract = rkSuite.getNumberOfBytesExtractedFromPrevSigHash();
            byte[] conDigest = new byte[bytesToExtract];
            System.arraycopy(digest, 0, conDigest, 0, bytesToExtract);

            //encode value as BASE64 String ==> chainValue
            return CashBoxUtils.base64Encode(conDigest, false);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
