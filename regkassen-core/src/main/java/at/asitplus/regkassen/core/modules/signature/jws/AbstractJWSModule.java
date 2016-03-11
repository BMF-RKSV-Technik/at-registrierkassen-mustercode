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
    protected SignatureModule openSystemSignatureModule;

    @Override
    public RKSuite getRKSuite() {
        return getSignatureModule().getRKSuite();
    }

    @Override
    public String getSerialNumberOfKeyID() {
        return openSystemSignatureModule.getSerialNumberOfKeyID();
    }

    public void setOpenSystemSignatureModule(SignatureModule signatureModul) {
        this.openSystemSignatureModule = signatureModul;
    }

    public SignatureModule getSignatureModule() {
        return openSystemSignatureModule;
    }

    @Override
    public List<String> signMachineCodeRepOfReceipt(List<String> machineCodeRepOfReceiptList,boolean signatureDeviceIsDamaged) {
        List<String> signedReceipts = new ArrayList<>();
        for (String receiptRepresentationForSignature : machineCodeRepOfReceiptList) {
            signedReceipts.add(signMachineCodeRepOfReceipt(receiptRepresentationForSignature,signatureDeviceIsDamaged));
        }
        return signedReceipts;
    }

    @Override
    public boolean isClosedSystemSignatureDevice() {
        return openSystemSignatureModule.isClosedSystemSignatureDevice();
    }
}
