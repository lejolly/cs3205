package sg.edu.nus.comp.cs3205.common.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.io.pem.PemObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

// https://www.txedo.com/blog/java-generate-rsa-keys-write-pem-file/
// https://www.txedo.com/blog/java-read-rsa-keys-pem-file/
// http://stackoverflow.com/questions/31007907/publickey-handling-java-php/31023995#31023995
public class KeyUtils {

    private static final Logger logger = LoggerFactory.getLogger(KeyUtils.class);

    private static final String PUBLIC_KEY_TYPE = "PUBLIC KEY";
    private static final String PRIVATE_KEY_TYPE = "RSA PRIVATE KEY";
    private static final String PUBLICKEY_PREFIX    = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLICKEY_POSTFIX   = "-----END PUBLIC KEY-----";
    private static final String PRIVATEKEY_PREFIX   = "-----BEGIN RSA PRIVATE KEY-----";
    private static final String PRIVATEKEY_POSTFIX  = "-----END RSA PRIVATE KEY-----";

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
            throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException,
            InvalidKeyException, SignatureException {
        logger.info("Generating keys for " + keyPairName);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        logger.info("Private key algorithm: " + rsaPrivateKey.getAlgorithm() + " Format: " +
                rsaPrivateKey.getFormat());
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        logger.info("Public key algorithm: " + rsaPublicKey.getAlgorithm() + " Format: " +
                rsaPublicKey.getFormat());
        String privateKeyPEM = PRIVATEKEY_PREFIX + "\n" + DatatypeConverter.printBase64Binary(
                rsaPrivateKey.getEncoded()).replaceAll("(.{64})", "$1\n") + "\n" + PRIVATEKEY_POSTFIX;
        String publicKeyPEM = PUBLICKEY_PREFIX + "\n" + DatatypeConverter.printBase64Binary(
                rsaPublicKey.getEncoded()).replaceAll("(.{64})", "$1\n") + "\n" + PUBLICKEY_POSTFIX;
        writeStringToFile(privateKeyPEM, "keys/" + keyPairName + "_id_rsa");
        writeStringToFile(publicKeyPEM, "keys/" + keyPairName + "_id_rsa.pub");
        if (verifyKey("keys/" + keyPairName + "_id_rsa", rsaPrivateKey)) {
            logger.info(String.format("%s successfully verified.", "keys/" + keyPairName + "_id_rsa"));
        } else {
            logger.warn(String.format("%s unable to be verified.", "keys/" + keyPairName + "_id_rsa"));
        }
        if (verifyKey("keys/" + keyPairName + "_id_rsa.pub", rsaPublicKey)) {
            logger.info(String.format("%s successfully verified.", "keys/" + keyPairName + "_id_rsa.pub"));
        } else {
            logger.warn(String.format("%s unable to be verified.", "keys/" + keyPairName + "_id_rsa.pub"));
        }
    }

    public static void writeStringToFile(String s, String filename) throws FileNotFoundException {
        try(PrintWriter out = new PrintWriter(filename)){
            out.println(s);
            out.flush();
            out.close();
        }
        logger.info(String.format("Successfully written in file %s.", filename));
    }

    public static Key readPemFile(String filename)
            throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        checkAndAddBouncyCastleProvider();
        KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
        PEMParser pemParser = new PEMParser(new FileReader(filename));
        PemObject pemObject = pemParser.readPemObject();
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
