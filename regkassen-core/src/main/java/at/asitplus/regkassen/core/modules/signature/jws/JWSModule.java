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

import java.util.List;

/**
 * Simple interface for highlevel signature module capable of creating a JSON Web Signature
 */
public interface JWSModule {
    /**
     * @param signatureModul Underlying signature module (e.g. card, HSM etc.) that is used to create the JWS signature
     */
    void setOpenSystemSignatureModule(SignatureModule signatureModul);

    /**
     *
     * @return underlying signature module
     */
    SignatureModule getSignatureModule();

    /**
     * sign machine readable code
     * @param machineCodeRepOfReceipt machine readable code
     * @param signatureDeviceIsDamaged flag to indicate a damaged signature device, this is ONLY required for the demo code
     * @return
     */
    String signMachineCodeRepOfReceipt(String machineCodeRepOfReceipt, boolean signatureDeviceIsDamaged);

    List<String> signMachineCodeRepOfReceipt(List<String> machineCodeRepOfReceiptList, boolean signatureDeviceIsDamaged);

    /**
     * @return RKSuite of Signature module (mainly required for ZDA-ID)
     */
    RKSuite getRKSuite();

    /**
     *
     * @return in case of an open-system: serial number of certificate.
     * in case of a closed system: company ID (UID, Steuernummer or GLN) plus KEY-ID (e.g. UID:123456789-KE1)
     * in case of an open system: serial number of the used certificate
     */
    String getSerialNumberOfKeyID();

    /**
     * @return signature module for a closed or an open system?
     */
    boolean isClosedSystemSignatureDevice();
}
