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

    private Context context;
    private ArrayList<Task> taskList;
    private DBHelper dbHelper;
    private TaskFragment taskFragment; // TaskFragment 인스턴스
    private String forMattedDate; // 바뀐 날짜는 저장하는 변수
    public TaskAdapter(Context context, ArrayList<Task> taskList,  TaskFragment taskFragment,String forMattedDate) {
        this.context = context;
        this.taskList = taskList;
        this.dbHelper = new DBHelper(context); // DBHelper 초기화
        this.taskFragment = taskFragment;
        this.forMattedDate = forMattedDate;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.taskTitleTextView.setText(task.getTitle());
        holder.taskPriorityTextView.setText("Priority: " + task.getPriority()); // 우선순위  설정
        holder.taskCheckBox.setChecked(task.isChecked());

//        checked라면 취소선을 긋고 흐릿하게 만든다.
        if(taskList.get(position).isChecked()){
            holder.itemView.setAlpha(0.5f); // 완료시 흐릿하게 만듦.
            holder.itemView.setBackground(new StrikethroughDrawable()); // 전체 itemView에 취소선을 긋는다.
        }

        // 체크박스 리스너
        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setChecked(isChecked);
            // UI 및 DB 갱신
            if (isChecked) {
                holder.itemView.setAlpha(0.5f);  // 완료시 흐릿하게 만듦.
                holder.itemView.setBackground(new StrikethroughDrawable()); // 전체 itemView에 취소선을 긋는다.
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
                task.setWhenChecked(formattedDate);
            } else {
                holder.itemView.setAlpha(1f); // 미완료 상태
                holder.itemView.setBackground(null);
                task.setWhenChecked(null);
            }
            dbHelper.updateTask(task); // DB 업데이트
            // 선택된 날짜로 할일 목록 갱신
            taskFragment.loadTasksByDate(forMattedDate);
        });

        // 옵션 버튼
        holder.optionsButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.optionsButton);
            popupMenu.inflate(R.menu.task_options_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit_task) {
                    // EditTaskActivity로 이동
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
                    dbHelper.deleteTask(task.getId()); // DB에서 삭제
                    taskList.remove(position); // 로컬 리스트에서 삭제
                    Toast.makeText(context, "할 일 삭제됨", Toast.LENGTH_SHORT).show();
                    // 선택된 날짜로 할일 목록 갱신
                    taskFragment.loadTasksByDate(forMattedDate);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitleTextView;
        TextView taskPriorityTextView;
//        TextView taskDeadlineTextView; // 추가된 deadline 표시
        CheckBox taskCheckBox;
        ImageView optionsButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitleTextView = itemView.findViewById(R.id.taskTitleTextView);
            taskPriorityTextView = itemView.findViewById(R.id.taskPriorityTextView);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            optionsButton = itemView.findViewById(R.id.optionsButton);
        }
    }

    // Drawable로 취소선을 그린다.
    public class StrikethroughDrawable extends Drawable {
        private final Paint paint = new Paint();
        // 취소선을 그린다.
        // 밝기를 낮추어 흐릿하게 보이게 한다.
        public StrikethroughDrawable() {
            paint.setColor(Color.BLACK); // 취소선 색상
            paint.setStrokeWidth(10);   // 취소선 굵기
            paint.setStyle(Paint.Style.STROKE);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            Rect bounds = getBounds();
            float centerY = bounds.exactCenterY(); // 뷰의 중앙 Y값

            // 전체 너비의 80%만 사용하여 취소선을 그림
            float left = bounds.left + (bounds.width() * 0.05f);  // 좌측 10% 여백
            float right = bounds.right - (bounds.width() * 0.05f); // 우측 10% 여백

            canvas.drawLine(left, centerY, right, centerY, paint); // 취소선 그리기
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }

    }
}
