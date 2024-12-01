package com.example.focuson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_EDIT = 101;
    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;
    private DBHelper dbHelper;
    private RecyclerView taskRecyclerView;

    protected Button addTaskButton;
    public ActivityResultLauncher<Intent> editTaskLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // RecyclerView 초기화
        taskRecyclerView = findViewById(R.id.taskRecyclerView);

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // addTaskButton 초기화
        addTaskButton = (Button) findViewById(R.id.addTaskButton);

        // DB에서 할 일 목록 불러오기
        loadTasksFromDB();

        // TaskAdapter 설정, 할일 목록 출력.
        setTaskAdapter();

        // 'Add Task' 버튼 클릭 시 AddTaskTitleActivity로 이동
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTaskTitleActivity.class);
                startActivity(intent);
            }
        });

        editTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isUpdated = result.getData().getBooleanExtra("isUpdated", false);
                        if (isUpdated) {
                            loadTasksFromDB(); // DB에서 할 일 목록 불러오기
                            setTaskAdapter(); // TaskAdapter 설정, 할일 목록 출력.

                        }
                    }
                }
        );

        // 새로 추가된 할 일이 있는 경우 목록에 추가
        if (getIntent().hasExtra("newTaskTitle")) {

            String taskTitle = getIntent().getStringExtra("newTaskTitle");
            int importance = getIntent().getIntExtra("importanceValue", 1);
            int desire = getIntent().getIntExtra("desireValue", 1);
            int obligation = getIntent().getIntExtra("obligationValue", 1);
            Date deadlineDate = (Date) getIntent().getSerializableExtra("deadline");

            // Date -> String 변환
            String deadline = null;
            if (deadlineDate != null) {
                deadline = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(deadlineDate);
            }

            Task newTask = new Task(0, taskTitle, importance, obligation, deadline, desire, false, null,0);
            dbHelper.insertTask(newTask); // DB에 새로운 Task 저장

            loadTasksFromDB(); // DB에서 다시 할 일 목록 불러오기

            setTaskAdapter(); // UI 업데이트
            Toast.makeText(this, "할 일이 추가되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 수정 결과를 받아 처리
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        // null이 나오고 있다. intent를 받아오지 못한다.
//        Log.i("isgetIntent", "onActivityResult: " +data );
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
//            boolean isUpdated = data.getBooleanExtra("isUpdated", false);
//            if (isUpdated) {
//                // 데이터를 다시 불러오고 RecyclerView를 갱신
//                loadTasksFromDB();
//                setTaskAdapter();
//            }
//        }
//    }
    // ActivityResultLauncher 초기화

    private void loadTasksFromDB() {
        // DB에서 모든 할 일 불러오기
        taskList = dbHelper.getTasksWithPriority();
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
    }

    private void setTaskAdapter (){
        taskAdapter = new TaskAdapter(this, taskList);
        taskRecyclerView.setAdapter(taskAdapter);
    }
}
