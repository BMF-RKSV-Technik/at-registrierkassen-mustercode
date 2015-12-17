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

package at.asitplus.regkassen.demo.snippets;

import at.asitplus.regkassen.core.base.util.CashBoxUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashDemo {
    public static void main(String[] args) {

        //Code snippet that shows how to generate the Link for the QR-Code/OCR-string
        //example receipt encoded as machine readable code in QR-Code representation
        String code = "_R1-AT0_DEMO-CASH-BOX703_496410_2015-12-17T11:22:26_61,33_15,42_38,26_0,00_42,81_udSL0zTzFaA=_b869153bc32a1c9_TZgOKfzheUs=_d7VoZ/nyr/8Mt2NVBZ0L4ivQwdlE3CmCFmz10bA5NXhGQ1QkunsDTqKvOSy//3nO8WT0+ypQqrDXMvyOGOXl9w==";

        //calculate hash according to specification
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hashValue32Bytes = md.digest(code.getBytes());

        //take 8 bytes of hash value
        byte[] hashValue8Bytes = new byte[8];
        System.arraycopy(hashValue32Bytes, 0, hashValue8Bytes, 0, 8);

        //BASE64-URL encode the 8-byte hash-value
        String base64UrlHashCodeRep = CashBoxUtils.base64Encode(hashValue8Bytes,true);

        //example link
        String link = "http://cashbox.example.at/" + base64UrlHashCodeRep;
        System.out.println(link);
    }
}
