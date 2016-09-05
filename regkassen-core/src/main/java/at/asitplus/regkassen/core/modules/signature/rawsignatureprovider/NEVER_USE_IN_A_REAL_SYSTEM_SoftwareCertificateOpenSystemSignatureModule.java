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

import at.asitplus.regkassen.common.RKSuite;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SIMPLE and NOT REUSABLE demo for a trivial signature module
 * In a real cash box this would be represented by an HSM, a smart card or e.g. a cloud service capable of creating
 * SHA256withECDSA signatures (according to Detailspezifikation ABS 2)
 */
public class NEVER_USE_IN_A_REAL_SYSTEM_SoftwareCertificateOpenSystemSignatureModule implements SignatureModule {

    protected PrivateKey signingKey;
    protected java.security.cert.Certificate signingCertificate;
    protected List<java.security.cert.Certificate> certificateChain;
    protected RKSuite rkSuite;
    protected String serialNumberOrKeyId;
    protected boolean closedSystemSignatureDevice;

    /**
     * this signature device is based on certificates and could therfore be used by open and closed systems
     * for open systems the parameter keyIdForClosedSystem needs to be supplied. open systems do not require this
     * parameter, since the serial number of the certificate is used as identifier
     * @param rkSuite   suite for this signature device
     * @param keyIdForClosedSystem key-id, needs to be supplied if the signature device is used for a closed system
     */
    public NEVER_USE_IN_A_REAL_SYSTEM_SoftwareCertificateOpenSystemSignatureModule(final RKSuite rkSuite, final String keyIdForClosedSystem) {
        this.rkSuite = rkSuite;
        if (rkSuite.getZdaID().startsWith("AT0")) {
            closedSystemSignatureDevice = true;
        } else {
            closedSystemSignatureDevice = false;
        }
        this.serialNumberOrKeyId = keyIdForClosedSystem;
        intialise();
    }

    public void intialise() {
        try {
            //create random demonstration ECC keys
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(256); //256 bit ECDSA key

            //create a key pair for the demo Certificate Authority
            final KeyPair caKeyPair = kpg.generateKeyPair();

            //create a key pair for the signature certificate, which is going to be used to sign the receipts
            final KeyPair signingKeyPair = kpg.generateKeyPair();

            //get references to private keys for the CA and the signing key
            final PrivateKey caKey = caKeyPair.getPrivate();
            signingKey = signingKeyPair.getPrivate();

            //create CA certificate and add it to the certificate chain
            //NOTE: DO NEVER EVER USE IN A REAL CASHBOX, THIS IS JUST FOR DEMONSTRATION PURPOSES
            //NOTE: these certificates have random values, just for the demonstration purposes here
            //However, for testing purposes the most important feature is the EC256 Signing Key, since this is required
            //by the RK Suite
            final X509v3CertificateBuilder caBuilder = new X509v3CertificateBuilder(
                    new X500Name("CN=RegKassa ZDA"),
                    BigInteger.valueOf(new SecureRandom().nextLong()),
                    new Date(System.currentTimeMillis() - 10000),
                    new Date(System.currentTimeMillis() + 24L * 3600 * 1000),
                    new X500Name("CN=RegKassa CA"),
                    SubjectPublicKeyInfo.getInstance(caKeyPair.getPublic().getEncoded()));
            caBuilder.addExtension(X509Extension.basicConstraints, true, new BasicConstraints(false));
            caBuilder.addExtension(X509Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
            final X509CertificateHolder caHolder = caBuilder.build(new JcaContentSignerBuilder("SHA256withECDSA").setProvider("BC").build(caKey));
            final X509Certificate caCertificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(caHolder);
            certificateChain = new ArrayList<java.security.cert.Certificate>();
            certificateChain.add(caCertificate);

            //create signing cert
            final long serialNumberCertificate = new SecureRandom().nextLong();
            if (!closedSystemSignatureDevice) {
                serialNumberOrKeyId = Long.toHexString(serialNumberCertificate);
            }

            final X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                    new X500Name("CN=RegKassa CA"),
                    BigInteger.valueOf(Math.abs(serialNumberCertificate)),
                    new Date(System.currentTimeMillis() - 10000),
                    new Date(System.currentTimeMillis() + 24L * 3600 * 1000),
                    new X500Name("CN=Signing certificate"),
                    SubjectPublicKeyInfo.getInstance(signingKeyPair.getPublic().getEncoded()));
            certBuilder.addExtension(X509Extension.basicConstraints, true, new BasicConstraints(false));
            certBuilder.addExtension(X509Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
            final X509CertificateHolder certHolder = certBuilder.build(new JcaContentSignerBuilder("SHA256withECDSA").setProvider("BC").build(caKey));
            signingCertificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final OperatorCreationException e) {
            e.printStackTrace();
        } catch (final CertIOException e) {
            e.printStackTrace();
        } catch (final CertificateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PrivateKey getSigningKey() {
        return signingKey;
    }

    @Override
    public Certificate getSigningCertificate() {
        return signingCertificate;
    }

    @Override
    public PublicKey getSigningPublicKey() {
        return signingCertificate.getPublicKey();
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
        return closedSystemSignatureDevice;
    }

    @Override
    public List<java.security.cert.Certificate> getCertificateChain() {
        return certificateChain;
    }

    @Override
    public RKSuite getRKSuite() {
        return rkSuite;
    }


}
