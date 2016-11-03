package sg.edu.nus.comp.cs3205.common.keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.utils.FileUtils;
import sg.edu.nus.comp.cs3205.common.utils.KeyUtils;

import java.io.File;
import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class KeyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(KeyGenerator.class);

    public static void main(String[] args) {
        try {
            FileUtils.createDirs(new File("keys"));
            KeyPairGenerator keyPairGenerator = KeyUtils.initialiseKeyPairGenerator();
            KeyUtils.generateAndWriteKeyPairs("c1", keyPairGenerator);
            KeyUtils.generateAndWriteKeyPairs("c2", keyPairGenerator);
            KeyUtils.generateAndWriteKeyPairs("c3", keyPairGenerator);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException: ", e);
        } catch (NoSuchProviderException e) {
            logger.error("NoSuchProviderException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        } catch (InvalidKeySpecException e) {
            logger.error("InvalidKeySpecException: ", e);
        }
    }

}
