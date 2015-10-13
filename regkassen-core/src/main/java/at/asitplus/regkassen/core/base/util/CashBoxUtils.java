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

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
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
    @SuppressWarnings("unused")
    public static byte[] base32Decode(String base32Data) {
        Base32 decoder = new Base32();
        return decoder.decode(base32Data);
    }

    public static String getValueFromMachineCode(String machineCodeRepresentation, MachineCodeValue machineCodeValue) {
        return machineCodeRepresentation.split("_")[machineCodeValue.getIndex()];
    }

    public static String getQRCodeRepresentationFromJWSCompactRepresentation(String jwsCompactRepresentationOfReceipt) {
        //get data
        String jwsPayloadEncoded = jwsCompactRepresentationOfReceipt.split("\\.")[1];
        String jwsSignatureEncoded = jwsCompactRepresentationOfReceipt.split("\\.")[2];

        String payload = new String(CashBoxUtils.base64Decode(jwsPayloadEncoded, true), Charset.forName("UTF-8"));
        String signature = CashBoxUtils.base64Encode(CashBoxUtils.base64Decode(jwsSignatureEncoded, true), false);

        return payload + "_" + signature;
    }

    public static String getPayloadFromQRCodeRepresentation(String qrCodeRepresentation) {
        String[] elements = qrCodeRepresentation.split("_");
        String payload = "";
        for (int i=0;i<12;i++) {
            payload+=elements[i];
            if (i<11) {
                payload+="_";
            }
        }
        return payload;
    }

    public static String getJWSCompactRepresentationFromQRMachineCodeRepresentation(String qrMachineCodeRepresentation) {
        String payload = getPayloadFromQRCodeRepresentation(qrMachineCodeRepresentation);

        //TODO replace with UTF-8 everywhere!!!!!
        String jwsPayload = CashBoxUtils.base64Encode(payload.getBytes(Charset.forName("UTF-8")),true);

        //TODO make that dependent on RK-SUITE
        String jwsHeader = "eyJhbGciOiJFUzI1NiJ9";
        String jwsSignature = CashBoxUtils.base64Encode(CashBoxUtils.base64Decode(CashBoxUtils.getValueFromMachineCode(qrMachineCodeRepresentation,MachineCodeValue.SIGNATURE_VALUE),false),true);

        String jwsCompactRepresentation = jwsHeader+"."+jwsPayload+"."+jwsSignature;
        return jwsCompactRepresentation;
    }

    public static long decryptTurnOverCounter(String encryptedTurnOverCounterBase64,String hashAlgorithm, String cashBoxIDUTF8String, String receiptIdentifierUTF8String,String aesKeyBase64) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] rawAesKey = CashBoxUtils.base64Decode(aesKeyBase64,false);
        SecretKeySpec secretKeySpec = new SecretKeySpec(rawAesKey,"AES");
        SecretKey aesKey = secretKeySpec;
        return decryptTurnOverCounter(encryptedTurnOverCounterBase64,hashAlgorithm,cashBoxIDUTF8String,receiptIdentifierUTF8String,aesKey);
    }


    public static long decryptTurnOverCounter(String encryptedTurnOverCounterBase64,String hashAlgorithm, String cashBoxIDUTF8String, String receiptIdentifierUTF8String,SecretKey aesKey) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
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
        //to storing only 8 bytes of the cipher text (not the full 16 bytes) (or 5 bytes if the mininum turnover length is used)
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        //start decryption process
        ByteBuffer encryptedTurnOverValueComplete = ByteBuffer.allocate(16);

        byte[] encryptedTurnOverValue = CashBoxUtils.base64Decode(encryptedTurnOverCounterBase64, false);
        int lengthOfEncryptedTurnOverValue = encryptedTurnOverValue.length;
        encryptedTurnOverValueComplete.put(encryptedTurnOverValue); //result after decoding the BASE64 value in Beleg

        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
        byte[] testPlainTurnOverValueComplete = cipher.doFinal(encryptedTurnOverValue);

        byte[] testPlainTurnOverValue = new byte[lengthOfEncryptedTurnOverValue];
        System.arraycopy(testPlainTurnOverValueComplete, 0, testPlainTurnOverValue, 0, lengthOfEncryptedTurnOverValue);

        //create java LONG out of ByteArray
        ByteBuffer plainTurnOverValueByteBuffer = ByteBuffer.wrap(testPlainTurnOverValue);
        long plainTurnOverValue = plainTurnOverValueByteBuffer.getLong();

        return plainTurnOverValue;

    }

    public static double getDoubleFromTaxSet(String taxSetValue) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
        DecimalFormat decimalFormat = (DecimalFormat)nf;

        try {
            return decimalFormat.parse(taxSetValue).doubleValue();
        } catch (ParseException e) {
        }
        nf = NumberFormat.getNumberInstance(Locale.US);
        decimalFormat = (DecimalFormat)nf;
        try {
            return decimalFormat.parse(taxSetValue).doubleValue();
        } catch (ParseException e) {
        }
        return -1;


    }

}
