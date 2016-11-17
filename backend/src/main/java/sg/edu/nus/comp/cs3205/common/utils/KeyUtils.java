package sg.edu.nus.comp.cs3205.common.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

// https://www.txedo.com/blog/java-generate-rsa-keys-write-pem-file/
// https://www.txedo.com/blog/java-read-rsa-keys-pem-file/
public class KeyUtils {

    private static final Logger logger = LoggerFactory.getLogger(KeyUtils.class);

    private static final String PUBLIC_KEY_TYPE = "PUBLIC KEY";
    private static final String PRIVATE_KEY_TYPE = "PRIVATE KEY";

    public static void checkAndAddBouncyCastleProvider() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
            logger.info("BouncyCastle provider added.");
        }
    }

    public static KeyPairGenerator initialiseKeyPairGenerator()
            throws NoSuchAlgorithmException, NoSuchProviderException {
        logger.info("Initialising key pair generator.");
        checkAndAddBouncyCastleProvider();
        SecureRandom random = SecureRandom.getInstanceStrong();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(2048, random);
        return keyPairGenerator;
    }

    public static void generateAndWriteKeyPairs(String keyPairName, KeyPairGenerator keyPairGenerator)
            throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        logger.info("Generating keys for " + keyPairName);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        writePemFile(rsaPrivateKey, PRIVATE_KEY_TYPE, "keys/" + keyPairName + "_id_rsa");
        writePemFile(rsaPublicKey, PUBLIC_KEY_TYPE, "keys/" + keyPairName + "_id_rsa.pub");
    }

    public static void writePemFile(Key key, String description, String filename)
            throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        PemObject pemObject = new PemObject(description, key.getEncoded());
        try (PemWriter pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(filename)))) {
            pemWriter.writeObject(pemObject);
        }
        logger.info(String.format("%s successfully writen in file %s.", description, filename));
        if (verifyKey(filename, key)) {
            logger.info(String.format("%s successfully verified.", description));
        } else {
            logger.warn(String.format("%s unable to be verified.", description));
        }
    }

    public static Key readPemFile(String filename)
            throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        checkAndAddBouncyCastleProvider();
        KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
        PEMParser pemParser = new PEMParser(new FileReader(filename));
        PemObject pemObject = (PemObject) pemParser.readPemObject();
        if (pemObject.getType().equals(PRIVATE_KEY_TYPE)) {
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
            return factory.generatePrivate(privKeySpec);
        } else if (pemObject.getType().equals(PUBLIC_KEY_TYPE)) {
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
            return factory.generatePublic(pubKeySpec);
        }
        return null;
    }

    public static boolean verifyKey(String filename, Key key)
            throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        logger.info(String.format("Verifying %s", filename));
        Key readKey = readPemFile(filename);
        return key.equals(readKey);
    }

}
