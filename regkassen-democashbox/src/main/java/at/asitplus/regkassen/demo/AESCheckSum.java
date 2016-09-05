package at.asitplus.regkassen.demo;


import at.asitplus.regkassen.common.util.CashBoxUtils;
import at.asitplus.regkassen.common.util.CryptoUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * DEMO-code for creating the check sum of an AES-key, this checksum can optionally be used for the manual registration of
 * a cash-register in FinanzOnline and helps to reduce errors.
 */
public class AESCheckSum {
    public static void main(String[] args) {

        //----------------------------------------------------------------------------------------------------
        //basic inits
        //add bouncycastle provider
        Security.addProvider(new BouncyCastleProvider());

        //----------------------------------------------------------------------------------------------------
        //check if unlimited strength policy files are installed, they are required for strong crypto algorithms ==> AES 256
        if (!CryptoUtil.isUnlimitedStrengthPolicyAvailable()) {
            System.out.println("Your JVM does not provide the unlimited strength policy. However, this policy is required to enable strong cryptography (e.g. AES with 256 bits). Please install the required policy files.");
            System.exit(0);
        }

        //AES key for encrypting the turnover counter
        SecretKey secretKey = CryptoUtil.createAESKey();

        //base64 encoding of AES key (DO NOT USE BASE64-URL encoding)
        String base64AESKey = CashBoxUtils.base64Encode(secretKey.getEncoded(), false);
        System.out.println("base64AESKey: " + base64AESKey);

        //checksum calculation
        int N = 3;
        String valSum = null;
        try {
            valSum = calcCheckSumFromKey(base64AESKey, N);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA256-hash algorithm not available, please check your JVM installation.");
            System.exit(0);
        }
        System.out.println("check sum: " + valSum);
    }

    public static boolean checkValSum(int numberOfBytes, String base64AESKey, String userCheckSum) {
        String calculatedCheckSum = null;
        try {
            calculatedCheckSum = calcCheckSumFromKey(base64AESKey, numberOfBytes);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA256-hash algorithm not available, please check your JVM installation.");
            System.exit(0);
        }
        return calculatedCheckSum.equals(userCheckSum);
    }

    /**
     * Calculation of check sum
     */
    public static String calcCheckSumFromKey(String base64AESKey, int numberOfBytes) throws NoSuchAlgorithmException {

        //calculate SHA256 hash of AES key
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(base64AESKey.getBytes());
        byte[] hashValue = md.digest();

        //extract N bytes from hash (N=3)
        byte[] hashValueBytes = new byte[numberOfBytes];
        System.arraycopy(hashValue, 0, hashValueBytes, 0, numberOfBytes);

        //BASE64-encode check sum
        String base64ValueValidation = CashBoxUtils.base64Encode(hashValueBytes, false);

        //remove padding character "="
        base64ValueValidation = base64ValueValidation.replace("=", "");
        return base64ValueValidation;
    }

}
