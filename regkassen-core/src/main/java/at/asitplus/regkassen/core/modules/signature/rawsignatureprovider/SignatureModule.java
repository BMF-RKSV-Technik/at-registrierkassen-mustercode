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

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

/**
 * Simple interface for a raw signature module capable of carrying out a SHA256withECDSA signature (SHA-256 hash, ECDSA
 * signature.)
 * //REF TO SPECIFICATION: Detailspezifikation/Abs 2
 */
public interface SignatureModule {
    PrivateKey getSigningKey();

    Certificate getSigningCertificate();

    List<Certificate> getCertificateChain();
}
