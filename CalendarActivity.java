package com.example.eunaecho.gproject;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    ListView myDate;
    Context context = this;
    Button btnGoToAdd;
    ArrayList<String> arrayList;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        myDate = (ListView) findViewById(R.id.myDate);
        btnGoToAdd = (Button) findViewById(R.id.btnGoToAdd);

        //데이터 저장
        arrayList = new ArrayList<String>();


        //리스트 뷰-어댑터 객체를 생성
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        myDate.setAdapter(adapter);

        //날짜가 변경될 때 이벤트를 받기위한 리스너
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                String date = (month + 1) + "/" + dayOfMonth + "/" + year;

                myDate.setVisibility(View.VISIBLE);

                //일정 읽어오기 ************!!!!!

            }
        });

        btnGoToAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //팝업창을 띄워서 데이터를 입력받도록 하자.
                Intent intent = new Intent(CalendarActivity.this, PopupActivity.class);
                //intent.putExtra("data", "test data");
                startActivityForResult(intent, 1);
                return ;
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            //데이터 처리
            if(resultCode == RESULT_OK) {
                String newName = data.getStringExtra("name");
                String newSubject = data.getStringExtra("sub");

                arrayList.add(newName);
                adapter.notifyDataSetChanged();
            }
        }
    }

}