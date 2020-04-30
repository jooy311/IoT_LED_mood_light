package com.example.eunaecho.gproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //컴포넌트 들고오기
        final EditText id = (EditText) findViewById(R.id.txtID);
        final EditText pw = (EditText) findViewById(R.id.txtPW);

        final Button btnLogin = findViewById(R.id.btnLogin);//로그인 버튼
        final Button btnJoin = findViewById(R.id.btnJoin);//회원가입 버튼
        // Button btnDB = findViewById(R.id.btnDB);


        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent JoinIntent = new Intent(LoginActivity.this, JoinActivity.class);
                LoginActivity.this.startActivity(JoinIntent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sId = id.getText().toString();
                String sPw = pw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("로그인되셨습니다! 환영합니다!")
                                        .setPositiveButton("확인",null)
                                        .create()
                                        .show();
                                String ID = jsonResponse.getString("ID");
                                String PASSWORD = jsonResponse.getString("PASSWORD");
                                String NAME = jsonResponse.getString("NAME");
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                intent.putExtra("ID", ID);
                                intent.putExtra("PASSWORD", PASSWORD);
                                intent.putExtra("NAME", NAME);//메인 화면에 사용자의 아이디, 비밀번호, 이름을 넘겨주기 위한 코드임
                                LoginActivity.this.startActivity(intent);

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("로그인에 실패")
                                        .setNegativeButton("다시시도",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginDatabase loginDatabase = new LoginDatabase(sId, sPw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginDatabase);
            }
        });
    }
}
