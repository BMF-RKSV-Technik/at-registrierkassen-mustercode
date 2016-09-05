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

package at.asitplus.regkassen.core.modules.init;


import at.asitplus.regkassen.core.modules.DEP.DEPModule;
import at.asitplus.regkassen.core.modules.print.PrinterModule;
import at.asitplus.regkassen.core.modules.signature.jws.JWSModule;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;

/**
 * simple init class for a CashBox
 * in this demonstration the AES key is also stored in this class
 * in a real world implementation the AES key would need to be stored in a secure location
 */
public class CashBoxParameters {
    protected String cashBoxId;
    protected SecretKey turnOverCounterAESKey;
    protected String companyID;
    protected int turnOverCounterLengthInBytes = 8;

    //modules
    protected DEPModule depModul;
    protected List<JWSModule> jwsSignatureModules = new ArrayList();
    protected PrinterModule printerModule;

    public String getCashBoxId() {
        return cashBoxId;
    }

    public void setCashBoxId(String cashBoxId) {
        this.cashBoxId = cashBoxId;
    }

    public SecretKey getTurnOverCounterAESKey() {
        return turnOverCounterAESKey;
    }

    public void setTurnOverCounterAESKey(SecretKey turnOverCounterAESKey) {
        this.turnOverCounterAESKey = turnOverCounterAESKey;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public DEPModule getDepModul() {
        return depModul;
    }

    public void setDepModul(DEPModule depModul) {
        this.depModul = depModul;
    }

    public List<JWSModule> getJwsSignatureModules() {
        return jwsSignatureModules;
    }

    public void setJwsSignatureModules(List<JWSModule> jwsSignatureModules) {
        this.jwsSignatureModules = jwsSignatureModules;
    }

    public PrinterModule getPrinterModule() {
        return printerModule;
    }

    public void setPrinterModule(PrinterModule printerModule) {
        this.printerModule = printerModule;
    }

    public int getTurnOverCounterLengthInBytes() {
        return turnOverCounterLengthInBytes;
    }

    public void setTurnOverCounterLengthInBytes(int turnOverCounterLengthInBytes) {
        this.turnOverCounterLengthInBytes = turnOverCounterLengthInBytes;
    }
}
