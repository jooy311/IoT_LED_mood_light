package com.example.eunaecho.gproject;

/**
 * Created by Eunae Cho on 2018-10-13.
 */
        import com.android.volley.Response;
        import com.android.volley.toolbox.StringRequest;

        import java.util.HashMap;
        import java.util.Map;


public class LoginDatabase extends StringRequest {
    final static private String URL = "http://jooy311.cafe24.com/login.php";
    private Map<String, String> parameters;

    public LoginDatabase(String id, String pw, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("ID", id);
        parameters.put("PASSWORD", pw);
    }

    @Override
    public Map<String, String> getParams() {return parameters;}
}