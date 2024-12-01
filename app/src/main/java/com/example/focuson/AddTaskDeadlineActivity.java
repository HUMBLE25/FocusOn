package com.example.focuson;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class AddTaskDeadlineActivity extends AppCompatActivity {

    private Calendar dueDate;
    private boolean isDateSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_deadline);

        TextView textViewUrgency = findViewById(R.id.textViewUrgency);
        Button buttonSelectDate = findViewById(R.id.buttonSelectDate);
        Button buttonNext = findViewById(R.id.buttonNextToDesire);

        // 날짜 선택 버튼 클릭 리스너
        buttonSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentDate = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddTaskDeadlineActivity.this,
                        (view, year, month, dayOfMonth) -> {
                            dueDate = Calendar.getInstance();
                            dueDate.set(year, month, dayOfMonth);
                            textViewUrgency.setText("Due Date: " + dayOfMonth + "/" + (month + 1) + "/" + year);
                            isDateSelected = true;
                        },
                        currentDate.get(Calendar.YEAR),
                        currentDate.get(Calendar.MONTH),
                        currentDate.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });

        // 'Next' 버튼 클릭 리스너 설정
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDateSelected) {
                    // 날짜가 선택되지 않은 경우 경고 메시지 표시
                    Toast.makeText(AddTaskDeadlineActivity.this, "Please select a due date", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 이전 화면에서 전달된 데이터 수집
                Intent intent = new Intent(AddTaskDeadlineActivity.this, AddTaskDesireActivity.class);
                String taskTitle = getIntent().getStringExtra("taskTitle");
                int importanceValue = getIntent().getIntExtra("importanceValue", 1);
                Date deadlineDate = new Date(dueDate.getTimeInMillis());

                // 다음 화면에 데이터 전달.
                intent.putExtra("taskTitle", taskTitle);
                intent.putExtra("importanceValue", importanceValue);
                intent.putExtra("deadline", deadlineDate);

                startActivity(intent);
            }
        });
    }
}
