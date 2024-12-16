package com.example.focuson;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AddTaskImportanceActivity extends AppCompatActivity {

    private int importanceValue = 1; // 기본 중요도 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_importance);

        TextView textViewImportance = findViewById(R.id.textViewImportance);
        SeekBar seekBarImportance = findViewById(R.id.seekBarImportance);
        Button buttonNext = findViewById(R.id.buttonNextToUrgency);

        // SeekBar 변화 리스너 설정
        seekBarImportance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                importanceValue = progress + 1; // 중요도는 1~10
                textViewImportance.setText("중요도: " + importanceValue);
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
                // 다음 화면으로 이동 (마감일 설정 화면)
                Intent intent = new Intent(AddTaskImportanceActivity.this, AddTaskDeadlineActivity.class);

                // 이전 화면에서 전달된 제목과 중요도 데이터를 함께 전달
                // 이전 화면에서 전달된 데이터 수집
                String taskTitle = getIntent().getStringExtra("taskTitle");
                // 다음 화면에 데이터 전달
                intent.putExtra("taskTitle", taskTitle);
                intent.putExtra("importanceValue", importanceValue);

                startActivity(intent);
            }
        });
    }
}
