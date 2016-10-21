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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * DEP Export format according to Detailspezifikation Abs 3
 */
public class DEPBelegDump {


  protected String   signatureCertificate;

  @JsonProperty("Zertifizierungsstellen")
  protected String[] certificateChain;

  @JsonProperty("Belege-kompakt")
  protected String[] belegeDaten;

  @JsonGetter("Signaturzertifikat")
  public String getSignatureCertificate() {
    return signatureCertificate;
  }

  @JsonSetter("Signaturzertifikat")
  public void setSignatureCertificate(final String signatureCertificate) {
    this.signatureCertificate = signatureCertificate;
  }

  @JsonSetter("Signatur- bzw. Siegelzertifikat")
  public void setSigOrSealCertificate(final String signatureCertificate) {
    this.signatureCertificate = signatureCertificate;
  }

  public String[] getBelegeDaten() {
    return belegeDaten;
  }

  public void setBelegeDaten(final String[] belegeDaten) {
    this.belegeDaten = belegeDaten;
  }

  public String[] getCertificateChain() {
    return certificateChain;
  }

  public void setCertificateChain(final String[] certificateChain) {
    this.certificateChain = certificateChain;
  }
}
