package com.example.focuson;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GraphFragment extends Fragment {

    // ProgressBar: 원형 진행도를 표시하는 UI 요소
    CircularProgressIndicator progressBar;

    // TextView: 성취도 퍼센트를 표시하는 텍스트
    private TextView textViewProgress;

    // TextView: 성취도 설명을 표시하는 텍스트
    private TextView textViewDescription;
    final Handler handler = new Handler();
    // DBHelper: 데이터베이스 작업을 위한 헬퍼 클래스
    private DBHelper dbHelper;

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
        textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewProgress = view.findViewById(R.id.textViewProgress);

        progressBar.setIndeterminate(false);
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

    /**
     * updateProgress
     * - 선택된 날짜에 따라 할 일 완료 비율을 계산하고 UI를 업데이트한다.
     * @param date 성취도를 계산할 날짜 문자열
     */
    // *TODO: 완료률에 따라 색상이 점차 바뀌는 그래디언트 효과 추가
    public void updateProgress(String date) {
        // 해당 날짜의 모든 할 일 가져오기
        ArrayList<Task> tasks = dbHelper.getTasksByDate(date);
        Log.i("tasks", "tasks: "+tasks);
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
        if (progress == 0) {
            // 할 일이 없는 경우
            progressBar.setProgress(0); // ProgressBar 0으로 설정
            textViewProgress.setText("0%"); // 성취도 0% 텍스트 표시
            textViewDescription.setText("No tasks available for the selected date."); // 설명 표시
            return;
        }
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            int load =0;
            @Override
            public void run() {
                load = load +1;
                progressBar.setProgressCompat(load,true);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewProgress.setText(load + "%"); // 퍼센트 표시
                    }
                });
                if(load == progress || progress==0){
                    timer.cancel();
                }
            }
        };
        timer.schedule(task,10,10);
        textViewDescription.setText("You've completed " + completedTasks + " out of " + totalTasks + " tasks."); // 설명 업데이트
    }
}
