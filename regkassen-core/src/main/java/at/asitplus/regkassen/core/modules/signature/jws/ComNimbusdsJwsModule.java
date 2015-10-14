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
import at.asitplus.regkassen.core.modules.signature.rawsignatureprovider.SignatureModule;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;

import java.security.interfaces.ECPrivateKey;
import java.util.ArrayList;
import java.util.List;

/**
 * JWS Signature module based on JWS library http://connect2id.com/products/nimbus-jose-jwt
 */
public class ComNimbusdsJwsModule implements JWSModule {

    protected SignatureModule signatureModule;
    protected boolean damageIsPossible = false;
    protected JWSSigner jwsSigner;

    @Override
    public void setSignatureModule(SignatureModule signatureModule) {
        this.signatureModule = signatureModule;
    }

    @Override
    public SignatureModule getSignatureModule() {
        return signatureModule;
    }

    @Override
    public String signMachineCodeRepOfReceipt(String machineCodeRepOfReceipt,
                                              RKSuite rkSuite) {

        ECPrivateKey key = (ECPrivateKey) signatureModule.getSigningKey();
        try {
            this.jwsSigner = new ECDSASigner(key);
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        //TODO explain
        if (damageIsPossible) {
            double randValue = Math.random();
            if (randValue>=0.5) {
                String jwsHeader = "eyJhbGciOiJFUzI1NiJ9";
                String jwsPayload = CashBoxUtils.base64Encode(machineCodeRepOfReceipt.getBytes(),true);
                String jwsSignature = CashBoxUtils.base64Encode("Sicherheitseinrichtung ausgefallen".getBytes(),true);
                String jwsCompactRep = jwsHeader+"."+jwsPayload+"."+jwsSignature;
                return jwsCompactRep;
            }
        }

        try {
            // Creates the JWS object with payload
            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.parse(rkSuite.getJwsSignatureAlgorithm())), new Payload(machineCodeRepOfReceipt));

            // Compute the EC signature
            jwsObject.sign(jwsSigner);

            // Serialize the JWS to compact form
            return jwsObject.serialize();
        } catch (JOSEException e) {
            e.printStackTrace();
            return null;
        }


    }

    public List<String> signMachineCodeRepOfReceipt(List<String> machineCodeRepOfReceiptList, RKSuite rkSuite) {
        List<String> signedReceipts = new ArrayList<>();
        for (String receiptRepresentationForSignature : machineCodeRepOfReceiptList) {
            signedReceipts.add(signMachineCodeRepOfReceipt(receiptRepresentationForSignature, rkSuite));
        }
        return signedReceipts;
    }

    /**
     * set damageIsPossible flag, only for demonstration purposes
     *
     * @param damageIsPossible set damageIsPossible state of signature module
     */
    public void setDamageIsPossible(boolean damageIsPossible) {
        this.damageIsPossible = damageIsPossible;
    }

    @Override
    public boolean isDamagePossible() {
        return damageIsPossible;
    }

}
