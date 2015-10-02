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

package at.asitplus.regkassen.verification;

import at.asitplus.regkassen.core.base.rksuite.RKSuite;
import at.asitplus.regkassen.core.base.util.CashBoxUtils;
import at.asitplus.regkassen.core.modules.DEP.DEPExportFormat;
import com.google.gson.Gson;
import org.apache.commons.cli.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.jws.EcdsaUsingShaAlgorithm;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Initial basic DEP export format verification tool.
 * Currently, this is a simple implementation that can only handle standard DEP export files
 * (without damaged signature modules)
 * current checks: cryptographic validity of signature, chaining, certificates ARE NOT checked
 */
public class VerifyDEPFormat {
    public static void main(String[] args) {
        try {
            // create CMD line option object
            Options options = new Options();

            // add CMD line options
            options.addOption("i", "dep-export-file-to-validate", true, "specifies a DEP export file and verifies stored receipts (signature, chaining)");

            ///parse CMD line options
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            String inputFileString = cmd.getOptionValue("i");
            if (inputFileString == null) {
                System.out.println("need a DEP export file as input... specify file with -i");
                System.exit(0);
            }

            //initialise cryptographic providers
            Security.addProvider(new BouncyCastleProvider());
            BufferedReader reader = new BufferedReader(new FileReader(new File(inputFileString)));

            Gson gson = new Gson();
            DEPExportFormat depExportFormat = gson.fromJson(reader, DEPExportFormat.class);
            X509Certificate signatureCertificate = parseCertificate(depExportFormat.getBelegPackage()[0].getSignatureCertificate());
            //List<X509Certificate> certificateChain = parseCertificates(depExportFormat.getBelegPackage()[0].getCertificateChain());

            String[] jwsRepresentationsOfReceipts = depExportFormat.getBelegPackage()[0].getBelegeDaten();

            String lastReceiptAsQRCodeRepresentation = null;

            int index = 1;
            for (String jwsCompactRepresentationOfReceipt : jwsRepresentationsOfReceipts) {
                System.out.println("Verification of receipt " + index);
                //get data
                String jwsHeaderEncoded = jwsCompactRepresentationOfReceipt.split("\\.")[0];
                String jwsPayloadEncoded = jwsCompactRepresentationOfReceipt.split("\\.")[1];
                String jwsSignatureEncoded = jwsCompactRepresentationOfReceipt.split("\\.")[2];
                byte[] jwsSignature = EcdsaUsingShaAlgorithm.convertConcatenatedToDer(CashBoxUtils.base64Decode(jwsSignatureEncoded, true));

                String jwsPayloadPlain = new String(CashBoxUtils.base64Decode(jwsPayloadEncoded, true));
                String cashBoxID = jwsPayloadPlain.split("_")[1];
                String previousSignatureValueBase64 = jwsPayloadPlain.split("_")[11];


                //verify cryptographically
                String signingInput = jwsHeaderEncoded + "." + jwsPayloadEncoded;
                byte[] signingInputAsASCII = signingInput.getBytes("US-ASCII");

                Signature ecdsaSignature = Signature.getInstance("SHA256withECDSA");
                assert signatureCertificate != null;
                ecdsaSignature.initVerify(signatureCertificate.getPublicKey());
                ecdsaSignature.update(signingInputAsASCII);

                System.out.println("Signature verification (only cryptographically): " + ecdsaSignature.verify(jwsSignature));



                //TODO implement
                //verify validity of certificates: revocation checking, trust etc.
                System.out.println("Certificate/chain validation: NOT IMPLEMENTED YET");
                System.out.println("Machine code elements - format verification: NOT IMPLEMENTED YET");


                //verify chaining
                MessageDigest messageDigest = MessageDigest.getInstance(RKSuite.R1_AT0.getHashAlgorithmForPreviousSignatureValue());
                if (lastReceiptAsQRCodeRepresentation == null) {
                    messageDigest.update(cashBoxID.getBytes());

                } else {
                    messageDigest.update(lastReceiptAsQRCodeRepresentation.getBytes());
                }
                byte[] hashValue = messageDigest.digest();
                byte[] chainValue = new byte[RKSuite.R1_AT0.getNumberOfBytesExtractedFromPrevSigHash()];
                System.arraycopy(hashValue, 0, chainValue, 0, RKSuite.R1_AT0.getNumberOfBytesExtractedFromPrevSigHash());

                String chainValueBase64 = CashBoxUtils.base64Encode(chainValue, false);
                if (chainValueBase64.equals(previousSignatureValueBase64)) {
                    System.out.println("Cryptographic chaining to previous receipt - verification: " + true);
                } else {
                    System.out.println("Cryptographic chaining to previous receipt - verification: " + false);
                }
                System.out.println("");

                lastReceiptAsQRCodeRepresentation = jwsPayloadPlain + "_" + CashBoxUtils.base64Encode(CashBoxUtils.base64Decode(jwsSignatureEncoded, true), false);
                index++;
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | IOException | SignatureException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static X509Certificate parseCertificate(String base64EncodedCertificate) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream bIn = new ByteArrayInputStream(CashBoxUtils.base64Decode(base64EncodedCertificate, false));
            return (X509Certificate) certificateFactory.generateCertificate(bIn);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<X509Certificate> parseCertificates(String[] base64EncodedCertificates) {
        List<X509Certificate> certificates = new ArrayList<>();
        for (String base64EncodedCertificate : base64EncodedCertificates) {
            certificates.add(parseCertificate(base64EncodedCertificate));
        }
        return certificates;
    }


}
