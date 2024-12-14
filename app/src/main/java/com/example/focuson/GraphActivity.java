package com.example.focuson;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {

    private ProgressBar progressBar; // 원형 ProgressBar
    private TextView textViewProgress; // 성취도 퍼센트 텍스트
    private TextView textViewDescription; // 성취도 설명 텍스트
    private DBHelper dbHelper; // 데이터베이스 헬퍼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // View 초기화
        progressBar = findViewById(R.id.progressBar);
        textViewProgress = findViewById(R.id.textViewProgress);
        textViewDescription = findViewById(R.id.textViewDescription);

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // 선택된 날짜 데이터 가져오기
        String selectedDate = getIntent().getStringExtra("selectedDate");

        // 할 일 완료 비율 계산 및 UI 업데이트
        updateProgress(selectedDate);
    }

    private void updateProgress(String date) {
        // 해당 날짜의 모든 할 일 가져오기
        ArrayList<Task> tasks = dbHelper.getTasksByDate(date);

        if (tasks == null || tasks.isEmpty()) {
            // 할 일이 없는 경우
            progressBar.setProgress(0); // ProgressBar 0으로 설정
            textViewProgress.setText("0%"); // 성취도 0% 텍스트 표시
            textViewDescription.setText("No tasks available for the selected date."); // 설명 표시
            return;
        }

        // 완료된 할 일 계산
        int totalTasks = tasks.size(); // 전체 할 일 수
        int completedTasks = 0; // 완료된 할 일 초기화

        for (Task task : tasks) {
            if (task.isChecked()) {
                completedTasks++; // 완료된 할 일 증가
            }
        }

        // 완료율 계산 (소수점 버림)
        int progress = (int) ((completedTasks / (float) totalTasks) * 100);

        // ProgressBar 및 텍스트 업데이트
        progressBar.setProgress(progress); // ProgressBar 진행도 설정
        textViewProgress.setText(progress + "%"); // 퍼센트 표시
        textViewDescription.setText("You've completed " + completedTasks + " out of " + totalTasks + " tasks."); // 설명 업데이트
    }
}
