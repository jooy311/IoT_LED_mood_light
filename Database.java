package com.example.eunaecho.gproject;
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.graphics.Bitmap;
        import android.provider.ContactsContract;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.toolbox.StringRequest;

        import java.io.ByteArrayOutputStream;
        import java.io.DataOutputStream;
        import java.io.FileInputStream;
        import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.sql.Blob;
        import java.util.HashMap;
        import java.util.Map;

/**
 * Created by Eunae Cho on 2018-07-30.
 */

public class Database extends StringRequest {

    final static private String URL = "http://jooy311.cafe24.com/join.php";

    private Map<String, String> parameters;
    private Map<String, byte[]> parameters2; //바이너리 형태의 프로필사진을 전달해주기 위한 파라미터

    public Database(String id, String pw, String name, String birth, String phone, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        parameters = new HashMap<>(); //초기화
        parameters.put("ID", id);
        parameters.put("PASSWORD", pw);
        parameters.put("NAME", name);
        parameters.put("BIRTH", birth); //뒤에 ""를 붙임으로써 String형으로 만들어주기
        parameters.put("PHONE", phone);
        //parameters.put("PHOTO", photo + "BLOB");//이건 어떻게 해야 빨간줄 없이 들어갈까/
    }

    //프로필 사진은 따로 테이블을 만들어서 전달해주기로 함
    public Database(String id, byte[] photo, Response.Listener<String> listener2) {
        super(Method.POST, URL, listener2, null);
        parameters.put("ID", id);
        parameters2.put("PHOTO", photo);
    }

    //로그인 테이블 데이터 베이스
    public Database(String id, String pw, Response.Listener<String> listener3) {
        super(Method.POST, URL, listener3, null);
        parameters.put("ID", id);
        parameters.put("PW", pw);
    }

    public Database(String id, String date, String contents, Response.Listener<String> listener4) {
        super(Method.POST, URL, listener4, null);
        parameters.put("ID", id);
        parameters.put("DATE", date);
        parameters.put("CONTENTS", contents);
    }


    public Map<String, String> getParams() {
        return parameters; //파라미터 반환
    }

    public Map<String, byte[]> getParams2() {
        return parameters2;//다시짜야함}

    }
}