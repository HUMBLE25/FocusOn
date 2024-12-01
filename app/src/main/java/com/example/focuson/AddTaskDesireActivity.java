package com.example.focuson;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class AddTaskDesireActivity extends AppCompatActivity {

    private int desireValue = 1; // 기본 바람 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_desire);

        TextView textViewDesire = findViewById(R.id.textViewDesire);
        SeekBar seekBarDesire = findViewById(R.id.seekBarDesire);
        Button buttonNext = findViewById(R.id.buttonNextToObligation);

        // SeekBar 변화 리스너 설정
        seekBarDesire.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                desireValue = progress + 1; // 바람 점수는 1~10 사이
                textViewDesire.setText("Desire: " + desireValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // 'Next' 버튼 클릭 리스너 설정
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다음 화면으로 이동 (의무도 설정 화면)
                Intent intent = new Intent(AddTaskDesireActivity.this, AddTaskObligationActivity.class);

                // 이전 화면에서 전달된 데이터 수집
                String taskTitle = getIntent().getStringExtra("taskTitle");
                int importanceValue = getIntent().getIntExtra("importanceValue", 1);
                Date deadlineDate = (Date) getIntent().getSerializableExtra("deadline");

                // 데이터 전달
                intent.putExtra("taskTitle", taskTitle);
                intent.putExtra("importanceValue", importanceValue);
                intent.putExtra("deadline", deadlineDate);
                intent.putExtra("desireValue", desireValue);

                startActivity(intent);
            }
        });
    }
}
