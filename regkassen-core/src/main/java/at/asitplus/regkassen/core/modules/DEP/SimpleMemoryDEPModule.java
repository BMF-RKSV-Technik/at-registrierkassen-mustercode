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

package at.asitplus.regkassen.core.modules.DEP;

import at.asitplus.regkassen.core.base.receiptdata.ReceiptPackage;
import at.asitplus.regkassen.core.base.util.CashBoxUtils;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * simple memory based implementation of the DEP module
 * receipts are stored in memory
 */
public class SimpleMemoryDEPModule implements DEPModule {

    protected List<ReceiptPackage> receiptPackages = new ArrayList<>();

    public void storeReceipt(ReceiptPackage receiptPackage) {
        receiptPackages.add(receiptPackage);
    }

    public List<ReceiptPackage> getStoredReceipts() {
        return receiptPackages;
    }

    public DEPExportFormat exportDEP() {
        try {
            //prepare data structures for DEP export
            //DEP exports are grouped according to the used signature certificate (CURRENTLY NOT USED IN THE DEMO)
            HashMap<String, List<ReceiptPackage>> certificateToReceiptMap = new HashMap<>();

            for (ReceiptPackage receiptPackage : receiptPackages) {
                X509Certificate signingCertificate = receiptPackage.getSigningCertificate();
                List<ReceiptPackage> receiptPackagesForSignatureCertificate = certificateToReceiptMap.get(signingCertificate.getSerialNumber() + "");
                if (receiptPackagesForSignatureCertificate == null) {
                    receiptPackagesForSignatureCertificate = new ArrayList<>();
                    certificateToReceiptMap.put(signingCertificate.getSerialNumber() + "", receiptPackagesForSignatureCertificate);
                }
                receiptPackagesForSignatureCertificate.add(receiptPackage);
            }

            //create data structure for export format
            DEPExportFormat depExportFormat = new DEPExportFormat();
            DEPBelegDump[] belegDump = new DEPBelegDump[certificateToReceiptMap.keySet().size()];

            //store receipts in export format
            int dumpIndex = 0;
            for (List<ReceiptPackage> signedReceipts : certificateToReceiptMap.values()) {
                belegDump[dumpIndex] = new DEPBelegDump();
                depExportFormat.setBelegPackage(belegDump);

                List<String> receiptsInJWSCompactRepresentation = new ArrayList<>();
                for (ReceiptPackage receiptPackage : signedReceipts) {
                    receiptsInJWSCompactRepresentation.add(receiptPackage.getJwsCompactRepresentation());
                }
                belegDump[dumpIndex].setBelegeDaten(receiptsInJWSCompactRepresentation.toArray(new String[receiptsInJWSCompactRepresentation.size()]));

                String base64EncodedSignatureCertificate = CashBoxUtils.base64Encode(signedReceipts.get(0).getSigningCertificate().getEncoded(), false);
                belegDump[dumpIndex].setSignatureCertificate(base64EncodedSignatureCertificate);

                List<X509Certificate> base64EncodedCertificateChain = signedReceipts.get(0).getCertificateChain();
                String[] certificateChain = new String[base64EncodedCertificateChain.size()];
                for (int i = 0; i < base64EncodedCertificateChain.size(); i++) {
                    X509Certificate base64EncodedChainCertificate = base64EncodedCertificateChain.get(i);
                    certificateChain[i] = CashBoxUtils.base64Encode(base64EncodedChainCertificate.getEncoded(), false);
                }
                belegDump[dumpIndex].setCertificateChain(certificateChain);

                dumpIndex++;

            }
            return depExportFormat;
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ReceiptPackage getLastStoredReceipt() {
        if (receiptPackages.size() > 0) {
            return receiptPackages.get(receiptPackages.size() - 1);
        } else {
            return null;
        }
    }
}
