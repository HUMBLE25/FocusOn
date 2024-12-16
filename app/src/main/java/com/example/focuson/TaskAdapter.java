package com.example.focuson;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context; // Activity 또는 Fragment의 Context
    private ArrayList<Task> taskList; // 할 일 목록을 저장하는 리스트
    private DBHelper dbHelper; // 데이터베이스 작업을 위한 헬퍼 클래스
    private TaskFragment taskFragment; // TaskFragment 인스턴스
    private String forMattedDate; // 선택된 날짜 문자열
    /**
     * TaskAdapter 생성자
     * - TaskAdapter를 초기화하는 메서드.
     * - UI와 데이터를 연결하고 데이터베이스 헬퍼를 초기화한다.
     * @param context 현재 Activity나 Fragment의 Context
     * @param taskList 할 일 목록 데이터
     * @param taskFragment TaskFragment 인스턴스
     * @param forMattedDate 선택된 날짜 문자열
     */
    public TaskAdapter(Context context, ArrayList<Task> taskList, TaskFragment taskFragment, String forMattedDate) {
        this.context = context;
        this.taskList = taskList;
        this.dbHelper = new DBHelper(context); // DBHelper 초기화
        this.taskFragment = taskFragment;
        // 현재 날짜를 받아오지 선택한 날짜를 받아오지 않는다.
        this.forMattedDate = forMattedDate;
    }


    /**
     * onCreateViewHolder
     * - 각 아이템의 뷰를 생성하고 ViewHolder를 반환한다.
     * @param parent 부모 ViewGroup
     * @param viewType 뷰 타입
     * @return TaskViewHolder 인스턴스
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    /**
     * onBindViewHolder
     * - 각 아이템의 데이터와 UI 요소를 연결하는 메서드.
     * - 이벤트 리스너를 설정한다.
     * @param holder TaskViewHolder 인스턴스
     * @param position 현재 아이템의 위치
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // 아이템의 기본 데이터 설정
        holder.taskTitleTextView.setText(task.getTitle());
        holder.taskPriorityTextView.setText("우선순위: " + task.getPriority()); // 우선순위  설정
        holder.taskCheckBox.setChecked(task.isChecked());

        // 완료 상태에 따라 텍스트 스타일 설정
        if (task.isChecked()) {
            holder.taskTitleTextView.setPaintFlags(holder.taskTitleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // 취소선 추가
            holder.itemView.setAlpha(0.5f); // 컨테이너 전체를 흐릿하게 설정
        } else {
            holder.taskTitleTextView.setPaintFlags(holder.taskTitleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // 취소선 제거
            holder.itemView.setAlpha(1f); // 컨테이너 전체를 뚜렷하게 설정
        }


        // 체크박스 리스너 설정
        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setChecked(isChecked);
            // UI 및 DB 갱신
            if (isChecked) {
                holder.taskTitleTextView.setPaintFlags(holder.taskTitleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // 취소선 추가
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
                task.setWhenChecked(formattedDate);
            } else {
                holder.taskTitleTextView.setPaintFlags(holder.taskTitleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // 취소선 제거
                task.setWhenChecked(null);
            }
            dbHelper.updateTask(task); // DB 업데이트
            taskFragment.loadTasksByDate(forMattedDate); // TaskFragment 데이터 갱신
        });
// 옵션 버튼 클릭 이벤트 설정
        holder.optionsButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.optionsButton);
            popupMenu.inflate(R.menu.task_options_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit_task) {
                    // EditTaskActivity 실행
                    Intent editIntent = new Intent(context, EditTaskActivity.class);
                    editIntent.putExtra("taskId", task.getId());
                    editIntent.putExtra("taskTitle", task.getTitle());
                    editIntent.putExtra("importanceValue", task.getImportance());
                    editIntent.putExtra("desireValue", task.getDesire());
                    editIntent.putExtra("obligationValue", task.getObligation());
                    editIntent.putExtra("deadline", task.getDeadline());

                    // startActivityForResult 대신 editTaskLauncher 사용
                    ((MainActivity) context).editTaskLauncher.launch(editIntent);
                    return true;
                } else if (item.getItemId() == R.id.delete_task) {
                    dbHelper.deleteTask(task.getId()); // 데이터베이스에서 삭제
                    taskList.remove(position); // 로컬 리스트에서 삭제
                    Toast.makeText(context, "할 일 삭제됨", Toast.LENGTH_SHORT).show();
                    taskFragment.loadTasksByDate(forMattedDate); // TaskFragment 데이터 갱신
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });

    }

    /**
     * getItemCount
     * - 어댑터가 처리할 아이템의 개수를 반환하는 메서드.
     * @return 아이템 개수
     */
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * TaskViewHolder
     * - RecyclerView 아이템의 뷰를 관리하는 클래스.
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitleTextView; // 할 일 제목
        TextView taskPriorityTextView; // 할 일 우선순위
        CheckBox taskCheckBox; // 할 일 완료 여부 체크박스
        ImageView optionsButton; // 옵션 메뉴 버튼

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitleTextView = itemView.findViewById(R.id.taskTitleTextView);
            taskPriorityTextView = itemView.findViewById(R.id.taskPriorityTextView);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            optionsButton = itemView.findViewById(R.id.optionsButton);
        }
    }
}
