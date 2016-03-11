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

package at.asitplus.regkassen.core.base.receiptdata;

import java.security.cert.Certificate;
import java.util.List;

/*
    This class represents the data structure of a processed receipt that is handed over to the DEP
    Please keep in mind, that this is a simple model that contains just enough information to fulfill the requirements
    of the RKSV. For real applications, more data would be needed that are mandated by other regulations.
 */
public class ReceiptPackage {
    protected String jwsCompactRepresentation;
    protected Certificate signingCertificate;
    protected List<Certificate> certificateChain;

    public String getJwsCompactRepresentation() {
        return jwsCompactRepresentation;
    }

    public void setJwsCompactRepresentation(String jwsCompactRepresentation) {
        this.jwsCompactRepresentation = jwsCompactRepresentation;
    }

    public Certificate getSigningCertificate() {
        return signingCertificate;
    }

    public void setSigningCertificate(Certificate signingCertificate) {
        this.signingCertificate = signingCertificate;
    }

    public List<Certificate> getCertificateChain() {
        return certificateChain;
    }

    public void setCertificateChain(List<Certificate> certificateChain) {
        this.certificateChain = certificateChain;
    }

}
