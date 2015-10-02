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

import java.util.List;

/**
 * Simple interface for highlevel signature module capable of creating a JSON Web Signature
 */
public interface JWSModule {
    void setSignatureModule(SignatureModule signatureModul);

    SignatureModule getSignatureModule();

    String signMachineCodeRepOfReceipt(String machineCodeRepOfReceipt, RKSuite rkSuite);

    List<String> signMachineCodeRepOfReceipt(List<String> machineCodeRepOfReceiptList, RKSuite rkSuite);

    //JUST FOR DEMONSTRATION PURPOSES
    void setDamaged(boolean damaged);

}
