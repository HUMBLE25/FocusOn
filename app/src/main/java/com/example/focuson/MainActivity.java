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
    // ActivityResultLauncher: Activity에서 결과를 처리하기 위한 런처
    public ActivityResultLauncher<Intent> editTaskLauncher;

    // 날짜와 관련된 변수들
    private Calendar selectedDate; // 현재 선택된 날짜를 저장하기 위한 변수
    private String forMattedDate;  // 포맷팅된 날짜 문자열

    // Fragment 인스턴스
    private TaskFragment taskFragment; // 할 일 목록을 표시하는 TaskFragment
    private GraphFragment graphFragment; // 성취도를 표시하는 GraphFragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 현재 날짜로 초기화
        selectedDate = Calendar.getInstance();
        forMattedDate = getFormattedDate(selectedDate); // 포맷팅된 날짜 문자열로 저장

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fragment 초기화
        taskFragment = new TaskFragment();
        graphFragment = new GraphFragment();

        // 초기 화면으로 TaskFragment 설정
        setFragment(taskFragment);

        // 하단 네비게이션 초기화 및 클릭 리스너 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // 수정 결과 처리를 위한 런처 초기화
        initializeEditTaskLauncher();

        // 새로 추가된 Task 데이터를 처리
        handleNewTaskIntent();
    }

    /**
     * initializeEditTaskLauncher
     * - "수정" 후 결과 처리를 위한 런처 초기화 메서드.
     * - TaskFragment가 수정된 Task 데이터를 갱신하도록 한다.
     */
    private void initializeEditTaskLauncher() {
        editTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isUpdated = result.getData().getBooleanExtra("isUpdated", false);
                        if (isUpdated) {
                            taskFragment.loadTasksByDate(forMattedDate); // 날짜에 따른 할 일 갱신
                        }
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Toolbar 메뉴를 초기화하고 날짜를 업데이트한다.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        updateToolbarDate(menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Toolbar 메뉴 아이템 클릭 이벤트 처리
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

    /**
     * updateToolbarDate
     * - Toolbar의 날짜를 업데이트하는 메서드.
     * - 현재 선택된 날짜를 툴바에 표시한다.
     * @param menu Toolbar의 메뉴 객체
     */
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
    /**
     * showDatePicker
     * - 날짜 선택 다이얼로그를 표시하는 메서드.
     * - 선택된 날짜를 저장하고 프래그먼트 데이터를 갱신한다.
     */
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
    /**
     * setFragment
     * - 특정 프래그먼트로 화면을 전환하는 메서드.
     * @param fragment 전환할 프래그먼트
     */
    private void setFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("selectedDate", forMattedDate); // 현재 날짜 전달
        fragment.setArguments(bundle); // 선택된 Fragment에 인자 전달
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }
    /**
     * updateFragmentData
     * - 현재 표시 중인 프래그먼트의 데이터를 갱신하는 메서드.
     * - TaskFragment 또는 GraphFragment의 데이터를 갱신한다.
     */
    private void updateFragmentData() {
        if (taskFragment.isVisible()) {
            taskFragment.loadTasksByDate(forMattedDate);
        } else if (graphFragment.isVisible()) {
            graphFragment.updateProgress(forMattedDate);
        }
    }

    /**
     * handleNewTaskIntent
     * - Intent로 전달받은 새 Task 데이터를 처리하는 메서드.
     * - Task 데이터를 DB에 저장하고 TaskFragment를 갱신한다.
     */
    private void handleNewTaskIntent() {
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
    /**
     * getFormattedDate
     * - 날짜를 포맷팅하여 문자열로 반환하는 메서드.
     * @param calendar 포맷팅할 날짜를 가진 Calendar 객체
     */
    private String getFormattedDate(Calendar calendar) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }
}
