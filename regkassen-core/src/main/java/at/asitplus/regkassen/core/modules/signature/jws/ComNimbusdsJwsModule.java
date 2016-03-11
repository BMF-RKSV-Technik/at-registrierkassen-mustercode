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

import at.asitplus.regkassen.core.base.rksuite.RKSuite;
import at.asitplus.regkassen.core.base.util.CashBoxUtils;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;

import java.security.interfaces.ECPrivateKey;

/**
 * JWS Signature module based on JWS library http://connect2id.com/products/nimbus-jose-jwt
 */
public class ComNimbusdsJwsModule extends AbstractJWSModule {

    protected JWSSigner jwsSigner;

    @Override
    public String signMachineCodeRepOfReceipt(String machineCodeRepOfReceipt, boolean signatureDeviceIsDamaged) {

        ECPrivateKey key = (ECPrivateKey) openSystemSignatureModule.getSigningKey();
        try {
            this.jwsSigner = new ECDSASigner(key);
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        //FOR DEMONSTRATION PURPOSES
        //if damage occurs, the signature value is replaced with the term "Sicherheitseinrichtung ausgefallen"
        if (signatureDeviceIsDamaged) {
            String jwsHeader = "eyJhbGciOiJFUzI1NiJ9";  //ES256 Header for JWS
            String jwsPayload = CashBoxUtils.base64Encode(machineCodeRepOfReceipt.getBytes(), true); //get payload
            String jwsSignature = CashBoxUtils.base64Encode("Sicherheitseinrichtung ausgefallen".getBytes(), true);  //create damaged signature part
            String jwsCompactRep = jwsHeader + "." + jwsPayload + "." + jwsSignature;
            return jwsCompactRep;
        }


        try {
            // Creates the JWS object with payload
            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.parse(RKSuite.R1_AT0.getJwsSignatureAlgorithm())), new Payload(machineCodeRepOfReceipt));

            // Compute the EC signature
            jwsObject.sign(jwsSigner);

            // Serialize the JWS to compact form
            return jwsObject.serialize();
        } catch (JOSEException e) {
            e.printStackTrace();
            return null;
        }
    }


}
