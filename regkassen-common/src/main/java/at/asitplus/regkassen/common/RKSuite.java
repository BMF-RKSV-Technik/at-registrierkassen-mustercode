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

package at.asitplus.regkassen.common;

public enum RKSuite {
    // RK Suite defined in Detailspezifikation/ABS 2
    // suite for a closed system (closed systems are identified by the ZDA-ID
    // AT0)
    R1_AT0("1", "AT0", "ES256", "SHA-256", 8),
    R1_AT1("1", "AT1", "ES256", "SHA-256", 8),
    R1_AT2("1", "AT2", "ES256", "SHA-256", 8),
    R1_AT3("1", "AT3", "ES256", "SHA-256", 8),
    R1_AT4("1", "AT4", "ES256", "SHA-256", 8),
    R1_AT5("1", "AT5", "ES256", "SHA-256", 8),
    R1_AT6("1", "AT6", "ES256", "SHA-256", 8),
    R1_AT7("1", "AT7", "ES256", "SHA-256", 8),
    R1_AT8("1", "AT8", "ES256", "SHA-256", 8),
    R1_AT9("1", "AT9", "ES256", "SHA-256", 8),
    R1_AT10("1", "AT10", "ES256", "SHA-256", 8),

  // suite for an open system (in this case with the virtual ZDA identified by
    // AT100)
    R1_AT100("1", "AT100", "ES256", "SHA-256", 8);

  protected String suiteID;
  protected String zdaID;
  protected String jwsSignatureAlgorithm;
  protected String hashAlgorithmForPreviousSignatureValue;
  protected int    numberOfBytesExtractedFromPrevSigHash;

  RKSuite(String suiteID, String zdaID, String jwsSignatureAlgorithm,
      String hashAlgorithmForPreviousSignatureValue, int numberOfBytesExtractedFromPrevSigHash) {
    this.suiteID = suiteID;
    this.zdaID = zdaID;
    this.jwsSignatureAlgorithm = jwsSignatureAlgorithm;
    this.hashAlgorithmForPreviousSignatureValue = hashAlgorithmForPreviousSignatureValue;
    this.numberOfBytesExtractedFromPrevSigHash = numberOfBytesExtractedFromPrevSigHash;
  }

  public String getSuiteID() {
    return "R" + suiteID + "-" + zdaID;
  }

  public String getZdaID() {
    return zdaID;
  }

  public String getJwsSignatureAlgorithm() {
    return jwsSignatureAlgorithm;
  }

  public String getHashAlgorithmForPreviousSignatureValue() {
    return hashAlgorithmForPreviousSignatureValue;
  }

  public int getNumberOfBytesExtractedFromPrevSigHash() {
    return numberOfBytesExtractedFromPrevSigHash;
  }
}
