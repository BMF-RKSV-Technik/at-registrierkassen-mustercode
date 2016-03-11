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

import at.asitplus.regkassen.core.base.receiptdata.SimplifiedReceipt;

/**
 * This class represents an instruction that is executed for simulation purposes by a cashbox
 * This code is not relevant for the productive use. It is used for simulation/testing purposes
 */
public class CashBoxInstruction {
    //indicates whether the signature device should be working/damaged while generating the receipt
    protected Boolean signatureDeviceDamaged;
    //given receipt identifier
    protected String receiptIdentifier;
    //date to use for receipt generation
    protected String dateToUse;
    //used signature device, this is used to simulate a pool of signature creation devices
    protected Integer usedSignatureDevice;
    //simple receipt, that contains the tax-set-sums
    protected SimplifiedReceipt simplifiedReceipt;
    //type of receipt that should be generated
    protected TypeOfReceipt typeOfReceipt;

    public Boolean isSignatureDeviceDamaged() {
        return signatureDeviceDamaged;
    }

    public void setSignatureDeviceDamaged(Boolean signatureDeviceDamaged) {
        this.signatureDeviceDamaged = signatureDeviceDamaged;
    }

    public String getReceiptIdentifier() {
        return receiptIdentifier;
    }

    public void setReceiptIdentifier(String receiptIdentifier) {
        this.receiptIdentifier = receiptIdentifier;
    }

    public String getDateToUse() {
        return dateToUse;
    }

    public void setDateToUse(String dateToUse) {
        this.dateToUse = dateToUse;
    }

    public SimplifiedReceipt getSimplifiedReceipt() {
        return simplifiedReceipt;
    }

    public void setSimplifiedReceipt(SimplifiedReceipt simplifiedReceipt) {
        this.simplifiedReceipt = simplifiedReceipt;
    }

    public TypeOfReceipt getTypeOfReceipt() {
        return typeOfReceipt;
    }

    public void setTypeOfReceipt(TypeOfReceipt typeOfReceipt) {
        this.typeOfReceipt = typeOfReceipt;
    }

    public Integer getUsedSignatureDevice() {
        return usedSignatureDevice;
    }

    public void setUsedSignatureDevice(Integer usedSignatureDevice) {
        this.usedSignatureDevice = usedSignatureDevice;
    }
}
