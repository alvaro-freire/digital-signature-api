package com.example.digitalsignatureapi.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtil {

    private static final String AES = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";
    private static final int IV_SIZE = 16;
    private static final int KEY_SIZE = 32;

    private static SecretKey getKeyFromPassword(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = Arrays.copyOf(sha.digest(password.getBytes(StandardCharsets.UTF_8)), KEY_SIZE);
        return new SecretKeySpec(key, AES);
    }

    public static String encrypt(String data, String password) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        SecretKey key = getKeyFromPassword(password);
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDataWithIv = new byte[IV_SIZE + encryptedData.length];
        System.arraycopy(iv, 0, encryptedDataWithIv, 0, IV_SIZE);
        System.arraycopy(encryptedData, 0, encryptedDataWithIv, IV_SIZE, encryptedData.length);
        return Base64.getEncoder().encodeToString(encryptedDataWithIv);
    }

    public static String decrypt(String encryptedData, String password) throws Exception {
        byte[] encryptedDataWithIv = Base64.getDecoder().decode(encryptedData);
        byte[] iv = Arrays.copyOfRange(encryptedDataWithIv, 0, IV_SIZE);
        byte[] encryptedBytes = Arrays.copyOfRange(encryptedDataWithIv, IV_SIZE, encryptedDataWithIv.length);
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        SecretKey key = getKeyFromPassword(password);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decryptedData = cipher.doFinal(encryptedBytes);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
