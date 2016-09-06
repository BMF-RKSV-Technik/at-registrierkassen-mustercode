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

import at.asitplus.regkassen.common.RKSuiteIdentifier;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.CryptoException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static at.asitplus.regkassen.common.util.CashBoxUtils.get2ComplementRepForLong;

/**
 * Util class for AES encryption and decryption with different modes of
 * operation
 */
public class CryptoUtil {

  /**
   * Helper method to convert DER-encoded signature values (e.g. used by Java)
   * to concatenated signature values
   * (as used by the JWS-standard)
   *
   * @param derEncodedSignatureValue
   *          DER-encoded signature value
   * @return concatenated signature value (as used by JWS standard)
   * @throws IOException
   */
  public static byte[] convertDEREncodedSignatureToJWSConcatenated(final byte[] derEncodedSignatureValue)
      throws IOException {
    final ASN1InputStream asn1InputStream = new ASN1InputStream(derEncodedSignatureValue);
    final ASN1Primitive asn1Primitive = asn1InputStream.readObject();
    asn1InputStream.close();
    final ASN1Sequence asn1Sequence = (ASN1Sequence.getInstance(asn1Primitive));
    final ASN1Integer rASN1 = (ASN1Integer) asn1Sequence.getObjectAt(0);
    final ASN1Integer sASN1 = (ASN1Integer) asn1Sequence.getObjectAt(1);
    final X9IntegerConverter x9IntegerConverter = new X9IntegerConverter();
    final byte[] r = x9IntegerConverter.integerToBytes(rASN1.getValue(), 32);
    final byte[] s = x9IntegerConverter.integerToBytes(sASN1.getValue(), 32);

    final byte[] concatenatedSignatureValue = new byte[64];
    System.arraycopy(r, 0, concatenatedSignatureValue, 0, 32);
    System.arraycopy(s, 0, concatenatedSignatureValue, 32, 32);

    return concatenatedSignatureValue;
  }

  /**
   * Helper method to convert concatenated signature values (as used by the JWS-standard) to
   * DER-encoded signature values (e.g. used by Java)
   *
   * @param concatenatedSignatureValue
   *          concatenated signature value (as used by JWS standard)
   * @return DER-encoded signature value
   * @throws IOException
   */
  public static byte[] convertJWSConcatenatedToDEREncodedSignature(final byte[] concatenatedSignatureValue) throws IOException {

    final byte[] r = new byte[33];
    final byte[] s = new byte[33];
    System.arraycopy(concatenatedSignatureValue, 0, r, 1, 32);
    System.arraycopy(concatenatedSignatureValue, 32, s, 1, 32);
    final BigInteger rBigInteger = new BigInteger(r);
    final BigInteger sBigInteger = new BigInteger(s);

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final DERSequenceGenerator seqGen = new DERSequenceGenerator(bos);

    seqGen.addObject(new ASN1Integer(rBigInteger.toByteArray()));
    seqGen.addObject(new ASN1Integer(sBigInteger.toByteArray()));
    seqGen.close();
    bos.close();

    final byte[] derEncodedSignatureValue = bos.toByteArray();

    return derEncodedSignatureValue;
  }

  /**
   * Generates a random AES key for encrypting/decrypting the turnover value
   * ATTENTION: In a real cash box this key would be generated during the init
   * process and stored in a secure area
   *
   * @return generated AES key
   */
  public static SecretKey createAESKey() {
    try {
      final KeyGenerator kgen = KeyGenerator.getInstance("AES");
      final int keySize = 256;
      kgen.init(keySize);
      return kgen.generateKey();
    } catch (final NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * helper method to check whether the JVM has the unlimited strength policy
   * installed
   *
   * @return
   */
  public static boolean isUnlimitedStrengthPolicyAvailable() {
    try {
      return Cipher.getMaxAllowedKeyLength("AES") >= 256;
    } catch (final NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * convert base64 encoded AES key to JAVA SecretKey
   *
   * @param base64AESKey
   *          BASE64 encoded AES key
   * @return Java SecretKey representation of encoded AES key
   */
  public static SecretKey convertBase64KeyToSecretKey(final String base64AESKey) {
    final byte[] rawAesKey = CashBoxUtils.base64Decode(base64AESKey, false);
    final SecretKeySpec aesKey = new SecretKeySpec(rawAesKey, "AES");
    return aesKey;
  }

  /**
   * method for AES encryption in ECB mode
   *
   * @param concatenatedHashValue
   * @param turnoverCounter
   * @param symmetricKey
   */
  public static String encryptECB(final byte[] concatenatedHashValue, final Long turnoverCounter, final SecretKey symmetricKey,int turnOverCounterLengthInBytes)
      throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
      IllegalBlockSizeException, BadPaddingException {

    // extract bytes 0-15 from hash value
    final ByteBuffer byteBufferIV = ByteBuffer.allocate(16);
    byteBufferIV.put(concatenatedHashValue);
    final byte[] IV = byteBufferIV.array();

    // prepare data
    // block size for AES is 128 bit (16 bytes)
    // thus, the turnover counter needs to be inserted into an array of length 16

    //initialisation of the data which should be encrypted
    final ByteBuffer byteBufferData = ByteBuffer.allocate(16);
    byteBufferData.putLong(turnoverCounter);
    final byte[] data = byteBufferData.array();

    //now the turnover counter is represented in two's-complement representation (negative values are possible)
    //length is defined by the respective implementation (min. 5 bytes)
    byte[] turnOverCounterByteRep = get2ComplementRepForLong(turnoverCounter,turnOverCounterLengthInBytes);

    //two's-complement representation is copied to the data array, and inserted at index 0
    System.arraycopy(turnOverCounterByteRep,0,data,0,turnOverCounterByteRep.length);

    // prepare AES cipher with ECB mode, NoPadding is essential for the
    // decryption process. Padding could not be reconstructed due
    // to storing only 8 bytes of the cipher text (not the full 16 bytes)
    // (or 5 bytes if the mininum turnover length is used)
    //
    // Note: Due to the use of ECB mode, no IV is defined for initializing
    // the cipher. In addition, the data is not enciphered directly. Instead,
    // the computed IV is encrypted. The result is subsequently XORed
    // bitwise with the data to compute the cipher text.
    final Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");
    cipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
    final byte[] intermediateResult = cipher.doFinal(IV);

    final byte[] result = new byte[data.length];

    // xor encryption result with data
    for (int i = 0; i < data.length; i++) {
      result[i] = (byte) ((data[i]) ^ (intermediateResult[i]));
    }

    final byte[] encryptedTurnOverValue = new byte[turnOverCounterLengthInBytes];

    // turnover length is used
    System.arraycopy(result, 0, encryptedTurnOverValue, 0, turnOverCounterLengthInBytes);

    // encode result as BASE64
    return CashBoxUtils.base64Encode(encryptedTurnOverValue, false);
  }

  /**
   * method for AES decryption in ECB mode
   *
   * @param concatenatedHashValue
   * @param base64EncryptedTurnOverValue
   * @param symmetricKey
   */
  public static long decryptECB(final byte[] concatenatedHashValue, final String base64EncryptedTurnOverValue,
      final SecretKey symmetricKey)
      throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
      InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

    // extract bytes 0-15 from hash value
    final ByteBuffer byteBufferIV = ByteBuffer.allocate(16);
    byteBufferIV.put(concatenatedHashValue);
    final byte[] IV = byteBufferIV.array();

    final byte[] encryptedTurnOverValue = CashBoxUtils.base64Decode(base64EncryptedTurnOverValue, false);

    // prepare AES cipher with ECB mode
    //
    // Note: Due to the use of ECB mode, no IV is defined for initializing
    // the cipher. In addition, the data is not enciphered directly. Instead,
    // the IV computed above is encrypted again. The result is subsequently
    // XORed
    // bitwise with the cipher text to retrieve the plain data.
    final Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");
    cipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
    final byte[] intermediateResult = cipher.doFinal(IV);

    final byte[] result = new byte[encryptedTurnOverValue.length];

    // XOR decryption result with data
    for (int i = 0; i < encryptedTurnOverValue.length; i++) {
      result[i] = (byte) ((encryptedTurnOverValue[i]) ^ (intermediateResult[i]));
    }

    return getLong(result);

  }

  /**
   * method for AES encryption in CFB mode (for the first block CFB and CTR are
   * exactly the same
   *
   * @param concatenatedHashValue
   * @param turnoverCounter
   * @param symmetricKey
   */
  public static String encryptCFB(final byte[] concatenatedHashValue, final Long turnoverCounter, final SecretKey symmetricKey,int turnOverCounterLengthInBytes)
      throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
      InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

    // extract bytes 0-15 from hash value
    final ByteBuffer byteBufferIV = ByteBuffer.allocate(16);
    byteBufferIV.put(concatenatedHashValue);
    final byte[] IV = byteBufferIV.array();

    // prepare data
    // block size for AES is 128 bit (16 bytes)
    // thus, the turnover counter needs to be inserted into an array of length 16

    //initialisation of the data which should be encrypted
    final ByteBuffer byteBufferData = ByteBuffer.allocate(16);
    byteBufferData.putLong(turnoverCounter);
    final byte[] data = byteBufferData.array();

    //now the turnover counter is represented in two's-complement representation (negative values are possible)
    //length is defined by the respective implementation (min. 5 bytes)
    byte[] turnOverCounterByteRep = get2ComplementRepForLong(turnoverCounter,turnOverCounterLengthInBytes);

    //two's-complement representation is copied to the data array, and inserted at index 0
    System.arraycopy(turnOverCounterByteRep,0,data,0,turnOverCounterByteRep.length);

    // prepare AES cipher with CFB mode, NoPadding is essential for the
    // decryption process. Padding could not be reconstructed due
    // to storing only 8 bytes of the cipher text (not the full 16 bytes)
    // (or 5 bytes if the mininum turnover length is used)
    final IvParameterSpec ivSpec = new IvParameterSpec(IV);

    final Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding", "BC");
    cipher.init(Cipher.ENCRYPT_MODE, symmetricKey, ivSpec);

    // encrypt the turnover value with the prepared cipher
    final byte[] encryptedTurnOverValueComplete = cipher.doFinal(data);

    // extract bytes that will be stored in the receipt (only bytes 0-7)
    final byte[] encryptedTurnOverValue = new byte[turnOverCounterLengthInBytes]; // or 5 bytes if min.
    // turnover length is
    // used
    System.arraycopy(encryptedTurnOverValueComplete, 0, encryptedTurnOverValue, 0,
            turnOverCounterLengthInBytes);

    // encode result as BASE64
    final String base64EncryptedTurnOverValue = CashBoxUtils.base64Encode(encryptedTurnOverValue, false);



    return base64EncryptedTurnOverValue;

  }

  /**
   * method for AES decryption in CFB mode
   *
   * @param concatenatedHashValue
   * @param base64EncryptedTurnOverValue
   * @param symmetricKey
   */
  public static long decryptCFB(final byte[] concatenatedHashValue, final String base64EncryptedTurnOverValue,
      final SecretKey symmetricKey)
      throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
      InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

    // extract bytes 0-15 from hash value
    final ByteBuffer byteBufferIV = ByteBuffer.allocate(16);
    byteBufferIV.put(concatenatedHashValue);
    final byte[] IV = byteBufferIV.array();

    final byte[] encryptedTurnOverValue = CashBoxUtils.base64Decode(base64EncryptedTurnOverValue, false);

    // prepare AES cipher with CFB mode
    final IvParameterSpec ivSpec = new IvParameterSpec(IV);

    final Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding", "BC");
    cipher.init(Cipher.DECRYPT_MODE, symmetricKey, ivSpec);
    final byte[] testPlainTurnOverValueComplete = cipher.doFinal(encryptedTurnOverValue);
    return getLong(testPlainTurnOverValueComplete);

  }

  //this helper-method converts the byte-array-representation of the turnover counter to a long value
  //the constructor of the biginteger class correctly interprets the byte array as two's-complement rep and
  //creates the appropriate biginteger, which is then converted to a long value
  static long getLong(final byte[] bytes) {

    return new BigInteger(bytes).longValue();
  }

  /**
   * method for AES encryption in CTR mode
   *
   * @param concatenatedHashValue
   * @param turnoverCounter
   * @param symmetricKey
   */
  public static String encryptCTR(final byte[] concatenatedHashValue,  Long turnoverCounter, final SecretKey symmetricKey, int turnOverCounterLengthInBytes)
      throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
      InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

    // extract bytes 0-15 from hash value
    final ByteBuffer byteBufferIV = ByteBuffer.allocate(16);
    byteBufferIV.put(concatenatedHashValue);
    final byte[] IV = byteBufferIV.array();

    // prepare data
    // block size for AES is 128 bit (16 bytes)
    // thus, the turnover counter needs to be inserted into an array of length 16

    //initialisation of the data which should be encrypted
    final ByteBuffer byteBufferData = ByteBuffer.allocate(16);
    byteBufferData.putLong(turnoverCounter);
    final byte[] data = byteBufferData.array();

    //now the turnover counter is represented in two's-complement representation (negative values are possible)
    //length is defined by the respective implementation (min. 5 bytes)
    byte[] turnOverCounterByteRep = get2ComplementRepForLong(turnoverCounter,turnOverCounterLengthInBytes);

    //two's-complement representation is copied to the data array, and inserted at index 0
    System.arraycopy(turnOverCounterByteRep,0,data,0,turnOverCounterByteRep.length);

    // prepare AES cipher with CTR/ICM mode, NoPadding is essential for the
    // decryption process. Padding could not be reconstructed due
    // to storing only 8 bytes of the cipher text (not the full 16 bytes)
    // (or 5 bytes if the mininum turnover length is used)
    final IvParameterSpec ivSpec = new IvParameterSpec(IV);

    final Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
    cipher.init(Cipher.ENCRYPT_MODE, symmetricKey, ivSpec);

    // encrypt the turnover value with the prepared cipher
    final byte[] encryptedTurnOverValueComplete = cipher.doFinal(data);

    // extract bytes that will be stored in the receipt (only bytes 0-7)
    // cryptographic NOTE: this is only possible due to the use of the CTR
    // mode, would not work for ECB/CBC etc. modes
    final byte[] encryptedTurnOverValue = new byte[turnOverCounterLengthInBytes]; // or 5 bytes if min.
    // turnover length is
    // used
    System.arraycopy(encryptedTurnOverValueComplete, 0, encryptedTurnOverValue, 0,
            turnOverCounterLengthInBytes);

    // encode result as BASE64

    return CashBoxUtils.base64Encode(encryptedTurnOverValue, false);

  }

  /**
   * method for AES decryption in CTR mode
   *
   * @param concatenatedHashValue
   * @param base64EncryptedTurnOverValue
   * @param symmetricKey
   */
  public static long decryptCTR(final byte[] concatenatedHashValue, final String base64EncryptedTurnOverValue,
      final SecretKey symmetricKey)
      throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
      InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

    // extract bytes 0-15 from hash value
    final ByteBuffer byteBufferIV = ByteBuffer.allocate(16);
    byteBufferIV.put(concatenatedHashValue);
    final byte[] IV = byteBufferIV.array();

    final byte[] encryptedTurnOverValue = CashBoxUtils.base64Decode(base64EncryptedTurnOverValue, false);

    // prepare AES cipher with CTR/ICM mode
    final IvParameterSpec ivSpec = new IvParameterSpec(IV);

    final Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
    cipher.init(Cipher.DECRYPT_MODE, symmetricKey, ivSpec);
    final byte[] testPlainTurnOverValueComplete = cipher.doFinal(encryptedTurnOverValue);
    return getLong(testPlainTurnOverValueComplete);

  }

  // see next method
  public static long decryptTurnOverCounter(final String encryptedTurnOverCounterBase64, final String hashAlgorithm,
      final String cashBoxIDUTF8String, final String receiptIdentifierUTF8String, final String aesKeyBase64) throws Exception {
    final byte[] rawAesKey = CashBoxUtils.base64Decode(aesKeyBase64, false);
    final SecretKey aesKey = new SecretKeySpec(rawAesKey, "AES");
    return decryptTurnOverCounter(encryptedTurnOverCounterBase64, hashAlgorithm, cashBoxIDUTF8String,
        receiptIdentifierUTF8String, aesKey);
  }

  /**
   * decrypt the turnover counter with the given AES key, and parameters for IV
   * creation
   * Ref: Detailspezifikation Abs 8/Abs 9/Abs 10
   *
   * @param encryptedTurnOverCounterBase64
   *          encrypted turnover counter
   * @param hashAlgorithm
   *          hash-algorithm used to generate IV
   * @param cashBoxIDUTF8String
   *          cashbox-id, required for IV creation
   * @param receiptIdentifierUTF8String
   *          receiptidentifier, required for IV creation
   * @param aesKey
   *          aes key
   * @return decrypted turnover value as long
   * @throws Exception
   */
  public static long decryptTurnOverCounter(final String encryptedTurnOverCounterBase64, final String hashAlgorithm,
      final String cashBoxIDUTF8String, final String receiptIdentifierUTF8String, final SecretKey aesKey) throws Exception {
    // calc IV value (cashbox if + receipt identifer, both as UTF-8 Strings)
    final String IVUTF8StringRepresentation = cashBoxIDUTF8String + receiptIdentifierUTF8String;

    // calc hash
    final MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
    final byte[] hashValue = messageDigest.digest(IVUTF8StringRepresentation.getBytes());
    final byte[] concatenatedHashValue = new byte[16];
    System.arraycopy(hashValue, 0, concatenatedHashValue, 0, 16);

    // extract bytes 0-15 from hash value
    final ByteBuffer byteBufferIV = ByteBuffer.allocate(16);
    byteBufferIV.put(concatenatedHashValue);

    // IV for AES algorithm
    final byte[] IV = byteBufferIV.array();

    // prepare AES cipher with CTR/ICM mode, NoPadding is essential for the
    // decryption process. Padding could not be reconstructed due
    // to storing only 8 bytes of the cipher text (not the full 16 bytes) (or 5
    // bytes if the minimum turnover length is used)
    final IvParameterSpec ivSpec = new IvParameterSpec(IV);

    // start decryption process
    final ByteBuffer encryptedTurnOverValueComplete = ByteBuffer.allocate(16);

    // decode turnover base64 value
    final byte[] encryptedTurnOverValue = CashBoxUtils.base64Decode(encryptedTurnOverCounterBase64, false);

    // extract length (required to extract the correct number of bytes from
    // decrypted value
    final int lengthOfEncryptedTurnOverValue = encryptedTurnOverValue.length;

    // prepare for decryption (require 128 bit blocks...)
    encryptedTurnOverValueComplete.put(encryptedTurnOverValue);

    // decryption setup, AES ciper in CTR mode, NO PADDING!)
    final Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
    cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);

    // decrypt value, now we have a 128 bit block, with trailing junk bytes
    final byte[] plainTurnOverValueComplete = cipher.doFinal(encryptedTurnOverValue);

    // // remove junk bytes by extracting known length of plain text
    byte[] plainTurnOverValueTruncated = new byte[lengthOfEncryptedTurnOverValue];
    System.arraycopy(plainTurnOverValueComplete, 0, plainTurnOverValueTruncated, 0, lengthOfEncryptedTurnOverValue);

    return new BigInteger(plainTurnOverValueTruncated).longValue();
  }

  /**
   * computing chaining value for input receipt
   * @param input input receipt for which the chaining value should be calculated
   * @param rkSuite suite with information for chaining value calculation
   * @return BASE64-encoded chain value
   * @throws NoSuchAlgorithmException
   */
  public static String computeChainingValue(final String input, final RKSuiteIdentifier rkSuite)
      throws NoSuchAlgorithmException {
    final MessageDigest md = MessageDigest.getInstance(rkSuite.getHashAlgorithmForPreviousSignatureValue());

    // calculate hash value
    md.update(input.getBytes());
    final byte[] digest = md.digest();

    // extract number of bytes (N, defined in RKsuite) from hash value
    final int bytesToExtract = rkSuite.getNumberOfBytesExtractedFromPrevSigHash();
    final byte[] conDigest = new byte[bytesToExtract];
    System.arraycopy(digest, 0, conDigest, 0, bytesToExtract);

    // encode value as BASE64 String ==> chainValue
    return CashBoxUtils.base64Encode(conDigest, false);
  }

  /**
   * get hash value for given String
   * @param data string to be hashed
   * @return hash value as biginteger in hex representation
   * @throws CryptoException
   */
  public static String hashData(final String data) throws CryptoException {
    try {
      final MessageDigest md = MessageDigest.getInstance("SHA-256", "BC");
      md.update(data.getBytes("UTF-8"));
      return new BigInteger(md.digest()).toString(16);
    } catch (final Exception e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }
}
