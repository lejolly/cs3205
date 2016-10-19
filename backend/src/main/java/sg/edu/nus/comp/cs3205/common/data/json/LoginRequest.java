package sg.edu.nus.comp.cs3205.common.data.json;

public class LoginRequest extends BaseJSONFormat {

    private static final String ACTION = "login_request";

    public LoginRequest() {
        setAction(ACTION);
    }

    private LoginRequest(BaseJSONFormat baseJSONFormat) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setData(baseJSONFormat.getData());
        loginRequest.setId(baseJSONFormat.getId());
        loginRequest.setError(baseJSONFormat.getError());
        loginRequest.setInput(baseJSONFormat.getInput());
    }

    public static LoginRequest fromBaseFormat(BaseJSONFormat baseJSONFormat) {
        if (baseJSONFormat.getAction().equals(ACTION)) {
            return new LoginRequest(baseJSONFormat);
        } else {
            return null;
        }
    }

}
