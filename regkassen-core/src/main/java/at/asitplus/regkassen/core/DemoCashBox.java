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

package at.asitplus.regkassen.core;

import at.asitplus.regkassen.core.base.receiptdata.*;
import at.asitplus.regkassen.core.base.rksuite.RKSuite;
import at.asitplus.regkassen.core.base.util.AESUtil;
import at.asitplus.regkassen.core.base.util.CashBoxUtils;
import at.asitplus.regkassen.core.modules.DEP.DEPExportFormat;
import at.asitplus.regkassen.core.modules.init.CashBoxParameters;
import at.asitplus.regkassen.core.modules.print.ReceiptPrintType;
import at.asitplus.regkassen.core.modules.signature.rawsignatureprovider.DO_NOT_USE_IN_REAL_CASHBOX_DemoSoftwareSignatureModule;
import org.apache.commons.math3.util.Precision;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

/**
 * Simple demonstration CashBox, can be initialized with different modules (signature, DEP, print)
 */
public class DemoCashBox {
    protected CashBoxParameters cashBoxParameters;
    protected long currentReceiptIdentifier;
    protected long turnoverCounter;
    protected int receiptCounter = 0;

    public DemoCashBox(CashBoxParameters cashBoxParameters) {
        this.cashBoxParameters = cashBoxParameters;
        currentReceiptIdentifier = cashBoxParameters.getInitialReceiptIdentifier();
    }

    /**
     * helper method to create, sign and store receipt
     *
     * @param rawReceiptData
     * @param forceSignatureDeviceToWork
     */
    protected void createStoreAndSignReceiptPackage(RawReceiptData rawReceiptData, boolean forceSignatureDeviceToWork, boolean justTraining, boolean isStornoReceipt) {
        //create receiptpackage, in this demo cashbox this is a data structure that contains all receipt relevant data
        //for simplicity this data structure is also stored in the DEP
        ReceiptPackage receiptPackage = new ReceiptPackage();

        //set current suite
        receiptPackage.setRkSuite(cashBoxParameters.getRkSuite());

        //add data to the receiptPackage data structure, the certificates need to be stored in the DEP module, since the
        //DEP export must include the signing certificates and their chain for verification purposes

        //add signing certificate
        receiptPackage.setSigningCertificate(cashBoxParameters.getJwsModule().getSignatureModule().getSigningCertificate());
        //add certificate trust chain
        receiptPackage.setCertificateChain(cashBoxParameters.getJwsModule().getSignatureModule().getCertificateChain());

        //set raw receipt data
        receiptPackage.setRawReceiptData(rawReceiptData);

        //prepare raw receipt data for signing
        //the prepared data is stored in the receiptPackage data structure
        prepareSignatureData(receiptPackage, justTraining, isStornoReceipt);

        //sign the receipt, store the results in the receiptPackage data structure
        String dataToBeSigned = receiptPackage.getReceiptRepresentationForSignature().getDataToBeSigned(cashBoxParameters.getRkSuite());

        //make sure that DEMO damaged mode of signature creation device is only active after the first receipt
        String jwsCompactRepresentation;
        if (forceSignatureDeviceToWork) {
            boolean allowDamage = cashBoxParameters.getJwsModule().isDamagePossible();
            cashBoxParameters.getJwsModule().setDamageIsPossible(false);
            jwsCompactRepresentation = cashBoxParameters.getJwsModule().signMachineCodeRepOfReceipt(dataToBeSigned, cashBoxParameters.getRkSuite());
            cashBoxParameters.getJwsModule().setDamageIsPossible(allowDamage);
        } else {
            jwsCompactRepresentation = cashBoxParameters.getJwsModule().signMachineCodeRepOfReceipt(dataToBeSigned, cashBoxParameters.getRkSuite());
        }

        receiptPackage.setJwsCompactRepresentation(jwsCompactRepresentation);

        //store receipt in the DEP module
        cashBoxParameters.getDepModul().storeReceipt(receiptPackage);

        receiptCounter++;
    }

    /**
     * @param rawReceiptData raw receipt data, that contains all the items
     */
    public void storeReceipt(RawReceiptData rawReceiptData, boolean justTraining, boolean isStornoReceipt) {

        //for demonstration purposes, we change the certificate after a defined number of receipts (specified in the paramters file)
        if (cashBoxParameters.getChangeSignatureCertificateAfterSoManyReceipts() >= 0) {
            if (receiptCounter >= cashBoxParameters.getChangeSignatureCertificateAfterSoManyReceipts()) {
                //only change once
                cashBoxParameters.setChangeSignatureCertificateAfterSoManyReceipts(-1);
                //only works for the DEMO Signature Module that is based on software certificates
                if (cashBoxParameters.getJwsModule().getSignatureModule() instanceof DO_NOT_USE_IN_REAL_CASHBOX_DemoSoftwareSignatureModule) {
                    DO_NOT_USE_IN_REAL_CASHBOX_DemoSoftwareSignatureModule do_not_use_in_real_cashbox_demoSoftwareSignatureModule = (DO_NOT_USE_IN_REAL_CASHBOX_DemoSoftwareSignatureModule) cashBoxParameters.getJwsModule().getSignatureModule();
                    do_not_use_in_real_cashbox_demoSoftwareSignatureModule.intialise();
                }
            }
        }

        //check whether this is the first receipt, if it is we need to make sure the signature creation device works
        //THIS IS JUST RELEVANT FOR THIS DEMO CODE, AS WE HAVE A RANDOM VARIABLE THAT CONTROLS THE STATE OF THE SIG
        //CREATION DEVICE
        boolean forceSignatureDeviceToWork;
        if (retrieveLastStoredReceipt() == null) {
            //make sure that signature creation device works for first receipt
            forceSignatureDeviceToWork = true;
        } else {
            //check whether the signature creation devices was offline for the last receipt
            //if it was offline, we need to inject a receipt that has 0 turnover for all taxsets
            //Verordnung/Â§ 17, Abs 4 (last sentence)
            if (CashBoxUtils.checkReceiptForDamagedSigatureCreationDevice(retrieveLastStoredReceipt().getJwsCompactRepresentation())) {
                //make sure that this signature creation works for the "null" receipt and the subsequent real receipt
                forceSignatureDeviceToWork = true;
                createStoreAndSignReceiptPackage(new RawReceiptData(), true, false, false);
            } else {
                //not the first receipt, and also not in recovery mode from a damaged sig device, random glitches possible for demo mode
                forceSignatureDeviceToWork = false;
            }
        }
        createStoreAndSignReceiptPackage(rawReceiptData, forceSignatureDeviceToWork, justTraining, isStornoReceipt);
    }

    public DEPExportFormat exportDEP() {
        return cashBoxParameters.getDepModul().exportDEP();
    }

    public ReceiptPackage retrieveLastStoredReceipt() {
        return cashBoxParameters.getDepModul().getLastStoredReceipt();
    }

    public List<ReceiptPackage> getStoredReceipts() {
        return cashBoxParameters.getDepModul().getStoredReceipts();
    }

    public byte[] printReceipt(ReceiptPackage receiptPackage, ReceiptPrintType receiptPrintType) {
        return cashBoxParameters.getPrinterModule().printReceipt(receiptPackage, receiptPrintType);
    }

    public List<byte[]> printReceipt(List<ReceiptPackage> receiptPackageList, ReceiptPrintType receiptPrintType) {
        return cashBoxParameters.getPrinterModule().printReceipt(receiptPackageList, receiptPrintType);
    }


    /**
     * Internal cashbox procedure that uses the raw receipt data and prepares the data-to-be-signed
     *
     * @param receiptPackage receipt package that contains all the information of the receipt, and the signed receipt
     */

    protected void prepareSignatureData(ReceiptPackage receiptPackage, boolean justTraining, boolean isStornoReceipt) {
        //preparation of data-to-be-signed according to Detailspezifikation/Abs 4
        ReceiptPackage lastStoredReceipt = cashBoxParameters.getDepModul().getLastStoredReceipt();

        //prepare a data structure that contains the data-to-be-signed. This data structure contains all the
        //fields that need to-be-signed according to Detailspezifikation/Abs 4
        ReceiptRepresentationForSignature receiptRepresentationForSignature = new ReceiptRepresentationForSignature();

        //set data (the order here corresponds to the order of the fields in Detailsspezifikation/Abs 4

        //Kassen-ID (here cashBoxID)
        receiptRepresentationForSignature.setCashBoxID(cashBoxParameters.getCashBoxID());

        //Belegnummer (here receiptIdentifier)
        receiptRepresentationForSignature.setReceiptIdentifier(currentReceiptIdentifier);

        //data or receipt generation (Beleg-Datum-Uhrzeit) (here receiptDateAndTime)
        Date now = new Date();
        receiptRepresentationForSignature.setReceiptDateAndTime(now);

        //prepare sum values for all tax types: get all positions and sum up the values according to their tey type (Normal, Ermaessigt-1, Ermaessigt-2, Null, Besonders)
        //IMPORTANT NOTE that clarifies how rounding is handled
        //for each tax type: the positions (with two decimal places) are summed up without rounding, the sum is then rounded to 0 decimal places and then added
        //to the turnover counter
        List<Item> items = receiptPackage.getRawReceiptData().getItems();
        for (Item item : items) {
            TaxType taxType = item.getTaxType();
            if (taxType == TaxType.SATZ_NORMAL) {
                receiptRepresentationForSignature.setSumTaxSetNormal(receiptRepresentationForSignature.getSumTaxSetNormal() + item.getValue());
            } else if (taxType == TaxType.SATZ_ERMAESSIGT_1) {
                receiptRepresentationForSignature.setSumTaxSetErmaessigt1(receiptRepresentationForSignature.getSumTaxSetErmaessigt1() + item.getValue());
            } else if (taxType == TaxType.SATZ_ERMAESSIGT_2) {
                receiptRepresentationForSignature.setSumTaxSetErmaessigt2(receiptRepresentationForSignature.getSumTaxSetErmaessigt2() + item.getValue());
            } else if (taxType == TaxType.SATZ_NULL) {
                receiptRepresentationForSignature.setSumTaxSetNull(receiptRepresentationForSignature.getSumTaxSetNull() + item.getValue());
            } else if (taxType == TaxType.SATZ_BESONDERS) {
                receiptRepresentationForSignature.setSumTaxSetBesonders(receiptRepresentationForSignature.getSumTaxSetBesonders() + item.getValue());
            }
        }

        //the sums for each tax type are now added to the cashbox turnover value
        //then the turnover value is encrypted and stored in the data-to-be-signed data structure
        //Stand-Umsatz-Zaehler-AES256-ICM (here encryptedTurnoverValue)
        if (!justTraining) {
            updateTurnOverCounterAndAddToDataToBeSigned(receiptRepresentationForSignature);
            if (isStornoReceipt) {
                //mark receipt with "STO" tag
                receiptRepresentationForSignature.setEncryptedTurnoverValue(CashBoxUtils.base64Encode("STO".getBytes(), false));
            }
        } else {
            receiptRepresentationForSignature.setEncryptedTurnoverValue(CashBoxUtils.base64Encode("TRA".getBytes(), false));
        }
        //store UTF-8 String representation of serial number of signing certificate (Zertifikat-Seriennummer) (here signatureCertificateSerialNumber)
        //receiptRepresentationForSignature.setSignatureCertificateSerialNumber(cashBoxParameters.getJwsModule().getSignatureModule().getSigningCertificate().getSerialNumber() + "");
        receiptRepresentationForSignature.setSignatureCertificateSerialNumber(((X509Certificate)(cashBoxParameters.getJwsModule().getSignatureModule().getSigningCertificate())).getSerialNumber() + "");
        
        //create a chain between the last receipt entry and the current receipt
        //Sig-Voriger-Beleg (here signatureValuePreviousReceiptBASE64)
        String signatureValuePreviousReceiptBASE64 = calculateSignatureValuePreviousReceipt(lastStoredReceipt);
        receiptRepresentationForSignature.setSignatureValuePreviousReceipt(signatureValuePreviousReceiptBASE64);

        receiptPackage.setReceiptRepresentationForSignature(receiptRepresentationForSignature);

        //receipt identifier is incremented
        //NOTE in this demonstration a long representation is used for the receipt identifier, however any other representation
        //is possible
        currentReceiptIdentifier++;
    }

    protected String calculateSignatureValuePreviousReceipt(ReceiptPackage receiptPackage) {
        try {
            RKSuite rkSuite = cashBoxParameters.getRkSuite();
            String inputForChainCalculation;

            //Detailspezifikation Abs 4 "Sig-Voriger-Beleg"
            //if the first receipt is stored, then the cashbox-identifier is hashed and is used as chaining value
            //otherwise the complete last receipt is hased and the result is used as chaining value
            if (receiptPackage == null) {
                inputForChainCalculation = cashBoxParameters.getCashBoxID();
            } else {
                inputForChainCalculation = receiptPackage.getJwsCompactRepresentation();
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

    /**
     * encrypt turnovercounter ("Stand-Umsatz-Zaehler-AES256-ICM"), according to Detailspezifikation Abs. 8 and Abs. 9
     *
     * @param receiptRepresentationForSignature receipt data
     */
    protected void updateTurnOverCounterAndAddToDataToBeSigned(ReceiptRepresentationForSignature receiptRepresentationForSignature) {
        try {
            //if we have a receipt for training purposes, we don't change the turnover counter
            double sumTaxTypeNormal = Precision.round(receiptRepresentationForSignature.getSumTaxSetNormal(), 2);
            double sumTaxTypeErmaessigt1 = Precision.round(receiptRepresentationForSignature.getSumTaxSetErmaessigt1(), 2);
            double sumTaxTypeErmaessigt2 = Precision.round(receiptRepresentationForSignature.getSumTaxSetErmaessigt2(), 2);
            double sumTaxTypeBesonders = Precision.round(receiptRepresentationForSignature.getSumTaxSetBesonders(), 2);
            double sumTaxTypeNull = Precision.round(receiptRepresentationForSignature.getSumTaxSetNull(), 2);

            //ATTENTION: changes made to procedure on how to sum up/round values for turnover counter
            //PREV: sum up values, round them, add them to turnover counter
            //NOW: to simplify procedures: turnover counter changed to €-cent. before: 100€ were represented as 100, now
            //they are represented as 10000
            double tempSum = 0.0;
            tempSum += sumTaxTypeNormal;
            tempSum += sumTaxTypeErmaessigt1;
            tempSum += sumTaxTypeErmaessigt2;
            tempSum += sumTaxTypeBesonders;
            tempSum += sumTaxTypeNull;

            //NEW METHOD: convert sum to €-cent and add to turnover counter
            turnoverCounter += (tempSum * 100);

            //OLD METHOD: DO NOT USE
            //turnoverCounter += Math.round(tempSum);

            //encrypt turnover counter and store the encrypted value in the data-to-be-signed package

            //prepare IV for encryption process, the Initialisation Vector (IV) is calculating by concatenating and then
            //hashing the
            //receipt-identifier (Belegnummer) and
            //the cashbox-ID (Kassen-ID)

            //Get UTF-8 String representation of cashBox-ID (Kassen-ID), STRING in Java are already UTF-8 encoded, thus no
            //encoding transformation is done here
            //IMPORTANT HINT: NEVER EVER use the same "Kassen-ID" and "Belegnummer" for different receipts!!!!
            String cashBoxIDUTF8String = receiptRepresentationForSignature.getCashBoxID();
            String receiptIdentifierUTF8String = receiptRepresentationForSignature.getReceiptIdentifier() + ""; //the simple way to convert the long value to an UTF-8 String
            String IVUTF8StringRepresentation = cashBoxIDUTF8String + receiptIdentifierUTF8String;

            ///hash the String with the hash-algorithm defined in the cashbox-algorithm-suite
            MessageDigest messageDigest = MessageDigest.getInstance(cashBoxParameters.getRkSuite().getHashAlgorithmForPreviousSignatureValue());
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
            String base64EncryptedTurnOverValue = AESUtil.encryptCTR(concatenatedHashValue, turnoverCounter, cashBoxParameters.getTurnoverKeyAESkey());
            //String base64EncryptedTurnOverValue = AESUtil.encryptCFB(concatenatedHashValue, turnoverCounter, cashBoxParameters.getTurnoverKeyAESkey());
            //String base64EncryptedTurnOverValue = AESUtil.encryptECB(concatenatedHashValue, turnoverCounter, cashBoxParameters.getTurnoverKeyAESkey());

            
            //set encrypted turnovervalue in data-to-be-signed datastructure
            receiptRepresentationForSignature.setEncryptedTurnoverValue(base64EncryptedTurnOverValue);

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
            long testPlainOverTurnOverReconstructed = AESUtil.decryptCTR(concatenatedHashValue, base64EncryptedTurnOverValue, cashBoxParameters.getTurnoverKeyAESkey());            
            //long testPlainOverTurnOverReconstructed = AESUtil.decryptCFB(concatenatedHashValue, base64EncryptedTurnOverValue, cashBoxParameters.getTurnoverKeyAESkey());            
            //long testPlainOverTurnOverReconstructed = AESUtil.decryptECB(concatenatedHashValue, base64EncryptedTurnOverValue, cashBoxParameters.getTurnoverKeyAESkey());
            
            
            if (turnoverCounter != testPlainOverTurnOverReconstructed) {
                System.out.println("DECRYPTION ERROR IN METHOD updateTurnOverCounterAndAddToDataToBeSigned, MUST NOT HAPPEN");
            }
        } catch (NoSuchProviderException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | BadPaddingException e) {
            e.printStackTrace();
        }
    }
}
