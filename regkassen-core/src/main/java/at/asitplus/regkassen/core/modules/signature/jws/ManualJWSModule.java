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

package at.asitplus.regkassen.core.modules.signature.jws;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import at.asitplus.regkassen.common.util.CashBoxUtils;
import at.asitplus.regkassen.common.util.CryptoUtil;

/**
 * Manual JWS signature module, that does not require any JWS library
 * For Reg-Kassen Verordnung, we just need one simple method
 * 1. concatenated BASE64-URL encoding of HEADER and PAYLOAD (via ".")
 * 2. This is the data to be signed, which is hashed via SHA-256
 * 3. The hash is then signed via ECDSA
 * 4. Only additional task to do due to JWS spec: encode signature result according to JWS spec (//TODO replace method from JWS lib here with own method)
 */
public class ManualJWSModule extends AbstractJWSModule {

    @Override
    public String signMachineCodeRepOfReceipt(String machineCodeRepOfReceipt, boolean signatureDeviceIsDamaged) {
        try {

            //FOR DEMONSTRATION PURPOSES
            //if damage occurs, the signature value is replaced with the term "Sicherheitseinrichtung ausgefallen"
            if (signatureDeviceIsDamaged) {
                String jwsHeader = "eyJhbGciOiJFUzI1NiJ9";  //ES256 Header for JWS
                String jwsPayload = CashBoxUtils.base64Encode(machineCodeRepOfReceipt.getBytes(), true); //get payload
                String jwsSignature = CashBoxUtils.base64Encode("Sicherheitseinrichtung ausgefallen".getBytes(), true);  //create damaged signature part
                String jwsCompactRep = jwsHeader + "." + jwsPayload + "." + jwsSignature;
                return jwsCompactRep;
            }


            //prepare data to be signed, "ES256 JWS header" fixed (currently the only relevant signature/hash method (RK1)
            String jwsHeaderBase64Url = "eyJhbGciOiJFUzI1NiJ9";
            String jwsPayloadBase64Url = CashBoxUtils.base64Encode(machineCodeRepOfReceipt.getBytes(), true);
            String jwsDataToBeSigned = jwsHeaderBase64Url + "." + jwsPayloadBase64Url;

            //prepare signature according to JAVA JCE/JCA
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(openSystemSignatureModule.getSigningKey());
            signature.update(jwsDataToBeSigned.getBytes());

            //sign data
            byte[] signatureResult = signature.sign();

            //encode according to JWS spec
            //the result of a ECDSA signature consists of two numbers: r and s, typically signature libraries (like in JAVA)
            //encode these two values in the ASN.1 notation
//            ECDSASignature ::= SEQUENCE {
//                r   INTEGER,
//                s   INTEGER
//            }
            //however, the JWS standard just concatenates the byte-array representations of those values (R|S)
            //if the signature - like in this example - is calculated via a crypto library (and not directly with a JWS lib)
            //then in most of the cases a conversion from ASN.1 to the concatenated representation is required. In this demo
            //the transformation is done via the following method.
            byte[] jwsSignature = CryptoUtil.convertDEREncodedSignatureToJWSConcatenated(signatureResult);

            //encode result as BASE64-URL
            String jwsSignatureBase64Url = CashBoxUtils.base64Encode(jwsSignature, true);

            //store as JWS compact representation
            return jwsHeaderBase64Url + "." + jwsPayloadBase64Url + "." + jwsSignatureBase64Url;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
