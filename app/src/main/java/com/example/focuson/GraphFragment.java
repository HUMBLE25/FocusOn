package com.example.focuson;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class GraphFragment extends Fragment {

    private ProgressBar progressBar; // 원형 ProgressBar
    private TextView textViewProgress; // 성취도 퍼센트 텍스트
    private TextView textViewDescription; // 성취도 설명 텍스트
    private DBHelper dbHelper; // 데이터베이스 헬퍼

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_graph.xml을 inflate하여 View 생성
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View 초기화
        progressBar = view.findViewById(R.id.progressBar);
        textViewProgress = view.findViewById(R.id.textViewProgress);
        textViewDescription = view.findViewById(R.id.textViewDescription);

        // DBHelper 초기화
        dbHelper = new DBHelper(requireContext());

        // 선택된 날짜 데이터 가져오기
        Bundle arguments = getArguments();
        if (arguments != null) {
            String selectedDate = arguments.getString("selectedDate");

            // 할 일 완료 비율 계산 및 UI 업데이트
            updateProgress(selectedDate);
        }
    }

    public void updateProgress(String date) {
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
