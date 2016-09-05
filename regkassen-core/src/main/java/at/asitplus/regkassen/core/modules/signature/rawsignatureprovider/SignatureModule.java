/*
 * Copyright (C) 2015, 2016
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

import at.asitplus.regkassen.common.RKSuite;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.List;

/**
 * interface for a basic raw signature module (e.g. a smartcard, HSM, or even an online module
 */
public interface SignatureModule {
    /**
     *
     * @return
     */
    RKSuite getRKSuite();

    /**
     * Reference to the private signing key. In case of a smartcard or an HSM, this would only be the reference, in case
     * of the software based demonstration modules the encoded key is returned
     * @return
     */
    PrivateKey getSigningKey();

    /**
     * get signing certificate (only for certificate based signature devices, which are required for open system)
     * @return
     */
    Certificate getSigningCertificate();
    List<Certificate> getCertificateChain();

    /**
     * get public key (e.g. a closed system that does not employ certificates)
     * @return
     */
    PublicKey getSigningPublicKey();

    /**
     * hash and sign data...
     * @param dataToBeSigned data to be signed
     * @return
     */
    byte[] signData(byte[] dataToBeSigned);

    /**
     * @return in case of an open-system: serial number of certificate.
     * in case of a closed system: company ID (UID, Steuernummer or GLN) plus KEY-ID (e.g. UID:123456789-KE1)
     */
    String getSerialNumberOfKeyID();

    /**
     * @return signature module for a closed or an open system?
     */
    boolean isClosedSystemSignatureDevice();

}
