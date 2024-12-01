package com.example.focuson;

import android.content.Context;
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

    public TaskAdapter(Context context, ArrayList<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.dbHelper = new DBHelper(context); // DBHelper 초기화
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
        // 왜 모두 1로 출력 되는 거지?
        Log.d("task.getImportance()", "task.getImportance() returned: " + task.getImportance());
        holder.taskPriorityTextView.setText("Priority: " + task.getImportance());
        holder.taskCheckBox.setChecked(task.isChecked());
        // checked라면 취소선을 긋고 흐릿하게 만든다.
//        Log.d("ischekd", "onBindViewHolder() returned: " + taskList.get(position).isChecked());
        if(taskList.get(position).isChecked()){
            // 완료시 흐릿하게 만듦.
            holder.itemView.setAlpha(0.5f);
            // 전체 itemView에 취소선을 긋는다.
            holder.itemView.setBackground(new StrikethroughDrawable());
        }
        // 체크박스 리스너
        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setChecked(isChecked);
            if (isChecked) {
                // 완료 시간 설정
                // 완료시 흐릿하게 만듦.
                holder.itemView.setAlpha(0.5f);
                // 전체 itemView에 취소선을 긋는다.
                holder.itemView.setBackground(new StrikethroughDrawable());
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());

                task.setWhenChecked(formattedDate);
            } else {
                // 미완료 상태
                holder.itemView.setAlpha(1f);
                holder.itemView.setBackground(null);
                task.setWhenChecked(null);
            }
            // DB 업데이트
            dbHelper.updateTask(task);
        });

        // 옵션 버튼
        holder.optionsButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.optionsButton);
            popupMenu.inflate(R.menu.task_options_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit_task) {
                    // 수정 로직 개발 필요.
                    Toast.makeText(context, "할 일 수정: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.delete_task) {
                    dbHelper.deleteTask(task.getId()); // DB에서 삭제
                    taskList.remove(position); // 로컬 리스트에서 삭제
                    Toast.makeText(context, "할 일 삭제됨", Toast.LENGTH_SHORT).show();
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
