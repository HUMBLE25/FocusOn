package com.example.focuson;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private Calendar selectedDate; // 날짜 선택을 위한 변수
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

        // 초기 날짜는 오늘로 설정
        selectedDate = Calendar.getInstance();

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadTasksByDate(getFormattedDate(selectedDate)); // 오늘 날짜의 할 일 불러오기
//        loadTasksFromDB(); // DB에서 할 일 목록 불러오기
//        setTaskAdapter(); // TaskAdapter 설정, 할일 목록 출력.

        // 'Add Task' 버튼 클릭 시 AddTaskTitleActivity로 이동
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTaskTitleActivity.class);
                startActivity(intent);
            }
        });

        // "수정" 후 결과 처리
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // 오늘 날짜 표시
        MenuItem todayDateItem = menu.findItem(R.id.menu_today_date);
        String todayDate = new SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.getDefault())
                .format(selectedDate.getTime());
        todayDateItem.setTitle(todayDate);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_calendar) {
            // 달력 아이콘 클릭 시 날짜 선택 다이얼로그 표시
            showDatePicker();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        Calendar currentDate = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // 선택된 날짜를 저장
                    selectedDate.set(year, month, dayOfMonth);

                    // 상단 메뉴 날짜 갱신
                    invalidateOptionsMenu();

                    // TODO: 선택된 날짜에 해당하는 할 일을 불러오는 로직 추가
                    String formattedDate = getFormattedDate(selectedDate);
                    loadTasksByDate(formattedDate);
                    Toast.makeText(this, "선택된 날짜: " + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.getTime()), Toast.LENGTH_SHORT).show();
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // 날짜 포맷 메서드
    private String getFormattedDate(Calendar calendar) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }

    // 선택된 날짜의 데이터를 불러오는 메서드
    private void loadTasksByDate(String date) {
        Log.i("date", "(date): "+ date); // 오늘 날은 잘 나온다.
        taskList = dbHelper.getTasksByDate(date); // DBHelper에서 데이터 불러오기
        Log.i("dbHelper.getTasksByDate(date)", "dbHelper.getTasksByDate(date)"+taskList); // 데이터를 못불러오고 있다.
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        setTaskAdapter(); // RecyclerView 갱신
    }

    public void loadTasksFromDB() {
        // DB에서 모든 할 일 불러오기
        taskList = dbHelper.getTasksWithPriority();
//        taskList = dbHelper.getTasksByDate(getFormattedDate(selectedDate));         // 선택된 날짜로부터 할 일을 불러온다.
        if (taskList == null) {

            taskList = new ArrayList<>();
        }
    }

    public void setTaskAdapter (){
        taskAdapter = new TaskAdapter(this, taskList);
        taskRecyclerView.setAdapter(taskAdapter);
    }
}
