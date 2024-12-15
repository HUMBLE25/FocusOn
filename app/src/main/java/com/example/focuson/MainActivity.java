package com.example.focuson;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_EDIT = 101;
    public ActivityResultLauncher<Intent> editTaskLauncher;
    private Calendar selectedDate; // 날짜 선택을 위한 변수
    private String forMattedDate; // 바뀐 날짜는 저장하는 변수

    private TaskFragment taskFragment; // TaskFragment 인스턴스
    private GraphFragment graphFragment; // GraphFragment 인스턴스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기 날짜는 오늘로 설정
        selectedDate = Calendar.getInstance();

        forMattedDate = getFormattedDate(selectedDate);
        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 프래그먼트 초기화
        taskFragment = new TaskFragment();
        graphFragment = new GraphFragment();

        // TaskFragment를 초기 화면으로 설정
        Bundle bundle = new Bundle();
        bundle.putString("selectedDate", forMattedDate);
        taskFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, taskFragment)
                .commit();

        // 하단네비게이션바 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        handleNewTaskIntent(); // Intent로 새 할 일 데이터 처리

        // TaskFragment에서 필요한 매서드
        // "수정" 후 결과 처리
        editTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isUpdated = result.getData().getBooleanExtra("isUpdated", false);
                        if (isUpdated) {
                            // 모든 데이터를 불러 오는 문제가 있음 해당 날짜의 데이터만 불러오면 될 것이다.
                            taskFragment.loadTasksByDate(forMattedDate);// DB에서 할 일 목록 불러오기
                        }
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        updateToolbarDate(menu);
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

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        if (item.getItemId() == R.id.menu_tasks) {
            setFragment(taskFragment);
        } else if (item.getItemId() == R.id.menu_graph) {
            setFragment(graphFragment);
        }
        return true;
    };

    private void updateToolbarDate(Menu menu) {
        MenuItem todayDateItem = menu.findItem(R.id.menu_today_date);
        if (todayDateItem != null) {
            View actionView = todayDateItem.getActionView();
            if (actionView != null) {
                TextView todayDateText = actionView.findViewById(R.id.custom_today_date);
                todayDateText.setText(forMattedDate);
            }
        }
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

                    forMattedDate = getFormattedDate(selectedDate);
                    updateFragmentData();
                    Toast.makeText(this, "선택된 날짜: " + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.getTime()), Toast.LENGTH_SHORT).show();
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }
    private void updateFragmentData() {
        if (taskFragment.isVisible()) {
            taskFragment.loadTasksByDate(forMattedDate);
        } else if (graphFragment.isVisible()) {
            graphFragment.updateProgress(forMattedDate);
        }
    }

    private void handleNewTaskIntent() {
        // 앱이 종료되지는 앟는다.
        // 하지만 화면이 갱신되지 않는다.
        // DB에 들어가지만 날짜가 들어가지 않는 문제가 생기고 있다.
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("newTaskTitle")) {
            String taskTitle = intent.getStringExtra("newTaskTitle");
            int importance = intent.getIntExtra("importanceValue", 1);
            int desire = intent.getIntExtra("desireValue", 1);
            int obligation = intent.getIntExtra("obligationValue", 1);
            Date deadlineDate = (Date) getIntent().getSerializableExtra("deadline");

            // Date -> String 변환
            String deadline = null;
            if (deadlineDate != null) {
                deadline = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(deadlineDate);
            }

            Task newTask = new Task(0, taskTitle, importance, obligation, deadline, desire, false, null, 0);
            new DBHelper(this).insertTask(newTask);

            if (taskFragment.isVisible()) {
                taskFragment.loadTasksByDate(forMattedDate);
            }
            Toast.makeText(this, "할 일이 추가되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    // 날짜 포맷 메서드
    private String getFormattedDate(Calendar calendar) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }
}
