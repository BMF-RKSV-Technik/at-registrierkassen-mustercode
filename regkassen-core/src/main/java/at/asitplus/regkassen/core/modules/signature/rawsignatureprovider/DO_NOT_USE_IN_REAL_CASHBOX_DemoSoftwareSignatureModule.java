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

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SIMPLE and NOT REUSABLE demo for a trivial signature module
 * In a real cash box this would be represented by an HSM, a smart card or e.g. a cloud service capable of creating
 * SHA256withECDSA signatures (according to Detailspezifikation ABS 2
 */
public class DO_NOT_USE_IN_REAL_CASHBOX_DemoSoftwareSignatureModule implements SignatureModule {

    protected PrivateKey signingKey;
    protected X509Certificate signingCertificate;
    protected List<X509Certificate> certificateChain;

    //NOTE: NEVER EVER USE IN A REAL CASHBOX, THIS IS JUST FOR DEMONSTRATION PURPOSES
    //In a real cashbox, this would use a smart card, an HSM or a cloud service
    public DO_NOT_USE_IN_REAL_CASHBOX_DemoSoftwareSignatureModule() {
        try {
            //create random demonstration ECC keys
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(256); //256 bit ECDSA key

            //create a key pair for the demo Certificate Authority
            KeyPair caKeyPair = kpg.generateKeyPair();

            //create a key pair for the signature certificate, which is going to be used to sign the receipts
            KeyPair signingKeyPair = kpg.generateKeyPair();

            //get references to private keys for the CA and the signing key
            PrivateKey caKey = (ECPrivateKey) caKeyPair.getPrivate();
            signingKey = (ECPrivateKey) signingKeyPair.getPrivate();

            //create CA certificate and add it to the certificate chain
            //NOTE: DO NEVER EVER USE IN A REAL CASHBOX, THIS IS JUST FOR DEMONSTRATION PURPOSES
            //NOTE: these certificates have random values, just for the demonstration purposes here
            //However, for testing purposes the most important feature is the EC256 Signing Key, since this is required
            //by the RK Suite
            X509v3CertificateBuilder caBuilder = new X509v3CertificateBuilder(
                    new X500Name("CN=RegKassa ZDA"),
                    BigInteger.valueOf(new SecureRandom().nextLong()),
                    new Date(System.currentTimeMillis() - 10000),
                    new Date(System.currentTimeMillis() + 24L * 3600 * 1000),
                    new X500Name("CN=RegKassa CA"),
                    SubjectPublicKeyInfo.getInstance(caKeyPair.getPublic().getEncoded()));
            caBuilder.addExtension(X509Extension.basicConstraints, true, new BasicConstraints(false));
            caBuilder.addExtension(X509Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
            X509CertificateHolder caHolder = caBuilder.build(new JcaContentSignerBuilder("SHA256withECDSA").setProvider("BC").build(caKey));
            X509Certificate caCertificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(caHolder);
            certificateChain = new ArrayList<>();
            certificateChain.add(caCertificate);

            //create signing cert
            X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                    new X500Name("CN=RegKassa CA"),
                    BigInteger.valueOf(new SecureRandom().nextLong()),
                    new Date(System.currentTimeMillis() - 10000),
                    new Date(System.currentTimeMillis() + 24L * 3600 * 1000),
                    new X500Name("CN=Signing certificate"),
                    SubjectPublicKeyInfo.getInstance(signingKeyPair.getPublic().getEncoded()));
            certBuilder.addExtension(X509Extension.basicConstraints, true, new BasicConstraints(false));
            certBuilder.addExtension(X509Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
            X509CertificateHolder certHolder = certBuilder.build(new JcaContentSignerBuilder("SHA256withECDSA").setProvider("BC").build(caKey));
            signingCertificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertIOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    public PrivateKey getSigningKey() {
        return signingKey;
    }

    public X509Certificate getSigningCertificate() {
        return signingCertificate;
    }

    public List<X509Certificate> getCertificateChain() {
        return certificateChain;
    }
}
