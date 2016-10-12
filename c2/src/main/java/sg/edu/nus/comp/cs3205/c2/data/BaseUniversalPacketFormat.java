package sg.edu.nus.comp.cs3205.c2.data;

import org.jose4j.jwt.JwtClaims;

/**
 * Universal Packet Format
 *
 * action - Determines the nature of the packet, can be in the form _request or _response
 *
 * data - Data associated with the request / response
 *
 * error - If this field is set, an error has occurred along the processing chain and all
 * layers should not modify the packet except to generate an error display
 *
 * id - An identifier for the current layer that generated this packet
 *
 * input - The universal packet received as input
 *
 * sign - Signature generated from concatenating the entire packet contents signed with the
 * private key of the processing component, can be verified by an auditor possessing the
 * corresponding public keys
 */
abstract class BaseUniversalPacketFormat {

    private String action;
    private String data;
    private String error;
    private String id;
    private String input;
    private String sign;

    BaseUniversalPacketFormat(String action, String data, String error, String id, String input, String sign) {
        this.action = action;
        this.data = data;
        this.error = error;
        this.id = id;
        this.input = input;
        this.sign = sign;
    }

    public JwtClaims getClaims() {
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setClaim("action", action);
        jwtClaims.setClaim("data", data);
        jwtClaims.setClaim("error", error);
        jwtClaims.setClaim("id", id);
        jwtClaims.setClaim("input", input);
        jwtClaims.setClaim("sign", sign);
        return jwtClaims;
    }

    public String getAction() {
        return action;
    }

    public String getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public String getId() {
        return id;
    }

    public String getInput() {
        return input;
    }

    public String getSign() {
        return sign;
    }

    void setAction(String action) {
        this.action = action;
    }

    void setData(String data) {
        this.data = data;
    }

    void setError(String error) {
        this.error = error;
    }

    void setId(String id) {
        this.id = id;
    }

    void setInput(String input) {
        this.input = input;
    }

    void setSign(String sign) {
        this.sign = sign;
    }

}
