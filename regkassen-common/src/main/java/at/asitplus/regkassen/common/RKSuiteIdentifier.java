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

public enum RKSuiteIdentifier {
    // RK Suite defined in Detailspezifikation/ABS 2
    R1("1", "ES256", "SHA-256", "SHA256withECDSA", "ECDSA", 8);

  public static final String[] SUPPORTED_PREFIXES = {
      "R1-AT" };

  protected final String       suiteID;
  protected final String       jwsSignatureAlgorithm;
  protected final String       javaSignatureAlgorithm;
  protected final String       javaPublicKeySpec;

  protected final String       hashAlgorithmForPreviousSignatureValue;
  protected final int          numberOfBytesExtractedFromPrevSigHash;

  RKSuiteIdentifier(String suiteID, String jwsSignatureAlgorithm,
      String hashAlgorithmForPreviousSignatureValue, String javaSignatureAlgorithm, String javaPublicKeySpec,
      int numberOfBytesExtractedFromPrevSigHash) {
    this.suiteID = suiteID;
    this.jwsSignatureAlgorithm = jwsSignatureAlgorithm;
    this.hashAlgorithmForPreviousSignatureValue = hashAlgorithmForPreviousSignatureValue;
    this.numberOfBytesExtractedFromPrevSigHash = numberOfBytesExtractedFromPrevSigHash;
    this.javaSignatureAlgorithm = javaSignatureAlgorithm;
    this.javaPublicKeySpec = javaPublicKeySpec;
  }

  public String getJavaSignatureAlgorithm() {
    return javaSignatureAlgorithm;
  }

  public String getJavaPublicKeySpec() {
    return javaPublicKeySpec;
  }

  public String getSuiteID() {
    return "R" + suiteID;
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

  public static boolean isSupported(String rk) {
    return fromRKString(rk) != null;
  }

  public static RKSuiteIdentifier fromRKString(String rk) {
    if (rk == null || !prefixSupported(rk))
      return null;
    try {
      String suiteID = rk.split("-")[0];
      return RKSuiteIdentifier.valueOf(suiteID);
    } catch (Exception e) {
      return null;
    }
  }

  private static boolean prefixSupported(String mc) {
    if (mc == null)
      return false;
    for (String p : SUPPORTED_PREFIXES) {
      if (mc.startsWith(p))
        return true;
    }
    return false;
  }
}
