package sg.edu.nus.comp.cs3205.c3.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.utils.KeyUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

public class C3KeyManager {

    private static final Logger logger = LoggerFactory.getLogger(C3KeyManager.class);

    public static RSAPublicKey c2RsaPublicKey = null;
    public static RSAPrivateKey c3RsaPrivateKey = null;

    public C3KeyManager() {
        logger.info("Initialising keys for C3.");
        try {
            c2RsaPublicKey = (RSAPublicKey) KeyUtils.readPemFile("keys/c2_id_rsa.pub");
            c3RsaPrivateKey = (RSAPrivateKey) KeyUtils.readPemFile("keys/c3_id_rsa");
        } catch (IOException e) {
            logger.error("IOException: ", e);
        } catch (NoSuchProviderException e) {
            logger.error("NoSuchProviderException: ", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException: ", e);
        } catch (InvalidKeySpecException e) {
            logger.error("InvalidKeySpecException: ", e);
        }
    }

}
