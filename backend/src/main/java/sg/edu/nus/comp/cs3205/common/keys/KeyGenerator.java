package sg.edu.nus.comp.cs3205.common.keys;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.keys.PemFile;

import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

// https://www.txedo.com/blog/java-generate-rsa-keys-write-pem-file/
public class KeyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(KeyGenerator.class.getSimpleName());

    public static void main(String[] args) {
        try {
            KeyPairGenerator keyPairGenerator = initialiseKeyPairGenerator();
            generateAndWriteKeyPairs("c1", keyPairGenerator);
            generateAndWriteKeyPairs("c2", keyPairGenerator);
            generateAndWriteKeyPairs("c3", keyPairGenerator);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException: ", e);
        } catch (NoSuchProviderException e) {
            logger.error("NoSuchProviderException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
    }

    private static void generateAndWriteKeyPairs(String keyPairName, KeyPairGenerator keyPairGenerator)
            throws IOException {
        logger.info("Generating keys for " + keyPairName);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        writePemFile(rsaPrivateKey, "RSA PRIVATE KEY", "keys/" + keyPairName + "_id_rsa");
        writePemFile(rsaPublicKey, "RSA PUBLIC KEY", "keys/" + keyPairName + "_id_rsa.pub");
    }

    private static KeyPairGenerator initialiseKeyPairGenerator()
            throws NoSuchAlgorithmException, NoSuchProviderException {
        logger.info("Initialising key pair generator.");
        Security.addProvider(new BouncyCastleProvider());
        logger.info("BouncyCastle provider added.");
        SecureRandom random = SecureRandom.getInstanceStrong();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(2048, random);
        return keyPairGenerator;
    }

    private static void writePemFile(Key key, String description, String filename)
            throws IOException {
        PemFile pemFile = new PemFile(key, description);
        pemFile.write(filename);
        logger.info(String.format("%s successfully writen in file %s.", description, filename));
    }

}
