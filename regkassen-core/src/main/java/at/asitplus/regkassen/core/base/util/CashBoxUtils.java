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

package at.asitplus.regkassen.core.base.util;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.io.IOUtils;
import org.jose4j.base64url.internal.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CashBoxUtils {

    /**
     * Generates a random AES key for encrypting/decrypting the turnover value
     * ATTENTION: In a real cash box this key would be generated during the init process and stored in a secure area
     *
     * @return generated AES key
     */
    public static SecretKey createAESKey() {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            int keySize = 256;
            kgen.init(keySize);
            return kgen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper method for writing printed receipts to files
     *
     * @param printedReceipts binary representation of receipts to be stored
     * @param prefix          prefix for file names
     * @param baseDir         base directory, where files should be written
     */
    public static void writeReceiptsToFiles(List<byte[]> printedReceipts, String prefix, File baseDir) {
        try {
            int index = 1;
            for (byte[] printedReceipt : printedReceipts) {
                ByteArrayInputStream bIn = new ByteArrayInputStream(printedReceipt);
                File receiptFile = new File(baseDir, prefix + "Receipt " + index + ".pdf");
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(receiptFile));
                IOUtils.copy(bIn, bufferedOutputStream);
                bufferedOutputStream.close();
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * BASE64 encoding helper function
     *
     * @param data      binary representation of data to be encoded
     * @param isUrlSafe indicates whether BASE64 URL-safe encoding should be used (required for JWS)
     * @return BASE64 encoded representation of input data
     */
    public static String base64Encode(byte[] data, boolean isUrlSafe) {
        Base64 encoder = new Base64(isUrlSafe);
        return new String(encoder.encode(data)).replace("\r\n", "");
    }

    /**
     * BASE64 decoder helper function
     *
     * @param base64Data BASE64 encoded data
     * @param isUrlSafe  indicates whether BASE64 URL-safe encoding was used (required for JWS)
     * @return binary representation of decoded data
     */
    public static byte[] base64Decode(String base64Data, boolean isUrlSafe) {
        Base64 decoder = new Base64(isUrlSafe);
        return decoder.decode(base64Data);
    }

    /**
     * BASE32 encoding helper (required for OCR representation)
     *
     * @param data binary representation of data to be encoded
     * @return BASE32 encoded representation of input data
     */
    public static String base32Encode(byte[] data) {
        Base32 encoder = new Base32();
        return new String(encoder.encode(data)).replace("\r\n", "");
    }

    /**
     * BASE32 decoding helper (required for OCR representation)
     *
     * @param base32Data BASE32 encoded data
     * @return binary representation of decoded data
     */
    public static byte[] base32Decode(String base32Data) {
        Base32 decoder = new Base32();
        return decoder.decode(base32Data);
    }

    /**
     * get a value from the machine code representation
     *
     * @param machineCodeRepresentation machinecode representation (QR or OCR code)
     * @param machineCodeValue          which value? e.g. signature value, rk-suite etc.
     * @return the extracted value as String
     */
    public static String getValueFromMachineCode(String machineCodeRepresentation, MachineCodeValue machineCodeValue) {
        //plus 1 due to leading "_"
        return machineCodeRepresentation.split("_")[machineCodeValue.getIndex() + 1];
    }

    /**
     * convert JWS compact representation to QR-machine-code representation of signed receipt
     *
     * @param jwsCompactRepresentationOfReceipt JWS compact representation of signed receipt
     * @return the QR-machine-code-representation of signed receipt
     */
    public static String getQRCodeRepresentationFromJWSCompactRepresentation(String jwsCompactRepresentationOfReceipt) {
        //get data
        String jwsPayloadEncoded = jwsCompactRepresentationOfReceipt.split("\\.")[1];
        String jwsSignatureEncoded = jwsCompactRepresentationOfReceipt.split("\\.")[2];

        String payload = new String(CashBoxUtils.base64Decode(jwsPayloadEncoded, true), Charset.forName("UTF-8"));
        String signature = CashBoxUtils.base64Encode(CashBoxUtils.base64Decode(jwsSignatureEncoded, true), false);

        return payload + "_" + signature;
    }

    /**
     * extract the payload of the QR-Code (remove signature value)
     *
     * @param qrCodeRepresentation the QR-machine-code-representation of signed receipt
     * @return extracted payload of QR-machine-code-representation of signed receipt
     */
    public static String getPayloadFromQRCodeRepresentation(String qrCodeRepresentation) {
        String[] elements = qrCodeRepresentation.split("_");
        String payload = "";
        for (int i = 0; i < 13; i++) {
            payload += elements[i];
            if (i < 12) {
                payload += "_";
            }
        }
        return payload;
    }

    /**
     * convert QR-machine-code representation of signed receipt to JWS compact representation
     *
     * @param qrMachineCodeRepresentation the QR-machine-code-representation of signed receipt
     * @return JWS compact representation of signed receipt
     */
    public static String getJWSCompactRepresentationFromQRMachineCodeRepresentation(String qrMachineCodeRepresentation) {
        String payload = getPayloadFromQRCodeRepresentation(qrMachineCodeRepresentation);

        //TODO replace with UTF-8 everywhere!!!!!
        String jwsPayload = CashBoxUtils.base64Encode(payload.getBytes(Charset.forName("UTF-8")), true);

        //TODO make that dependent on RK-SUITE
        String jwsHeader = "eyJhbGciOiJFUzI1NiJ9";
        String jwsSignature = CashBoxUtils.base64Encode(CashBoxUtils.base64Decode(CashBoxUtils.getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SIGNATURE_VALUE), false), true);

        return jwsHeader + "." + jwsPayload + "." + jwsSignature;
    }

    //see next method
    public static long decryptTurnOverCounter(String encryptedTurnOverCounterBase64, String hashAlgorithm, String cashBoxIDUTF8String, String receiptIdentifierUTF8String, String aesKeyBase64) throws Exception {
        byte[] rawAesKey = CashBoxUtils.base64Decode(aesKeyBase64, false);
        SecretKey aesKey = new SecretKeySpec(rawAesKey, "AES");
        return decryptTurnOverCounter(encryptedTurnOverCounterBase64, hashAlgorithm, cashBoxIDUTF8String, receiptIdentifierUTF8String, aesKey);
    }

    /**
     * decrypt the turnover counter with the given AES key, and parameters for IV creation
     * Ref: Detailspezifikation Abs 8/Abs 9/Abs 10
     *
     * @param encryptedTurnOverCounterBase64 encrypted turnover counter
     * @param hashAlgorithm                  hash-algorithm used to generate IV
     * @param cashBoxIDUTF8String            cashbox-id, required for IV creation
     * @param receiptIdentifierUTF8String    receiptidentifier, required for IV creation
     * @param aesKey                         aes key
     * @return derypted turnover value as long
     * @throws Exception
     */
    public static long decryptTurnOverCounter(String encryptedTurnOverCounterBase64, String hashAlgorithm, String cashBoxIDUTF8String, String receiptIdentifierUTF8String, SecretKey aesKey) throws Exception {
        //calc IV value (cashbox if + receipt identifer, both as UTF-8 Strings)
        String IVUTF8StringRepresentation = cashBoxIDUTF8String + receiptIdentifierUTF8String;

        //calc hash
        MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
        byte[] hashValue = messageDigest.digest(IVUTF8StringRepresentation.getBytes());
        byte[] concatenatedHashValue = new byte[16];
        System.arraycopy(hashValue, 0, concatenatedHashValue, 0, 16);

        //extract bytes 0-15 from hash value
        ByteBuffer byteBufferIV = ByteBuffer.allocate(16);
        byteBufferIV.put(concatenatedHashValue);

        //IV for AES algorithm
        byte[] IV = byteBufferIV.array();

        //prepare AES cipher with CTR/ICM mode, NoPadding is essential for the decryption process. Padding could not be reconstructed due
        //to storing only 8 bytes of the cipher text (not the full 16 bytes) (or 5 bytes if the minimum turnover length is used)
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        //start decryption process
        ByteBuffer encryptedTurnOverValueComplete = ByteBuffer.allocate(16);

        //decode turnover base64 value
        byte[] encryptedTurnOverValue = CashBoxUtils.base64Decode(encryptedTurnOverCounterBase64, false);

        //extract length (required to extract the correct number of bytes from decrypted value
        int lengthOfEncryptedTurnOverValue = encryptedTurnOverValue.length;

        //prepare for decryption (require 128 bit blocks...)
        encryptedTurnOverValueComplete.put(encryptedTurnOverValue);

        //decryption setup, AES ciper in CTR mode, NO PADDING!)
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);

        //decrypt value, now we have a 128 bit block, with trailing junk bytes
        byte[] testPlainTurnOverValueComplete = cipher.doFinal(encryptedTurnOverValue);

        //remove junk bytes by extracting known length of plain text
        byte[] testPlainTurnOverValue = new byte[lengthOfEncryptedTurnOverValue];
        System.arraycopy(testPlainTurnOverValueComplete, 0, testPlainTurnOverValue, 0, lengthOfEncryptedTurnOverValue);

        //create java LONG out of ByteArray
        ByteBuffer plainTurnOverValueByteBuffer = ByteBuffer.wrap(testPlainTurnOverValue);

        return plainTurnOverValueByteBuffer.getLong();
    }

    /**
     * get double value from arbitrary String represent a double (0,00) or (0.00)
     *
     * @param taxSetValue double value as String
     * @return double value
     * @throws Exception
     */
    public static double getDoubleFromTaxSet(String taxSetValue) throws Exception {
        //try format ("0,00")
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
        DecimalFormat decimalFormat = (DecimalFormat) nf;
        Exception parseException;
        try {
            return decimalFormat.parse(taxSetValue).doubleValue();
        } catch (ParseException ignored) {
        }
        //if Austrian/German format fail, try US format (0.00)
        nf = NumberFormat.getNumberInstance(Locale.US);
        decimalFormat = (DecimalFormat) nf;
        try {
            return decimalFormat.parse(taxSetValue).doubleValue();
        } catch (ParseException e) {
            parseException = e;
        }
        throw parseException;
    }

    /**
     * check whether the JWS compact representation of signed receipt contains indicator for damaged signature creation device
     *
     * @param jwsCompactRepresentation
     * @return
     */
    public static boolean checkLastReceiptForDamagedSigatureCreationDevice(String jwsCompactRepresentation) {
        String encodedSignatureValueBase64 = jwsCompactRepresentation.split("\\.")[2];
        String decodedSignatureValue = new String(CashBoxUtils.base64Decode(encodedSignatureValueBase64, true));
        return "Sicherheitseinrichtung ausgefallen".equals(decodedSignatureValue);
    }

    /**
     * get sum of all tax-set turnover values from QR-machine-code-representation of signed receipt
     *
     * @param qrMachineCodeRepresentation QR-machine-code-representation of signed receipt
     * @param calcAbsValue                flag which indicates whether abs(value) should be used, if set, this can be used to check whether
     *                                    the sum is zero. this is needed for checking the first receipt of the DEP or the first receipt after
     *                                    recovering from a failed signature creation device.
     * @return
     * @throws Exception
     */
    public static double getTaxSetTurnOverSumFromQRMachineCodeRepresentation(String qrMachineCodeRepresentation, boolean calcAbsValue) throws Exception {
        double currentTaxSetNormal = CashBoxUtils.getDoubleFromTaxSet(CashBoxUtils.getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SUM_TAX_SET_NORMAL));
        double currentTaxSetErmaessigt1 = CashBoxUtils.getDoubleFromTaxSet(CashBoxUtils.getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SUM_TAX_SET_ERMAESSIGT1));
        double currentTaxSetErmaessigt2 = CashBoxUtils.getDoubleFromTaxSet(CashBoxUtils.getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SUM_TAX_SET_ERMAESSIGT2));
        double currentTaxSetBesonders = CashBoxUtils.getDoubleFromTaxSet(CashBoxUtils.getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SUM_TAX_SET_BESONDERS));
        double currentTaxSetNull = CashBoxUtils.getDoubleFromTaxSet(CashBoxUtils.getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SUM_TAX_SET_NULL));

        if (calcAbsValue) {
            return Math.abs(currentTaxSetNormal) + Math.abs(currentTaxSetErmaessigt1) + Math.abs(currentTaxSetErmaessigt2) + Math.abs(currentTaxSetBesonders) + Math.abs(currentTaxSetNull);
        } else {
            return currentTaxSetNormal + currentTaxSetErmaessigt1 + currentTaxSetErmaessigt2 + currentTaxSetBesonders + currentTaxSetNull;
        }
    }

    /**
     * extract certificates from DEP Export Format String representation
     *
     * @param base64EncodedCertificate BASE64 encoded DER-encoded-certificate
     * @return java object for X509Certificate
     * @throws CertificateException
     */
    public static X509Certificate parseCertificate(String base64EncodedCertificate) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream bIn = new ByteArrayInputStream(CashBoxUtils.base64Decode(base64EncodedCertificate, false));
        return (X509Certificate) certificateFactory.generateCertificate(bIn);
    }

    /**
     * extract certificates from DEP Export Format String representation
     *
     * @param base64EncodedCertificates BASE64 encoded DER-encoded-certificates
     * @return java objects for X509Certificate
     * @throws CertificateException
     */
    public static List<X509Certificate> parseCertificates(String[] base64EncodedCertificates) throws CertificateException {
        List<X509Certificate> certificates = new ArrayList<>();
        for (String base64EncodedCertificate : base64EncodedCertificates) {
            certificates.add(parseCertificate(base64EncodedCertificate));
        }
        return certificates;
    }
}
