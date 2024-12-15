package com.example.focuson;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class TaskFragment extends Fragment {

    // TODO: 리사이클러 뷰, 데이터를 불러 올 수 있어야 한다.
    // TODO: + 버튼 클릭시 다음 인텐트로 넘어 갈 수 있도록 한다.
    // TODO: 기존의 MainActivity의 모든 과정을 그대로 수행할 수 있도록 한다.
    // Mainactivity에서 값들을 전달해주는 방식으로 해야할 것이다.
    // dbhelper의 매서드를 활용하는 방법이니 데이터를 가져오는 것은 문제가 없을 것이다.
    // 하지만 어느 시점의 데이터를 불러올지는 어떻게 해야할까?
    // Fragement를 생성하여 MainActivity에 보이게 하는 것이다.
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final int REQUEST_CODE_EDIT = 101;
    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;
    private DBHelper dbHelper;
    private RecyclerView taskRecyclerView;
    protected Button addTaskButton;
    private Calendar selectedDate; // 날짜 선택을 위한 변수
    private String forMattedDate; // 바뀐 날짜는 저장하는 변수
    public ActivityResultLauncher<Intent> editTaskLauncher;
    public TaskFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // fragment_graph.xml을 inflate하여 View 생성
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    // bundle로 날짜를 받아오고 해당 날짜의 데이터를 가져와 출력한다.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 초기 날짜는 오늘로 설정
        selectedDate = Calendar.getInstance();

        dbHelper = new DBHelper(requireContext());

        taskRecyclerView = view.findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        forMattedDate = getFormattedDate(selectedDate); // 날짜의 포멧을 바꿈
        loadTasksByDate(forMattedDate);

        // 선택된 날짜 데이터 가져오기
        Bundle arguments = getArguments();
        if (arguments != null) {
            forMattedDate = arguments.getString("selectedDate");
            // 할일 목록 조회
            loadTasksByDate(forMattedDate);
        }

        addTaskButton = view.findViewById(R.id.addTaskButton); // 버튼 초기화
        addTaskButton.setOnClickListener(new AddTaskClickListener()); // 버튼 리스너 설정
    }


    // 받는 인자 값은 날짜만을 받으며, 리턴값은 없다.
    public void loadTasksByDate(String date) {
        taskList = dbHelper.getTasksByDate(date); // DBHelper에서 데이터 불러오기
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        // TaskAdapter에서 프래그멘트와 날짜를 받으면 되지 않나?
        taskAdapter = new TaskAdapter(requireContext(), taskList,this,forMattedDate);// RecyclerView 갱신

        taskRecyclerView.setAdapter(taskAdapter);
    }
    // 날짜 포멧 설정 매소드
    private String getFormattedDate(Calendar calendar) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }

    // 버튼 클릭시 AddTaskTitleActivity로 전환한다.
    private class AddTaskClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(requireContext(), AddTaskTitleActivity.class);
            startActivity(intent);
        }
    }

}