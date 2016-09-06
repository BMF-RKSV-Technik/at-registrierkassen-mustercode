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

package at.asitplus.regkassen.common.util;

import at.asitplus.regkassen.common.MachineCodeValue;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CashBoxUtils {
  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * Method that converts a Java date to an ISO 8601 string
   *
   * @param date
   *          Java date to be converted to an ISO 8601 string
   * @return the date converted to an ISO 8601 string
   */
  public static String convertDateToISO8601(Date date) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    return dateFormat.format(date);
  }

  /**
   * Method that converts a an ISO 8601 string to Java date
   *
   * @param dateString date as ISO 8601 string
   *          Java date to be converted to an ISO 8601 string
   * @return converted
   */
  public static Date convertISO8601toDate(String dateString) throws ParseException {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    Date date = dateFormat.parse(dateString);
    return date;
  }

  /**
   * Helper method for storing printed PDF receipts to files
   *
   * @param printedReceipts
   *          binary representation of receipts to be stored
   * @param prefix
   *          prefix for file names
   * @param baseDir
   *          base directory, where files should be written
   */
  public static void writeReceiptsToFiles(List<byte[]> printedReceipts, String prefix, File baseDir) {
    try {
      int index = 1;
      for (byte[] printedReceipt : printedReceipts) {
        ByteArrayInputStream bIn = new ByteArrayInputStream(printedReceipt);
        File receiptFile = new File(baseDir, prefix + "Receipt " + index + ".pdf");
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
            new FileOutputStream(receiptFile));
        IOUtils.copy(bIn, bufferedOutputStream);
        bufferedOutputStream.close();
        index++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * BASE64 encoding helper function
   *
   * @param data
   *          binary representation of data to be encoded
   * @param isUrlSafe
   *          indicates whether BASE64 URL-safe encoding should be used
   *          (required for JWS)
   * @return BASE64 encoded representation of input data
   */
  public static String base64Encode(byte[] data, boolean isUrlSafe) {
    Base64 encoder = new Base64(isUrlSafe);
    return new String(encoder.encode(data)).replace("\r\n", "");
  }

  /**
   * BASE64 decoder helper function
   *
   * @param base64Data
   *          BASE64 encoded data
   * @param isUrlSafe
   *          indicates whether BASE64 URL-safe encoding was used (required for
   *          JWS)
   * @return binary representation of decoded data
   */
  public static byte[] base64Decode(String base64Data, boolean isUrlSafe) {
    Base64 decoder = new Base64(isUrlSafe);
    return decoder.decode(base64Data);
  }

  /**
   * BASE32 encoding helper (required for OCR representation)
   *
   * @param data
   *          binary representation of data to be encoded
   * @return BASE32 encoded representation of input data
   */
  public static String base32Encode(byte[] data) {
    Base32 encoder = new Base32();
    return new String(encoder.encode(data)).replace("\r\n", "");
  }

  /**
   * BASE32 decoding helper (required for OCR representation)
   *
   * @param base32Data
   *          BASE32 encoded data
   * @return binary representation of decoded data
   */
  public static byte[] base32Decode(String base32Data) {
    Base32 decoder = new Base32();
    return decoder.decode(base32Data);
  }

  /**
   * get a value from the machine code representation
   *
   * @param machineCodeRepresentation
   *          machinecode representation (QR or OCR code)
   * @param machineCodeValue
   *          which value? e.g. signature value, rk-suite etc.
   * @return the extracted value as String
   */
  public static String getValueFromMachineCode(String machineCodeRepresentation,
      MachineCodeValue machineCodeValue) {
    // plus 1 due to leading "_"
    return machineCodeRepresentation.split("_")[machineCodeValue.getIndex() + 1];
  }

  /**
   * convert JWS compact representation to QR-machine-code representation of
   * signed receipt
   *
   * @param jwsCompactRepresentationOfReceipt
   *          JWS compact representation of signed receipt
   * @return the QR-machine-code-representation of signed receipt
   */
  public static String getQRCodeRepresentationFromJWSCompactRepresentation(
      String jwsCompactRepresentationOfReceipt) {
    // get data
    String jwsPayloadEncoded = jwsCompactRepresentationOfReceipt.split("\\.")[1];
    String jwsSignatureEncoded = jwsCompactRepresentationOfReceipt.split("\\.")[2];

    String payload = new String(CashBoxUtils.base64Decode(jwsPayloadEncoded, true), Charset.forName("UTF-8"));
    String signature = CashBoxUtils.base64Encode(CashBoxUtils.base64Decode(jwsSignatureEncoded, true), false);

    return payload + "_" + signature;
  }

  /**
   * convert JWS compact representation to OCR-machine-code representation of
   * signed receipt
   *
   * @param jwsCompactRepresentationOfReceipt
   *          JWS compact representation of signed receipt
   * @return the OCR-machine-code-representation of signed receipt
   */
  public static String getOCRCodeRepresentationFromJWSCompactRepresentation(
      String jwsCompactRepresentationOfReceipt) {
    // Ref: Detailspezifikation Abs 14
    // could be done more efficiently, but in this way the process of converting
    // the QR representation to the OCR representation is highlighted
    // get QR-Code representation from JWS compact representation
    String qrCodeRepresentation = CashBoxUtils
        .getQRCodeRepresentationFromJWSCompactRepresentation(jwsCompactRepresentationOfReceipt);

    // extract all elements
    String el1_rkSuite = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.RK_SUITE);
    String el2_cashboxID = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.CASHBOX_ID);
    String el3_receiptIdentifier = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.RECEIPT_IDENTIFIER);
    String el4_timeAndDate = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.RECEIPT_DATE_AND_TIME);
    String el5_taxSet_NORMAL = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.SUM_TAX_SET_NORMAL);
    String el6_taxSet_ERMAESSIGT1 = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.SUM_TAX_SET_ERMAESSIGT1);
    String el7_taxSet_ERMAESSIGT2 = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.SUM_TAX_SET_ERMAESSIGT2);
    String el8_taxSet_NULL = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.SUM_TAX_SET_NULL);
    String el9_taxSet_BESONDERS = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.SUM_TAX_SET_BESONDERS);
    String el10_encryptedTurnOverValue = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.ENCRYPTED_TURN_OVER_VALUE);
    String el11_certificateSerialNumberOrCompanyAndKeyID = CashBoxUtils.getValueFromMachineCode(
        qrCodeRepresentation, MachineCodeValue.CERTIFICATE_SERIAL_NUMBER_OR_COMPANYID_AND_KEYID);
    String el12_chainValue = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.CHAINING_VALUE_PREVIOUS_RECEIPT);
    String el13_signatureValue = CashBoxUtils.getValueFromMachineCode(qrCodeRepresentation,
        MachineCodeValue.SIGNATURE_VALUE);

    // re-encode the following values from BASE64 to BASE32
    el10_encryptedTurnOverValue = CashBoxUtils
        .base32Encode(CashBoxUtils.base64Decode(el10_encryptedTurnOverValue, false));
    el12_chainValue = CashBoxUtils.base32Encode(CashBoxUtils.base64Decode(el12_chainValue, false));
    el13_signatureValue = CashBoxUtils.base32Encode(CashBoxUtils.base64Decode(el13_signatureValue, false));

    // combine all values to OCR representation
    String ocrCodeRepresentation = "_" + el1_rkSuite + "_" + el2_cashboxID + "_" + el3_receiptIdentifier + "_"
        + el4_timeAndDate + "_" + el5_taxSet_NORMAL + "_" + el6_taxSet_ERMAESSIGT1 + "_"
        + el7_taxSet_ERMAESSIGT2 + "_" + el8_taxSet_NULL + "_" + el9_taxSet_BESONDERS + "_"
        + el10_encryptedTurnOverValue + "_" + el11_certificateSerialNumberOrCompanyAndKeyID + "_"
        + el12_chainValue + "_" + el13_signatureValue;

    return ocrCodeRepresentation;
  }

  /**
   * extract the payload of the QR-Code (remove signature value)
   *
   * @param qrCodeRepresentation
   *          the QR-machine-code-representation of signed receipt
   * @return extracted payload of QR-machine-code-representation of signed
   *         receipt
   */
  public static String getPayloadFromQRCodeRepresentation(String qrCodeRepresentation) {
    String[] elements = qrCodeRepresentation.split("_");
    String payload = "";
    for (int i = 0; i < 13; i++) {
      payload += elements[i];
      if (i < 12) {
        payload += "_";
      }
    }
    return payload;
  }

  /**
   * convert QR-machine-code representation of signed receipt to JWS compact
   * representation
   *
   * @param qrMachineCodeRepresentation
   *          the QR-machine-code-representation of signed receipt
   * @return JWS compact representation of signed receipt
   */
  public static String getJWSCompactRepresentationFromQRMachineCodeRepresentation(
      String qrMachineCodeRepresentation) {
    String payload = getPayloadFromQRCodeRepresentation(qrMachineCodeRepresentation);

    String jwsPayload = CashBoxUtils.base64Encode(payload.getBytes(Charset.forName("UTF-8")), true);

    String jwsHeader = "eyJhbGciOiJFUzI1NiJ9";
    String jwsSignature = CashBoxUtils.base64Encode(CashBoxUtils.base64Decode(
        CashBoxUtils.getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SIGNATURE_VALUE),
        false), true);

    return jwsHeader + "." + jwsPayload + "." + jwsSignature;
  }

  /**
   * get double value from arbitrary String represent a double (0,00) or (0.00)
   *
   * @param taxSetValue
   *          double value as String
   * @return double value
   * @throws Exception
   */
  public static double getDoubleFromTaxSet(String taxSetValue) throws Exception {
    // try format ("0,00")
    NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
    DecimalFormat decimalFormat = (DecimalFormat) nf;
    Exception parseException;
    try {
      return decimalFormat.parse(taxSetValue).doubleValue();
    } catch (ParseException ignored) {
    }
    // if Austrian/German format fail, try US format (0.00)
    nf = NumberFormat.getNumberInstance(Locale.US);
    decimalFormat = (DecimalFormat) nf;
    try {
      return decimalFormat.parse(taxSetValue).doubleValue();
    } catch (ParseException e) {
      parseException = e;
    }
    throw parseException;
  }

  /**
   * check whether the JWS compact representation of signed receipt contains
   * indicator for damaged signature creation device
   *
   * @param jwsCompactRepresentation
   *          JWS compact representation of signed receipt
   * @return signature device was damaged?
   */
  public static boolean checkReceiptForDamagedSigatureCreationDevice(String jwsCompactRepresentation) {
    String encodedSignatureValueBase64 = jwsCompactRepresentation.split("\\.")[2];
    String decodedSignatureValue = new String(CashBoxUtils.base64Decode(encodedSignatureValueBase64, true));
    return "Sicherheitseinrichtung ausgefallen".equals(decodedSignatureValue);
  }

  /**
   * get sum of all tax-set turnover values from QR-machine-code-representation
   * of signed receipt
   *
   * @param qrMachineCodeRepresentation
   *          QR-machine-code-representation of signed receipt
   * @param calcAbsValue
   *          flag which indicates whether abs(value) should be used, if set,
   *          this can be used to check whether
   *          the sum is zero. this is needed for checking the first receipt of
   *          the DEP or the first receipt after
   *          recovering from a failed signature creation device.
   * @return
   * @throws Exception
   */
  public static double getTaxSetTurnOverSumFromQRMachineCodeRepresentation(String qrMachineCodeRepresentation,
      boolean calcAbsValue) throws Exception {
    double currentTaxSetNormal = CashBoxUtils.getDoubleFromTaxSet(CashBoxUtils
        .getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SUM_TAX_SET_NORMAL));
    double currentTaxSetErmaessigt1 = CashBoxUtils.getDoubleFromTaxSet(CashBoxUtils
        .getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SUM_TAX_SET_ERMAESSIGT1));
    double currentTaxSetErmaessigt2 = CashBoxUtils.getDoubleFromTaxSet(CashBoxUtils
        .getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SUM_TAX_SET_ERMAESSIGT2));
    double currentTaxSetNull = CashBoxUtils.getDoubleFromTaxSet(
        CashBoxUtils.getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SUM_TAX_SET_NULL));
    double currentTaxSetBesonders = CashBoxUtils.getDoubleFromTaxSet(CashBoxUtils
        .getValueFromMachineCode(qrMachineCodeRepresentation, MachineCodeValue.SUM_TAX_SET_BESONDERS));

    if (calcAbsValue) {
      return Math.abs(currentTaxSetNormal) + Math.abs(currentTaxSetErmaessigt1)
          + Math.abs(currentTaxSetErmaessigt2) + Math.abs(currentTaxSetNull)
          + Math.abs(currentTaxSetBesonders);
    } else {
      return currentTaxSetNormal + currentTaxSetErmaessigt1 + currentTaxSetErmaessigt2 + currentTaxSetNull
          + currentTaxSetBesonders;
    }
  }

  /**
   * extract certificates from DEP Export Format String representation
   *
   * @param base64EncodedCertificate
   *          BASE64 encoded DER-encoded-certificate
   * @return java object for X509Certificate
   * @throws CertificateException
   */
  public static X509Certificate parseCertificate(String base64EncodedCertificate)
      throws CertificateException {
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    ByteArrayInputStream bIn = new ByteArrayInputStream(
        CashBoxUtils.base64Decode(base64EncodedCertificate, false));
    return (X509Certificate) certificateFactory.generateCertificate(bIn);
  }

  /**
   * extract certificates from DEP Export Format String representation
   *
   * @param base64EncodedCertificates
   *          BASE64 encoded DER-encoded-certificates
   * @return java objects for X509Certificate
   * @throws CertificateException
   */
  public static List<X509Certificate> parseCertificates(String[] base64EncodedCertificates)
      throws CertificateException {
    List<X509Certificate> certificates = new ArrayList<X509Certificate>();
    for (String base64EncodedCertificate : base64EncodedCertificates) {
      certificates.add(parseCertificate(base64EncodedCertificate));
    }
    return certificates;
  }

  /**
   * determine whether current receipt is "Trainingsbuchung", check via
   * encrypted turnover value, (BASE64 encoding of TRA)
   *
   * @param jwsCompactRepresentation
   *          JWS compact representation of signed receipt
   * @return is "Trainingsbuchung"?
   */
  public static boolean isJWSCompactRepTrainingReceipt(String jwsCompactRepresentation) {
    return isQRCodeRepTrainingReceipt(
        CashBoxUtils.getQRCodeRepresentationFromJWSCompactRepresentation(jwsCompactRepresentation));
  }

  /**
   * see above, same method, here: for machine code rep
   *
   * @param qrMachineCodeRepresentation
   * @return
   */
  public static boolean isQRCodeRepTrainingReceipt(String qrMachineCodeRepresentation) {
    String encryptedTurnOverCounter = CashBoxUtils.getValueFromMachineCode(qrMachineCodeRepresentation,
        MachineCodeValue.ENCRYPTED_TURN_OVER_VALUE);
    String decodedTurnOverCounter = new String(CashBoxUtils.base64Decode(encryptedTurnOverCounter, false));
    return "TRA".equals(decodedTurnOverCounter);
  }

  /**
   * determine wheter current receipt is "Stornobuchung", check via encrypted
   * turnover value, (BASE64 encoding of STO)
   *
   * @param jwsCompactRepresentation
   *          JWS compact representation of signed receipt
   * @return is "Stornobuchung"?
   */
  public static boolean isJWSCompactRepStornoReceipt(String jwsCompactRepresentation) {
    return isQRCodeRepStornoReceipt(
        CashBoxUtils.getQRCodeRepresentationFromJWSCompactRepresentation(jwsCompactRepresentation));
  }

  /*
   * see previous method
   */
  public static boolean isQRCodeRepStornoReceipt(String qrMachineCodeRepresentation) {
    String encryptedTurnOverCounter = CashBoxUtils.getValueFromMachineCode(qrMachineCodeRepresentation,
        MachineCodeValue.ENCRYPTED_TURN_OVER_VALUE);
    String decodedTurnOverCounter = new String(CashBoxUtils.base64Decode(encryptedTurnOverCounter, false));
    return "STO".equals(decodedTurnOverCounter);
  }

  /**
   * get two's-complement representation for given long value, result is encoded into byte-array of the given
   * length
   * @param value long value to be encoded
   * @param numberOfBytesFor2ComplementRepresentation length of resulting byte-array
   * @return byte array of turnover counter, in two's-complement representation
   */
  public static byte[] get2ComplementRepForLong(long value,int numberOfBytesFor2ComplementRepresentation) {
    if (numberOfBytesFor2ComplementRepresentation<1 || (numberOfBytesFor2ComplementRepresentation>8)) {
      throw new IllegalArgumentException();
    }

    //create byte buffer, max length 8 bytes (equal to long representation)
    ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    byteBuffer.putLong(value);
    byte[] longRep = byteBuffer.array();

    //if given length for encoding is equal to 8, we are done
    if (numberOfBytesFor2ComplementRepresentation==8) {
      return longRep;
    }

    //if given length of encoding is less than 8 bytes, we truncate the representation (of course one needs to be sure
    //that the given long value is not larger than the created byte array
    byte[] byteRep = new byte[numberOfBytesFor2ComplementRepresentation];

    //truncating the 8-bytes long representation
    System.arraycopy(longRep,8-numberOfBytesFor2ComplementRepresentation,byteRep,0,numberOfBytesFor2ComplementRepresentation);
    return byteRep;
  }
}
