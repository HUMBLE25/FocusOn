package com.example.focuson;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;
    private DBHelper dbHelper;
    private RecyclerView taskRecyclerView;

    protected Button addTaskButton;
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

        // 스크롤이 가능하도록 해야 한다.
        // 'Add Task' 버튼 클릭 시 AddTaskTitleActivity로 이동

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTaskTitleActivity.class);
                startActivity(intent);
            }
        });

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

            Task newTask = new Task(0, taskTitle, importance, obligation, deadline, desire, false, null);
            dbHelper.insertTask(newTask); // DB에 새로운 Task 저장

            loadTasksFromDB(); // DB에서 다시 할 일 목록 불러오기

            setTaskAdapter(); // UI 업데이트
            Toast.makeText(this, "할 일이 추가되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTasksFromDB() {
        // DB에서 모든 할 일 불러오기
        taskList = dbHelper.getTasksWithPriority();
//        Log.d("taskList", "loadTasksFromDB() returned: " + taskList);
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
    }

    private void setTaskAdapter (){
        taskAdapter = new TaskAdapter(this, taskList);
//        Log.d("taskAdapter", "setTaskAdapter() returned: " +taskAdapter );
        taskRecyclerView.setAdapter(taskAdapter);
    }

}
