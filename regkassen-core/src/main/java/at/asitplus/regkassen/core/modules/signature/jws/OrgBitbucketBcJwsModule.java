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
import at.asitplus.regkassen.core.modules.signature.rawsignatureprovider.SignatureModule;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;

import java.util.ArrayList;
import java.util.List;

/**
 * JWS Signature module based on JWS library https://bitbucket.org/b_c/jose4j/wiki/Home
 */
public class OrgBitbucketBcJwsModule implements JWSModule {
    protected JsonWebSignature jws;
    protected SignatureModule signatureModule;
    protected boolean damaged = false;

    public void setSignatureModule(SignatureModule signatureModule) {
        //init signature module
        this.signatureModule = signatureModule;
        jws = new JsonWebSignature();
        jws.setKey(signatureModule.getSigningKey());
    }

    /**
     * Sign machine code representation of receipt according to Detailspezifikation Abs 5
     *
     * @param machineCodeRepOfReceipt machine code representation as String according to Detailspezifikation Abs 5
     * @param rkSuite                 RK Suite according to Detailspezifikation Abs 2
     * @return JWS compact representation of signature, according to Detailspezifikation Abs 6
     */
    public String signMachineCodeRepOfReceipt(String machineCodeRepOfReceipt, RKSuite rkSuite) {
        //TODO handle damaged flag!
        //will be added to later versions of the demo code, to demonstrate how to handle a damaged/non reachable/offline
        //signature module

        try {
            //set data-to-be-signed (the same as the QR-code-representation)

            //e.g. as follows (line breaks for visualization purposes
            //R1-AT0_
            //DEMO-CASH-BOX929_
            //529540_
            //2015-10-01T15:22:34_
            //0.00_
            //0.00_
            //0.00_
            //35.76_
            //0.00_
            //vzDNcYfyQ3E=_
            //5430635153978658386_
            ///LPObTum+ck=_
            //MtK8aPNw7QSOqntvvJTd8+oTpaGxIyKaK6Y2kn76Lo7C0SwX2yB7FYZD5H0V70vlSTxEG+Vy1KBN+0IMB+RzQw==

            //REF TO SPECIFICATION: Detailspezifikation/Abs 5
            jws.setPayload(machineCodeRepOfReceipt);

            //set hashing/signature algorithm according to cashbox-suite
            //REF TO SPECIFICATION: Detailspezifikation/Abs 2
            jws.setAlgorithmHeaderValue(rkSuite.getJwsSignatureAlgorithm());

            //sign the payload
            //REF TO SPECIFICATION: Detailspezifikation Abs 5/Abs 6
            jws.sign();

            //get JWS compact representation
            //REF TO SPECIFICATION: Detailspezifikation Abs 6
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> signMachineCodeRepOfReceipt(List<String> machineCodeRepOfReceiptList, RKSuite rkSuite) {
        List<String> signedReceipts = new ArrayList<>();
        for (String receiptRepresentationForSignature : machineCodeRepOfReceiptList) {
            signedReceipts.add(signMachineCodeRepOfReceipt(receiptRepresentationForSignature, rkSuite));
        }
        return signedReceipts;
    }

    /**
     * set damaged flag, only for demonstration purposes
     *
     * @param damaged set damaged state of signature module
     */
    public void setDamaged(boolean damaged) {
        this.damaged = damaged;
    }

    public SignatureModule getSignatureModule() {
        return signatureModule;
    }
}
