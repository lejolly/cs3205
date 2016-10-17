package sg.edu.nus.comp.cs3205.common.utils;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import java.security.Key;

public class JwsUtils {

    public static String getSimpleSignedMessageWithId(Key key, String id, String message) throws JoseException {
        JsonWebSignature jws = new JsonWebSignature();
        JwtClaims jwtClaims = new JwtClaims();
        jws.setKey(key);
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);
        jwtClaims.setClaim("id", id);
        jwtClaims.setClaim("message", message);
        jws.setPayload(jwtClaims.toJson());
        return jws.getCompactSerialization();
    }

    public static String getSignedFieldWithId(Key key, String id, String field, String value) throws JoseException {
        JsonWebSignature jws = new JsonWebSignature();
        JwtClaims jwtClaims = new JwtClaims();
        jws.setKey(key);
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);
        jwtClaims.setClaim("id", id);
        jwtClaims.setClaim(field, value);
        jws.setPayload(jwtClaims.toJson());
        return jws.getCompactSerialization();
    }

    public static JwtClaims consumeSignedMessageWithId(Key key, String jws) throws InvalidJwtException {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder().setVerificationKey(key).build();
        return jwtConsumer.processToClaims(jws);
    }

}
