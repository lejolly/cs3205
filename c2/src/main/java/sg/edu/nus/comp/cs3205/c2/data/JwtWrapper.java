package sg.edu.nus.comp.cs3205.c2.data;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import com.google.gson.Gson;

public class JwtWrapper {
    public static String getJws(Payload payload) throws JoseException {
        RsaJsonWebKey key = RsaJwkGenerator.generateJwk(2048);

        Gson gson = new Gson();
        String jsonPayload = gson.toJson(payload, Payload.class);

        JwtClaims claims = new JwtClaims();
        claims.setGeneratedJwtId();
        claims.setClaim("payload", jsonPayload);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(key.getPrivateKey());
        jws.setKeyIdHeaderValue(key.getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        return jws.getCompactSerialization();
    }

    public static String getJwt(Payload payload) throws JoseException {
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(payload, Payload.class);

        JwtClaims claims = new JwtClaims();
        claims.setGeneratedJwtId();
        claims.setClaim("payload", jsonPayload);

        JsonWebSignature jwt = new JsonWebSignature();
        jwt.setPayload(claims.toJson());
        jwt.setAlgorithmConstraints(AlgorithmConstraints.NO_CONSTRAINTS);
        jwt.setAlgorithmHeaderValue(AlgorithmIdentifiers.NONE);

        return jwt.getCompactSerialization();
    }

    public static Payload parseJws(String jws) throws JoseException, InvalidJwtException, MalformedClaimException {
        RsaJsonWebKey key = RsaJwkGenerator.generateJwk(2048);

        JwtConsumer consumer = new JwtConsumerBuilder().setVerificationKey(key.getKey()).build();

        JwtClaims claims = consumer.processToClaims(jws);
        String jsonPayload = claims.getClaimValue("payload", String.class);
        Gson gson = new Gson();
        return gson.fromJson(jsonPayload, Payload.class);
    }

    public static Payload parseJwt(String jwt) throws InvalidJwtException, MalformedClaimException {
        JwtConsumer consumer = new JwtConsumerBuilder().setDisableRequireSignature()
                .setJwsAlgorithmConstraints(AlgorithmConstraints.NO_CONSTRAINTS).build();
        JwtClaims claims = consumer.processToClaims(jwt);
        String jsonPayload = claims.getClaimValue("payload", String.class);
        Gson gson = new Gson();
        return gson.fromJson(jsonPayload, Payload.class);
    }
}
