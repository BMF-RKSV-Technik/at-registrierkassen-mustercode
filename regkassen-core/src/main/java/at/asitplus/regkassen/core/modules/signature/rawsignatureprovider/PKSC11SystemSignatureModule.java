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

import at.asitplus.regkassen.core.base.rksuite.RKSuite;
import sun.security.pkcs11.SunPKCS11;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PKCS11 signature module. the PKCS11 standard is a widely adopted standard for using cryptograpic functions provided by
 * cryptographic tokens (smartcards) and HSMs
 */
public class PKSC11SystemSignatureModule implements SignatureModule {

    /* Define absolute path to PKCS#11 library (x64) */
    public static final String DLL_64 = "ENTER_PATH_TO_P11_LIB_64_HERE";

    /* Define absolute path to PKCS#11 library (x86) */
    public static final String DLL = "ENTER_PATH_TO_P11_LIB_HERE";

    /* Define alias of key to be used */
    public static final String KEY_ALIAS = "ENTER_KEY_ALIAS_HERE";

    private KeyStore ks;

    protected RKSuite rkSuite;

    protected boolean closedSystemSignatureDevice;
    protected String serialNumberOrKeyId;

    public PKSC11SystemSignatureModule(RKSuite rkSuite, String keyIdForClosedSystem) {
        if (rkSuite.getSuiteID().startsWith("AT0")) {
            closedSystemSignatureDevice = true;
        } else {
            closedSystemSignatureDevice = false;
        }
        this.serialNumberOrKeyId = keyIdForClosedSystem;

        initialize();
    }

    private void initialize() {

        String arch = System.getProperty("sun.arch.data.model");

        String pkcs11ConfigSettings = null;
        if (arch.equalsIgnoreCase("64")) {
            pkcs11ConfigSettings = "name=pkcs11\n" + "library=" + DLL_64;
        } else if (arch.equalsIgnoreCase("86")) {
            pkcs11ConfigSettings = "name=pkcs11\n" + "library=" + DLL;
        } else {
            System.err.println("Error: unknown architecture: " + arch);
            return;
        }

        byte[] pkcs11ConfigBytes = pkcs11ConfigSettings.getBytes();
        ByteArrayInputStream confStream = new ByteArrayInputStream(pkcs11ConfigBytes);

        SunPKCS11 pkcs11 = new SunPKCS11(confStream);
        Security.addProvider(pkcs11);

        try {
            ks = KeyStore.getInstance("PKCS11");

            ks.load(null, null);

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public PrivateKey getSigningKey() {

        try {

            return (PrivateKey) ks.getKey(KEY_ALIAS, null);

        } catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Certificate getSigningCertificate() {

        try {

            Certificate c = ks.getCertificate(KEY_ALIAS);
            return c;

        } catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<Certificate> getCertificateChain() {

        try {

            Certificate[] chain = ks.getCertificateChain(KEY_ALIAS);
            return new ArrayList<>(Arrays.asList(chain));

        } catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public PublicKey getSigningPublicKey() {
        X509Certificate c;
        try {
            c = (X509Certificate) ks.getCertificate(KEY_ALIAS);
            return c.getPublicKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] signData(byte[] dataToBeSigned) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(getSigningKey());
            signature.update(dataToBeSigned);
            return signature.sign();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getSerialNumberOfKeyID() {
        if (closedSystemSignatureDevice) {
            return serialNumberOrKeyId;
        } else {
            X509Certificate c;
            try {
                c = (X509Certificate) ks.getCertificate(KEY_ALIAS);
                return Long.toHexString(c.getSerialNumber().longValue());
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean isClosedSystemSignatureDevice() {
        return closedSystemSignatureDevice;
    }

    @Override
    public RKSuite getRKSuite() {
        return rkSuite;
    }

}
