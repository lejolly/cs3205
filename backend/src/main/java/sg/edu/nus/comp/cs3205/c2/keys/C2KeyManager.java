package sg.edu.nus.comp.cs3205.c2.keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;
import sg.edu.nus.comp.cs3205.common.utils.KeyUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

public class C2KeyManager extends AbstractManager {

    private static final Logger logger = LoggerFactory.getLogger(C2KeyManager.class.getSimpleName());

    public static RSAPublicKey c3RsaPublicKey = null;
    public static RSAPrivateKey c2RsaPrivateKey = null;

    public C2KeyManager() {
        logger.info("Initialising keys for C2.");
        try {
            c3RsaPublicKey = (RSAPublicKey) KeyUtils.readPemFile("keys/c3_id_rsa.pub");
            c2RsaPrivateKey = (RSAPrivateKey) KeyUtils.readPemFile("keys/c2_id_rsa");
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
