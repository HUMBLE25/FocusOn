package com.example.focuson;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddTaskTitleActivity extends AppCompatActivity {

    private EditText editTextTaskTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_title);

        editTextTaskTitle = findViewById(R.id.editTextTaskTitle);
        Button buttonNext = findViewById(R.id.buttonNextToImportance);

        // 'Next' 버튼 클릭 리스너 설정
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = editTextTaskTitle.getText().toString().trim();

                if (taskTitle.isEmpty()) {
                    // 제목이 비어 있는 경우 경고 메시지 표시
                    Toast.makeText(AddTaskTitleActivity.this, "Please enter a task title", Toast.LENGTH_SHORT).show();
                } else {
                    // 제목이 입력된 경우 다음 화면으로 이동
                    Intent intent = new Intent(AddTaskTitleActivity.this, AddTaskImportanceActivity.class);
                    intent.putExtra("taskTitle", taskTitle); // 제목 데이터를 다음 액티비티로 전달
                    startActivity(intent);
                }
            }
        });
    }
}
