package com.example.eunaecho.gproject;

        import com.android.volley.Response;
        import com.android.volley.toolbox.StringRequest;

        import java.util.HashMap;
        import java.util.Map;


public class IdcheckDatabase extends StringRequest {
    final static private String URL = "http://jooy311.cafe24.com/idChecked.php";
    private Map<String, String> parameters;

    public IdcheckDatabase(String id, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("ID", id);
    }

    @Override
    public Map<String, String> getParams()
    {return parameters;}
}