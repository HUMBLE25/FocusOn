package com.example.focuson;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * TaskFragment
 * - 사용자가 할 일을 관리하는 화면을 구성하는 프래그먼트입니다.
 * - MainActivity에서 관리되며, RecyclerView를 사용하여 할 일 목록을 표시합니다.
 * - 사용자가 날짜를 선택하거나 새로운 할 일을 추가하면 화면이 갱신됩니다.
 */
public class TaskFragment extends Fragment {
    // 변수
    private ArrayList<Task> taskList;  // 현재 화면에 표시될 할 일 목록
    private TaskAdapter taskAdapter;  // RecyclerView에 데이터를 바인딩하는 어댑터
    private DBHelper dbHelper;        // SQLite 데이터베이스와의 연동을 담당
    private RecyclerView taskRecyclerView; // 할 일 목록을 표시하는 RecyclerView
    protected Button addTaskButton;  // 새로운 할 일을 추가하는 버튼
    private Calendar selectedDate;   // 사용자가 선택한 날짜를 저장
    private String forMattedDate;    // 사용자가 선택한 날짜를 문자열로 변환하여 저장

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * onCreateView
     * - XML 레이아웃 파일(fragment_task.xml)을 inflate하여 View를 생성합니다.
     *
     * @param inflater           XML 레이아웃 파일을 inflate하는 도구
     * @param container          프래그먼트가 포함될 부모 뷰 그룹
     * @param savedInstanceState 프래그먼트 상태 복원을 위한 번들
     * @return 생성된 View를 반환
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // fragment_graph.xml을 inflate하여 View 생성
        return inflater.inflate(R.layout.fragment_task, container, false);
    }


    /**
     * onViewCreated
     * - View 생성 후 호출됩니다.
     * - 변수 초기화와 데이터 로딩을 수행합니다.
     *
     * @param view               생성된 View
     * @param savedInstanceState 프래그먼트 상태 복원을 위한 번들
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 1. 오늘 날짜를 기본 값으로 설정
        selectedDate = Calendar.getInstance();

        // 2. 데이터베이스 헬퍼 초기화
        dbHelper = new DBHelper(requireContext());

        // 3. RecyclerView 초기화
        taskRecyclerView = view.findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 4. 날짜를 포맷하여 화면에 표시
        forMattedDate = getFormattedDate(selectedDate); // 날짜의 포멧을 바꿈
        loadTasksByDate(forMattedDate);

        // 5. 외부에서 전달된 날짜 데이터가 있으면 해당 날짜의 데이터를 불러옴
        Bundle arguments = getArguments();
        if (arguments != null) {
            forMattedDate = arguments.getString("selectedDate");
            loadTasksByDate(forMattedDate);
        }

        // 6. 새로운 할 일 추가 버튼 초기화 및 클릭 이벤트 설정
        addTaskButton = view.findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(new AddTaskClickListener());
    }

    /**
     * loadTasksByDate
     * - 주어진 날짜의 할 일 목록을 데이터베이스에서 불러옵니다.
     *
     * @param date 데이터베이스 쿼리에 사용할 날짜 (yyyy-MM-dd 형식)
     */
    public void loadTasksByDate(String date) {
        // 1. 데이터베이스에서 주어진 날짜의 할 일 목록 가져오기
        taskList = dbHelper.getTasksByDate(date);
        if (taskList == null) {
            taskList = new ArrayList<>();
        }

        // 2. RecyclerView 어댑터 설정 및 갱신
        taskAdapter = new TaskAdapter(requireContext(), taskList, this, date);
        taskRecyclerView.setAdapter(taskAdapter);
    }

    /**
     * getFormattedDate
     * - Calendar 객체를 yyyy-MM-dd 형식의 문자열로 변환합니다.
     *
     * @param calendar 변환할 날짜
     * @return 포맷된 날짜 문자열
     */
    private String getFormattedDate(Calendar calendar) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }

    /**
     * AddTaskClickListener
     * - 새로운 할 일을 추가하는 버튼의 클릭 이벤트 리스너
     * - AddTaskTitleActivity로 전환하여 사용자 입력을 받습니다.
     */
    private class AddTaskClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(requireContext(), AddTaskTitleActivity.class);
            startActivity(intent);
        }
    }

}