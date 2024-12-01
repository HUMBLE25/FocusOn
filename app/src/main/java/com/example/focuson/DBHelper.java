package com.example.focuson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mytodolist.db";
    private static final int DATABASE_VERSION = 3; // VIEW 추가로 버전 증가
    private static final String TABLE_TASK = "Task";
    private static final String VIEW_TASK_PRIORITY = "TaskPriorityView";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_TASK + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title CHAR(20), " +
                "importance INTEGER, " +
                "obligation INTEGER, " +
                "deadline TEXT, " + // SQLite에서 날짜를 문자열로 저장
                "desire INTEGER, " +
                "ischecked INTEGER, " +
                "whenchecked TEXT);"; // 완료 시간도 문자열로 저장
        db.execSQL(createTableQuery);

        createPriorityView(db); // VIEW 생성
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("DROP VIEW IF EXISTS " + VIEW_TASK_PRIORITY); // 기존 VIEW 삭제
            createPriorityView(db); // 새로운 VIEW 생성
        }
    }

    private void createPriorityView(SQLiteDatabase db) {
        String createViewQuery = "CREATE VIEW " + VIEW_TASK_PRIORITY + " AS " +
                "SELECT " +
                "id, " +
                "title, " +
                "deadline, " +
                "ischecked, " +
                "whenchecked, " +
                "CASE " +
                "WHEN deadline = date('now') THEN 1 " +
                "ELSE 0 " +
                "END AS is_today, " +
                "(importance * 0.8 + desire * 0.5 + obligation * 0.9) AS weighted_priority " +
                "FROM " + TABLE_TASK + " " +
                "WHERE deadline BETWEEN date('now') AND date('now', '+1 day') " +
                "ORDER BY " +
                "is_today DESC, " +
                "weighted_priority DESC, " +
                "ischecked ASC, " +
                "whenchecked ASC;";
        db.execSQL(createViewQuery);
    }

    // Insert a new task
    public void insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        values.put("importance", task.getImportance());
        values.put("obligation", task.getObligation());
        values.put("deadline", task.getDeadline());
        values.put("desire", task.getDesire());
        values.put("ischecked", task.isChecked() ? 1 : 0);
        values.put("whenchecked", task.isChecked() ? task.getWhenChecked() : null);

        db.insert(TABLE_TASK, null, values);
        db.close();
    }

    // Update an existing task
    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        values.put("importance", task.getImportance());
        values.put("obligation", task.getObligation());
        values.put("deadline", task.getDeadline());
        values.put("desire", task.getDesire());
        values.put("ischecked", task.isChecked() ? 1 : 0);
        values.put("whenchecked", task.isChecked() ? task.getWhenChecked() : null);

        db.update(TABLE_TASK, values, "id=?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    // Delete a task
    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASK, "id=?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    // Get tasks with priority from the view
    public ArrayList<Task> getTasksWithPriority() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Task> taskList = new ArrayList<>();

        String query = "SELECT id, title, deadline, is_today, weighted_priority FROM " + VIEW_TASK_PRIORITY;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(0),  // id
                        cursor.getString(1),  // title
                        0,  // importance (생략)
                        0,  // obligation (생략)
                        cursor.getString(2),  // deadline
                        0,  // desire (생략)
                        false,  // ischecked (생략)
                        null  // whenchecked (생략)
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }
}
