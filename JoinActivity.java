package com.example.eunaecho.gproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class JoinActivity extends AppCompatActivity {

    //Layout 값
    EditText id;
    EditText pw;
    EditText pw2;
    EditText name;
    EditText birth;
    EditText phone;

    //확인용 변수
    Boolean id_ok = false;
    Boolean pw_ok = false;
    Boolean photo_ok = false;
    TextView txt_id_ok;

    ImageView imgView;
    ImageView img;

    private boolean idChecked = false;
    private static int PICK_IMAGE_REQUEST = 1; //프로필 이미지 설정하는 변수



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //버튼들
        final Button btnPre = findViewById(R.id.btnPre);
        final Button btnJoin = findViewById(R.id.btnSet);
        final Button btnGallery = findViewById(R.id.btnGallery);

        //회원정보 입력하는 공간들
        final EditText id = (EditText) findViewById(R.id.text_id);
        final EditText pw = (EditText) findViewById(R.id.text_pw);
        final EditText pw2 = (EditText) findViewById(R.id.text_pw2);
        final EditText name = (EditText) findViewById(R.id.text_name);
        final EditText birth = (EditText) findViewById(R.id.text_birth);
        final EditText phone = (EditText) findViewById(R.id.text_phone);

        //회원 확인 가능하도록
        img = findViewById(R.id.Img);

        //이전 버튼 누르면 로그인 화면으로
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PreIntent = new Intent(JoinActivity.this, LoginActivity.class);
                JoinActivity.this.startActivity(PreIntent);

                //값이 적혀진게 있으면 확인 창 띄우기
            }
        });


        /* 프로필 사진 설정하기 */
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Camera Permission", "CALL");
                //Intent 생성
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_PIC과 차이점?
                intent.setType("image/*"); //이미지만 보이게
                //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

                //값이 적혀진게 있으면 확인 창 띄우기
            }
        });

        /*중복체크 검사*/
        final Button btnidcheck = findViewById(R.id.btn_idcheck); //중복체크버튼
        btnidcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sId = id.getText().toString();
                if(idChecked)
                {
                    return;
                }
                if(sId.equals(""))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setMessage("아이디는 빈칸일 수 없습니다.")
                            .setPositiveButton("확인",null)
                            .create()
                            .show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                builder.setMessage("사용할 수 있는 아이디 입니다.")
                                        .setPositiveButton("확인",null)
                                        .create()
                                        .show();
                                id.setEnabled(false);
                                idChecked = true;
                                id.setTextColor(getResources().getColor(R.color.color_15));
                                btnidcheck.setEnabled(false);
                            }else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                builder.setMessage("이미 존재하는 아이디 입니다.")
                                        .setNegativeButton("확인",null)
                                        .create()
                                        .show();
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                //중복체크DB넣는 자리입니다.
                IdcheckDatabase idcheckDatabase = new IdcheckDatabase(sId, responseListener);
                RequestQueue queue = Volley.newRequestQueue(JoinActivity.this);
                queue.add(idcheckDatabase);
            }
        });

         /* 회원가입 완료 하기 */
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sId = id.getText().toString();
                String sPw = pw.getText().toString();
                String sPw2 = pw2.getText().toString();
                String sName = name.getText().toString();
                String sBirth = birth.getText().toString();
                String sPhone = phone.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");//안됐던 이유가 php파일 내용 쓸떼없는거 덧붙여서 그랬던거였음....ㅅㅂ
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                builder.setMessage("회원 등록에 성공했습니다.")
                                        .setPositiveButton("확인",null)
                                        .create()
                                        .show();
                                // if(builder.set)
                                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                                //builder.dismiss();
                                JoinActivity.this.startActivity(intent);
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                builder.setMessage("회원 등록에 싪패했습니다.")
                                        .setNegativeButton("다시시도",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                if(id.getText().length() == 0){
                    Toast.makeText(getApplicationContext(), "아이디를 입력해 주세요!", Toast.LENGTH_SHORT).show();
                    id.requestFocus();
                }else if(!idChecked){
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setMessage("아이디 중복체크를 해주세요.")
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                    return;
                }
                else if (pw.length() < 4 || pw.length() > 8) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setMessage("비밀번호는 4자리 이상 8자리 이하로 해주세요.")
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                    pw.requestFocus();
                    return;
                } else if(pw2.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setMessage("비밀번호 확인을 해주세요.")
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                    pw2.requestFocus();
                    return;
                } else if (!sPw2.equals(sPw)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setMessage("비밀번호가 일치하지 않습니다!")
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                    pw2.requestFocus();
                    return;
                } else if (name.getText().length() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setMessage("이름을 입력해 주세요.")
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                    name.requestFocus();
                    return;
                }else if (birth.getText().length() < 6 || birth.getText().length() > 6) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setMessage("생년월일은 ex)950311 형식으로 입력해 주세요")
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                    birth.requestFocus();
                    return;
                }else if (phone.getText().length() < 11) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setMessage("핸드폰번호를 제대로 입력해 주세요!")
                            .setNegativeButton("확인",null)
                            .create()
                            .show();
                    phone.requestFocus();
                    return;
                }else{ //내용이 다 적혀야만 가입될 수 있도록 함
                    Database db = new Database(sId, sPw, sName, sBirth, sPhone, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(JoinActivity.this);
                    queue.add(db);
                }
            }
        });



    }

    public void setIdChecked(boolean idChecked) {
        this.idChecked = idChecked;
    }

    //이미지를 byte로 변환
    public byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    //이미지를 불러오기
    public Bitmap getAppIcon(byte[] b){
        Bitmap bitmap = BitmapFactory.decodeByteArray(b,0,b.length);
        return bitmap;
    }

    private Bitmap resize(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }


    //사용자가 선택한 이미지의 uri를 얻어서 이미지뷰에 이미지를 띄워주는 코드
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //이미지를 하나 골랐을때
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
                //data에서 절대경로로 이미지를 가져옴
                Uri uri = data.getData();
                Bitmap user_image = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Toast.makeText(this, "사진이 선택 되었습니다..", Toast.LENGTH_LONG).show();


                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                int nh = (int) (user_image.getHeight() * (1024 / user_image.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(user_image, 1024, nh, true);
                //Bitmap bm = BitmapFactory.decodeStream() ;

                imgView = (ImageView) findViewById(R.id.imageView);
                imgView.setImageBitmap(scaled);

                bitmapToByteArray(user_image);
                Toast.makeText(getApplicationContext(), "byte[]로 변환됨", Toast.LENGTH_SHORT).show();

                //photo ->db에 uri를  String 형태로 저장되게한다...
                if (uri.getScheme().toString().compareTo("content")==0)
                {
                    Cursor cursor =getContentResolver().query(uri, null, null, null, null);
                    if (cursor.moveToFirst())
                    {
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        //Instead of "MediaStore.Images.Media.DATA" can be used "_data"

                        Uri filePathUri = Uri.parse(cursor.getString(column_index));
                        String file_name = filePathUri.getLastPathSegment().toString();
                        String file_path=filePathUri.getPath();
                        Toast.makeText(this,"File Name & PATH are:"+file_name+"\n"+file_path, Toast.LENGTH_LONG).show();
                    }
                }

            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }




}