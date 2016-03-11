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

import java.util.HashMap;

public class CryptographicMaterialContainer {
    protected String base64AESKey;
    protected HashMap<String,CertificateOrPublicKeyContainer> certificateOrPublicKeyMap;

    public String getBase64AESKey() {
        return base64AESKey;
    }

    public void setBase64AESKey(String base64AESKey) {
        this.base64AESKey = base64AESKey;
    }

    public HashMap<String, CertificateOrPublicKeyContainer> getCertificateOrPublicKeyMap() {
        return certificateOrPublicKeyMap;
    }

    public void setCertificateOrPublicKeyMap(HashMap<String, CertificateOrPublicKeyContainer> certificateOrPublicKeyMap) {
        this.certificateOrPublicKeyMap = certificateOrPublicKeyMap;
    }

}
