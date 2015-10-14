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

import at.asitplus.regkassen.core.base.rksuite.RKSuite;
import at.asitplus.regkassen.core.base.util.CashBoxUtils;

import java.security.cert.X509Certificate;
import java.util.List;

public class ReceiptPackage {
    protected RKSuite rkSuite;
    protected RawReceiptData rawReceiptData;
    protected ReceiptRepresentationForSignature receiptRepresentationForSignature;
    protected String jwsCompactRepresentation;
    protected X509Certificate signingCertificate;
    protected List<X509Certificate> certificateChain;

    protected String getDataToBeSigned() {
        return receiptRepresentationForSignature.getDataToBeSigned(rkSuite);
    }

    public String getQRCodeRepresentation() {
        String qrCodeRepresentationWithoutSignature = receiptRepresentationForSignature.getDataToBeSigned(rkSuite);

        //get signature value from JWS compact representation
        String signatureValueBASE64URL = jwsCompactRepresentation.split("\\.")[2];

        //re-encode from BASE64-URL to BASE64
        String signatureValueBASE64 = CashBoxUtils.base64Encode(CashBoxUtils.base64Decode(signatureValueBASE64URL,true),false);

        //get QR code rep
        String qrCodeRepresentationWithSignature = qrCodeRepresentationWithoutSignature+"_"+signatureValueBASE64;

        return qrCodeRepresentationWithSignature;
    }

    public String getOcrCodeRepresentation() {
        String orcCodeRepresentationWithoutSignature = receiptRepresentationForSignature.getOCRCodeRepresentationWithoutSignature(rkSuite);

        //get signature value from JWS compact representation
        String signatureValueBASE64URL = jwsCompactRepresentation.split("\\.")[2];

        //re-encode from BASE64-URL to BASE64
        String signatureValueBASE64 = CashBoxUtils.base32Encode(CashBoxUtils.base64Decode(signatureValueBASE64URL, true));

        //get OCR code rep
        String ocrCodeRepresentationWithSignature = orcCodeRepresentationWithoutSignature+"_"+signatureValueBASE64;

        return ocrCodeRepresentationWithSignature;
    }

    public RKSuite getRkSuite() {
        return rkSuite;
    }

    public void setRkSuite(RKSuite rkSuite) {
        this.rkSuite = rkSuite;
    }

    public RawReceiptData getRawReceiptData() {
        return rawReceiptData;
    }

    public void setRawReceiptData(RawReceiptData rawReceiptData) {
        this.rawReceiptData = rawReceiptData;
    }

    public String getJwsCompactRepresentation() {
        return jwsCompactRepresentation;
    }

    public void setJwsCompactRepresentation(String jwsCompactRepresentation) {
        this.jwsCompactRepresentation = jwsCompactRepresentation;
    }

    public ReceiptRepresentationForSignature getReceiptRepresentationForSignature() {
        return receiptRepresentationForSignature;
    }

    public void setReceiptRepresentationForSignature(ReceiptRepresentationForSignature receiptRepresentationForSignature) {
        this.receiptRepresentationForSignature = receiptRepresentationForSignature;
    }

    public X509Certificate getSigningCertificate() {
        return signingCertificate;
    }

    public void setSigningCertificate(X509Certificate signingCertificate) {
        this.signingCertificate = signingCertificate;
    }

    public List<X509Certificate> getCertificateChain() {
        return certificateChain;
    }

    public void setCertificateChain(List<X509Certificate> certificateChain) {
        this.certificateChain = certificateChain;
    }

}
