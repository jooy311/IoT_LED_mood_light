package com.example.eunaecho.gproject;

import android.app.DatePickerDialog;
import android.app.LauncherActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Eunae Cho on 2018-07-30.
 */

public class DiaryActivity extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    CalendarView calendarView;

    TextView dateDiary;
    EditText editText;
    Button btnSave;
    Button btnCancel;

    //날짜
    Calendar cal;
    int year;
    int month;
    int date;

    //DatePicker로 고른 날짜
    int set_Year;
    int set_Month;
    int set_Date;


    //텍스트 파일 저장
    final static String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DiaryFile";

    //php와 연동
    String dataUrl;
    phpDown task;

    ArrayList<ListItem> listItem= new ArrayList<ListItem>();
    ListItem Item;


      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        //(1)
        // datePicker = (DatePicker) findViewById(R.id.datePicker);
        dateDiary = (TextView) findViewById(R.id.dateDiary);
        editText = (EditText) findViewById(R.id.realDiary);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        calendarView = (CalendarView) findViewById(R.id.calendarView);

          //날짜 받아오기
        cal = new GregorianCalendar();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        date = cal.get(Calendar.DATE);

        //PHP와 연동
          dataUrl = "http://jooy311.cafe24.com/appimg/";

        //시작하자마자 날짜 선택할 수 있도록
        Dialog_DatePicker();

        //취소 버튼
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent DiaryIntent = new Intent(DiaryActivity.this, MainActivity.class);
                DiaryActivity.this.startActivity(DiaryIntent);
            }
        });

        //저장 버튼 누르면 텍스트 파일로 저장
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String now = dateDiary.getText().toString();
                String contents = editText.getText() + "\n";

                WriteTextFile(foldername, now + ".txt", contents);
                Toast.makeText(getApplicationContext(), "파일 저장", Toast.LENGTH_SHORT).show();

                task = new phpDown();
                task.execute("http://jooy311.cafe24.com/_diary.php");

            }
        });

        //날짜 클릭하면 날짜 바꿀 수 있도록
        dateDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_DatePicker();
            }
        });

          //클릭하면 배경색
          editText.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  editText.setBackgroundColor(Color.argb(50, 100,0,200));
              }
          });
    }

    private void Dialog_DatePicker() {

        datePickerDialog = new DatePickerDialog(DiaryActivity.this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                set_Date = datePickerDialog.getDatePicker().getDayOfMonth();
                set_Month = datePickerDialog.getDatePicker().getMonth() + 1;       //월을 0월부터 센다는 소문이,,
                set_Year = datePickerDialog.getDatePicker().getYear();

                dateDiary.setText(set_Year + "년 " + set_Month + "월 "+ set_Date + "일");
            }
        }, year, month - 1, date);
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        datePickerDialog.show();
    }

    //텍스트내용을 경로의 텍스트 파일에 쓰기
    public void WriteTextFile(String foldername, String filename, String contents){
        try{
            File dir = new File(foldername);

            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }

            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    //PHP 연동하기 위한

    private class phpDown extends AsyncTask<String, Integer,String>{

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try{
                      // 연결 url 설정
                      URL url = new URL(urls[0]);
                      // 커넥션 객체 생성
                      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                      // 연결되었으면.
                      if(conn != null){
                       //   Toast.makeText(getApplicationContext(), "연결 됨 ㅎㅎ", Toast.LENGTH_SHORT).show();
                         conn.setConnectTimeout(10000);
                         conn.setUseCaches(false);

                         // 연결되었음 코드가 리턴되면.
                         if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            for(;;){
                                // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                                String line = br.readLine();
                                if(line == null) break;
                                // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                                jsonHtml.append(line + "\n");
                             }
                          br.close();
                       }
                        conn.disconnect();
                     }
                   } catch(Exception ex){
                      ex.printStackTrace();
                   }
                   return jsonHtml.toString();

        }

        protected void onPostExecute(String str){
            String dataUrl;
            String txt1;
            String txt2;
            try{

                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for(int i=0; i<ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);
                    dataUrl = jo.getString("dataUrl");
                    txt1 = jo.getString("txt1");
                    txt2 = jo.getString("txt2");
                    listItem.add(new ListItem(dataUrl,txt1,txt2));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            editText.setText("dataUrl :"+listItem.get(0).getData(0)+"\ntxt1:"+ listItem.get(0).getData(1)+"\ntxt2:"+listItem.get(0).getData(2));
        }

    }

}




