package com.example.focuson;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class AddTaskObligationActivity extends AppCompatActivity {

    private int obligationValue = 1; // 기본 의무도 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_obligation);

        TextView textViewObligation = findViewById(R.id.textViewObligation);
        SeekBar seekBarObligation = findViewById(R.id.seekBarObligation);
        Button buttonComplete = findViewById(R.id.buttonComplete);

        // SeekBar 변화 리스너 설정
        seekBarObligation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                obligationValue = progress + 1; // 의무도 점수는 1~10
                textViewObligation.setText("의무도: " + obligationValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // 'Complete' 버튼 클릭 리스너 설정
        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 모든 데이터 수집
                String taskTitle = getIntent().getStringExtra("taskTitle");
                int importanceValue = getIntent().getIntExtra("importanceValue", 1);
                Date deadlineDate = (Date) getIntent().getSerializableExtra("deadline");
                int desireValue = getIntent().getIntExtra("desireValue", 1);

                // 최종 데이터 확인 (테스트 목적으로 Toast 사용)
                String message = "Task: " + taskTitle + "\n" +
                        "Importance: " + importanceValue + "\n" +
                        "deadlineDate: " + deadlineDate + "\n" +
                        "Desire: " + desireValue + "\n" +
                        "Obligation: " + obligationValue;
                // 토스트 메시지 확인 완료. 이제 메인
                Toast.makeText(AddTaskObligationActivity.this, message, Toast.LENGTH_LONG).show();

                // 메인 화면으로 돌아가기
                // 수집한 데이터 전달
                Intent intent = new Intent(AddTaskObligationActivity.this, MainActivity.class);
                intent.putExtra("newTaskTitle", taskTitle);
                intent.putExtra("importanceValue", importanceValue);
                intent.putExtra("deadline", deadlineDate);
                intent.putExtra("desireValue", desireValue);
                intent.putExtra("obligationValue",obligationValue);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
