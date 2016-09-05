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

package at.asitplus.regkassen.core.modules.DEP;

import at.asitplus.regkassen.core.base.receiptdata.ReceiptPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * simple memory based implementation of the DEP module
 * receipts are stored in memory
 */
public class SimpleMemoryDEPModule implements DEPModule {

    protected List<ReceiptPackage> receiptPackages = new ArrayList<ReceiptPackage>();

    @Override
    public void storeReceipt(final ReceiptPackage receiptPackage) {
        receiptPackages.add(receiptPackage);
    }

    @Override
    public List<ReceiptPackage> getStoredReceipts() {
        return receiptPackages;
    }


    @Override
    public DEPExportFormat exportDEP() {

        //prepare data structures for DEP export

        //create data structure for export format
        final DEPExportFormat depExportFormat = new DEPExportFormat();
        final DEPBelegDump[] belegDump = new DEPBelegDump[1];

        //store receipts in export format
        belegDump[0] = new DEPBelegDump();
        depExportFormat.setBelegPackage(belegDump);

        final List<String> receiptsInJWSCompactRepresentation = new ArrayList<String>();
        for (final ReceiptPackage receiptPackage : receiptPackages) {
            receiptsInJWSCompactRepresentation.add(receiptPackage.getJwsCompactRepresentation());
        }
        belegDump[0].setBelegeDaten(receiptsInJWSCompactRepresentation.toArray(new String[receiptsInJWSCompactRepresentation.size()]));

        //certificates can be optionally included in the DEP export format
        //since this is not mandatory, the demo code implements the simpler variant
        belegDump[0].setSignatureCertificate("");
        belegDump[0].setCertificateChain(new String[0]);

        return depExportFormat;

    }

    @Override
    public ReceiptPackage getLastStoredReceipt() {
        if (receiptPackages.size() > 0) {
            return receiptPackages.get(receiptPackages.size() - 1);
        } else {
            return null;
        }
    }
}
