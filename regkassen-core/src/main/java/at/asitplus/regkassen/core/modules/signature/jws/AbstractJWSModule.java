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

package at.asitplus.regkassen.core.modules.signature.jws;

import at.asitplus.regkassen.core.base.rksuite.RKSuite;
import at.asitplus.regkassen.core.modules.signature.rawsignatureprovider.SignatureModule;
import org.jose4j.jws.JsonWebSignature;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJWSModule implements JWSModule {

    protected JsonWebSignature jws;
    protected SignatureModule signatureModule;
    protected boolean damageIsPossible = false;
    protected double probabilityOfDamagedSignatureDevice;

    @Override
    public void setSignatureModule(SignatureModule signatureModul) {
        this.signatureModule = signatureModul;
    }

    @Override
    public SignatureModule getSignatureModule() {
        return signatureModule;
    }

    @Override
    public List<String> signMachineCodeRepOfReceipt(List<String> machineCodeRepOfReceiptList, RKSuite rkSuite) {
        List<String> signedReceipts = new ArrayList<>();
        for (String receiptRepresentationForSignature : machineCodeRepOfReceiptList) {
            signedReceipts.add(signMachineCodeRepOfReceipt(receiptRepresentationForSignature, rkSuite));
        }
        return signedReceipts;
    }

    /**
     * set damageIsPossible flag, only for demonstration purposes
     *
     * @param damageIsPossible set damageIsPossible state of signature module
     */
    @Override
    public void setDamageIsPossible(boolean damageIsPossible) {
        this.damageIsPossible = damageIsPossible;
    }

    @Override
    public boolean isDamagePossible() {
        return damageIsPossible;
    }

    /**
     * set probability of damaged signature device, only for demonstration purposes
     * @param probabilityOfDamagedSignatureDevice
     */
    @Override
    public void setProbabilityOfDamagedSignatureDevice(double probabilityOfDamagedSignatureDevice) {
        this.probabilityOfDamagedSignatureDevice = probabilityOfDamagedSignatureDevice;
    }

    @Override
    public double getProbabilityOfDamagedSignatureDevice() {
        return probabilityOfDamagedSignatureDevice;
    }
}
