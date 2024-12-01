package com.example.focuson;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private SeekBar seekBarImportance, seekBarDesire, seekBarObligation;
    private TextView textViewImportance, textViewDesire, textViewObligation, textViewDeadline;
    private Button buttonSelectDate, buttonSave;

    private Calendar selectedDate;
    private int taskId = -1; // 기본값: 새로운 데이터
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // 뒤로가기 버튼 처리
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 수정 완료 후 MainActivity에 결과 전달
                Intent resultIntent = new Intent();
                resultIntent.putExtra("isUpdated", true); // 수정 여부 전달
                setResult(RESULT_OK, resultIntent);

                // 액티비티 종료
                finish();
            }
        });

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // 뷰 초기화
        editTextTitle = findViewById(R.id.editTextTitle);
        seekBarImportance = findViewById(R.id.seekBarImportance);
        seekBarDesire = findViewById(R.id.seekBarDesire);
        seekBarObligation = findViewById(R.id.seekBarObligation);
        textViewImportance = findViewById(R.id.textViewImportance);
        textViewDesire = findViewById(R.id.textViewDesire);
        textViewObligation = findViewById(R.id.textViewObligation);
        textViewDeadline = findViewById(R.id.textViewDeadline);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        buttonSave = findViewById(R.id.buttonSave);

        // 전달받은 Task 데이터 불러오기
        Intent intent = getIntent();
        taskId = intent.getIntExtra("taskId", -1);
        String title = intent.getStringExtra("taskTitle");
        int importance = intent.getIntExtra("importanceValue", 1);
        int desire = intent.getIntExtra("desireValue", 1);
        int obligation = intent.getIntExtra("obligationValue", 1);
        String deadline = intent.getStringExtra("deadline");

        // 데이터 표시
        editTextTitle.setText(title);
        seekBarImportance.setProgress(importance - 1);
        seekBarDesire.setProgress(desire - 1);
        seekBarObligation.setProgress(obligation - 1);
        textViewImportance.setText("Importance: " + importance);
        textViewDesire.setText("Desire: " + desire);
        textViewObligation.setText("Obligation: " + obligation);
        textViewDeadline.setText("Deadline: " + deadline);

        // 날짜 선택 버튼 리스너
        buttonSelectDate.setOnClickListener(v -> {
            Calendar currentDate = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.getTime());
                        textViewDeadline.setText("Deadline: " + formattedDate);
                    },
                    currentDate.get(Calendar.YEAR),
                    currentDate.get(Calendar.MONTH),
                    currentDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // SeekBar 리스너 설정
        seekBarImportance.setOnSeekBarChangeListener(createSeekBarListener(textViewImportance, "Importance"));
        seekBarDesire.setOnSeekBarChangeListener(createSeekBarListener(textViewDesire, "Desire"));
        seekBarObligation.setOnSeekBarChangeListener(createSeekBarListener(textViewObligation, "Obligation"));

        // 저장 버튼 리스너
        buttonSave.setOnClickListener(v -> {
            String newTitle = editTextTitle.getText().toString().trim();
            if (newTitle.isEmpty()) {
                Toast.makeText(this, "Please enter a title.", Toast.LENGTH_SHORT).show();
                return;
            }

            int newImportance = seekBarImportance.getProgress() + 1;
            int newDesire = seekBarDesire.getProgress() + 1;
            int newObligation = seekBarObligation.getProgress() + 1;
            String newDeadline = textViewDeadline.getText().toString().replace("Deadline: ", "").trim();

            Task updatedTask = new Task(
                    taskId,
                    newTitle,
                    newImportance,
                    newObligation,
                    newDeadline,
                    newDesire,
                    false,
                    null,
                    0
            );

            if (taskId == -1) {
                dbHelper.insertTask(updatedTask);
                Toast.makeText(this, "Task added successfully.", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.updateTask(updatedTask);
                Toast.makeText(this, "Task updated successfully.", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }

    private SeekBar.OnSeekBarChangeListener createSeekBarListener(TextView textView, String label) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(label + ": " + (progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };
    }

    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("isUpdated", true); // 수정 여부 전달
        setResult(RESULT_OK, resultIntent); // 결과 설정
        super.finish(); // 액티비티 종료
    }

}
