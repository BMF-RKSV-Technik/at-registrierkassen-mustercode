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

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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


}
