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

package at.asitplus.regkassen.core.modules.init;


import at.asitplus.regkassen.core.base.rksuite.RKSuite;
import at.asitplus.regkassen.core.modules.print.PrinterModule;
import at.asitplus.regkassen.core.modules.DEP.DEPModule;
import at.asitplus.regkassen.core.modules.signature.jws.JWSModule;

import javax.crypto.SecretKey;

/**
 * simple init class for a CashBox
 * in this demonstration the AES key is also stored in this class
 * in a real world implementation the AES key would need to be stored in a secure place
 */
public class CashBoxParameters {
    protected String cashBoxID;
    protected long initialReceiptIdentifier;
    protected SecretKey turnoverKeyAESkey;
    protected RKSuite rkSuite;
    protected int changeSignatureCertificateAfterSoManyReceipts = -1; //-1 never change sig certificate, this value is used for demonstration purposes

    //modules
    protected DEPModule depModul;
    protected JWSModule jwsModule;
    protected PrinterModule printerModule;

    public int getChangeSignatureCertificateAfterSoManyReceipts() {
        return changeSignatureCertificateAfterSoManyReceipts;
    }

    public void setChangeSignatureCertificateAfterSoManyReceipts(int changeSignatureCertificateAfterSoManyReceipts) {
        this.changeSignatureCertificateAfterSoManyReceipts = changeSignatureCertificateAfterSoManyReceipts;
    }

    public String getCashBoxID() {
        return cashBoxID;
    }

    public void setCashBoxID(String cashBoxID) {
        this.cashBoxID = cashBoxID;
    }

    public long getInitialReceiptIdentifier() {
        return initialReceiptIdentifier;
    }

    public void setInitialReceiptIdentifier(long initialReceiptIdentifier) {
        this.initialReceiptIdentifier = initialReceiptIdentifier;
    }

    public SecretKey getTurnoverKeyAESkey() {
        return turnoverKeyAESkey;
    }

    public void setTurnoverKeyAESkey(SecretKey turnoverKeyAESkey) {
        this.turnoverKeyAESkey = turnoverKeyAESkey;
    }

    public RKSuite getRkSuite() {
        return rkSuite;
    }

    public void setRkSuite(RKSuite rkSuite) {
        this.rkSuite = rkSuite;
    }

    public DEPModule getDepModul() {
        return depModul;
    }

    public void setDepModul(DEPModule depModul) {
        this.depModul = depModul;
    }

    public JWSModule getJwsModule() {
        return jwsModule;
    }

    public void setJwsModule(JWSModule jwsModule) {
        this.jwsModule = jwsModule;
    }

    public PrinterModule getPrinterModule() {
        return printerModule;
    }

    public void setPrinterModule(PrinterModule printerModule) {
        this.printerModule = printerModule;
    }
}
