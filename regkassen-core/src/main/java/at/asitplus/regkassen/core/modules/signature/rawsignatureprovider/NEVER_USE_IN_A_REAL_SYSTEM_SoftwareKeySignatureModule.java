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

package at.asitplus.regkassen.core.modules.signature.rawsignatureprovider;

import java.security.*;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

import at.asitplus.regkassen.common.RKSuite;

/**
 * SIMPLE and NOT REUSABLE demo for a trivial signature module of a closed system (which does not employ certificates)
 * In a real cash box this would be represented by an HSM, a smart card or e.g. a cloud service capable of creating
 * SHA256withECDSA signatures (according to Detailspezifikation ABS 2
 */
public class NEVER_USE_IN_A_REAL_SYSTEM_SoftwareKeySignatureModule implements SignatureModule {

    protected PrivateKey signingKey;
    protected PublicKey publicKey;
    protected String serialNumberOrKeyId;

    /**
     * this signature device can only be used for a closed system (there are no certificates, which would be required by
     * an open system), thus the key-ID needs to be provided.
     *
     * @param keyIDForClosedSystem
     */
    public NEVER_USE_IN_A_REAL_SYSTEM_SoftwareKeySignatureModule(final String keyIDForClosedSystem) {
        this.serialNumberOrKeyId = keyIDForClosedSystem;
        intialise();
    }

    public void intialise() {
        try {
            //create random demonstration ECC keys
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(256); //256 bit ECDSA key

            //create a key pair for the signature certificate, which is going to be used to sign the receipts
            final KeyPair signingKeyPair = kpg.generateKeyPair();

            //get references to private/public signing key
            signingKey = signingKeyPair.getPrivate();
            publicKey = signingKeyPair.getPublic();
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //NOTE: NEVER EVER USE IN A REAL CASHBOX, THIS IS JUST FOR DEMONSTRATION PURPOSES
    //In a real cashbox, this would use a smart card, an HSM or a cloud service
    public NEVER_USE_IN_A_REAL_SYSTEM_SoftwareKeySignatureModule() {
        intialise();
    }

    @Override
    public PrivateKey getSigningKey() {
        return signingKey;
    }

    @Override
    public Certificate getSigningCertificate() {
        return null;
    }

    @Override
    public PublicKey getSigningPublicKey() {
        return publicKey;
    }

    @Override
    public byte[] signData(final byte[] dataToBeSigned) {

        try {
            final Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(getSigningKey());
            signature.update(dataToBeSigned);
            return signature.sign();
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final SignatureException e) {
            e.printStackTrace();
        } catch (final InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getSerialNumberOfKeyID() {
        return serialNumberOrKeyId;
    }

    @Override
    public boolean isClosedSystemSignatureDevice() {
        return true;
    }

    @Override
    public List<Certificate> getCertificateChain() {
        return new ArrayList<Certificate>();
    }

    @Override
    public RKSuite getRKSuite() {
        return RKSuite.R1_AT0;
    }

}
