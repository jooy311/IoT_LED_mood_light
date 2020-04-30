package com.example.eunaecho.gproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Eunae Cho on 2018-10-21.
 */

//캘린더 일정 추가하기 팝업 클래스
public class PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //팝업액티비티의 제목을 제거한다.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams();
        layoutParams.flags= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount= 0.5f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.add_schedule);

        final EditText et_name = (EditText) findViewById(R.id.et_name);
        final EditText et_sub = (EditText) findViewById(R.id.et_sub);

        //확인버튼 이벤트
        Button button_ok = (Button) findViewById(R.id.btnAdd);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //데이터 전달하고 액티비티 닫기
                String name = et_name.getText().toString();
                String sub = et_sub.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("name", name);
                intent.putExtra("sub", sub);
                setResult(RESULT_OK, intent);
                Toast.makeText(getApplicationContext() , name, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        //취소 버튼 이벤트
        Button button_cc = (Button) findViewById(R.id.btnNoAdd);
        button_cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //액티비티 닫기
                finish();
            }
        });
    }


    //바깥영역 클릭 방지와 백 버튼 차단
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        return;
//    }
}