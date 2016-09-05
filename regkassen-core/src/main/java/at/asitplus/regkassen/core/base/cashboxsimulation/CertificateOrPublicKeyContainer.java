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

package at.asitplus.regkassen.core.base.cashboxsimulation;

import at.asitplus.regkassen.common.SignatureDeviceType;
import at.asitplus.regkassen.common.util.CashBoxUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Data structure that is used to export cryptographic material required by the casbox, the export allows the
 * verification tool to verify the signature/encrypted turnover counter of the generated receipts
 */
public class CertificateOrPublicKeyContainer {
    //key/certificate identifier, for open systems: serial number of certificate, for closed systems: company-ID plus KeyID
    protected String id;
    //type (certificate (open or closed systems), or public-key-only (closed systems)
    protected SignatureDeviceType signatureDeviceType;
    //encoded certificate or public key
    //certificate: BASE64 encoded DER encoded certificate
    //public key (ECDSA key): BASE64 encoded public key, public key is encoded according to
    //https://tools.ietf.org/html/rfc3279 and
    //https://tools.ietf.org/html/rfc5480
    //open ssl command to parse key: openssl ec -in key.file -pubin -inform DER -text
    protected String signatureCertificateOrPublicKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SignatureDeviceType getSignatureDeviceType() {
        return signatureDeviceType;
    }

    public void setSignatureDeviceType(SignatureDeviceType signatureDeviceType) {
        this.signatureDeviceType = signatureDeviceType;
    }

    public String getSignatureCertificateOrPublicKey() {
        return signatureCertificateOrPublicKey;
    }

    public void setSignatureCertificateOrPublicKey(String signatureCertificateOrPublicKey) {
        this.signatureCertificateOrPublicKey = signatureCertificateOrPublicKey;
    }

    @JsonIgnore
    public PublicKey getPublicKey() {
        PublicKey publicKey = null;
        try {
            if (signatureDeviceType == SignatureDeviceType.CERTIFICATE) {
                X509Certificate cert = getCertificate();
                publicKey = cert.getPublicKey();
            } else {
                KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
                publicKey = fact.generatePublic(new X509EncodedKeySpec(CashBoxUtils.base64Decode(signatureCertificateOrPublicKey, false)));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    @JsonIgnore
    public X509Certificate getCertificate() {
        try {
            if (signatureDeviceType == SignatureDeviceType.PUBLIC_KEY) {
                return null;
            } else {
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                InputStream in = new ByteArrayInputStream(CashBoxUtils.base64Decode(signatureCertificateOrPublicKey, false));
                X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
                return cert;
            }
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }
}
