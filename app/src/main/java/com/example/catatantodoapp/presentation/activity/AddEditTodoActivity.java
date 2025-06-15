package com.example.catatantodoapp.presentation.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.catatantodoapp.R;
import com.example.catatantodoapp.data.model.Todo;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditTodoActivity extends AppCompatActivity {
    public static final String EXTRA_TODO = "extra_todo";
    private EditText etTitle;
    private CheckBox cbCompleted;
    private Button btnSetReminder;
    private TextView tvReminderTime;
    private Todo todo;
    private long reminderTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_todo);

        etTitle = findViewById(R.id.et_todo_title);
        cbCompleted = findViewById(R.id.cb_todo_completed);
        btnSetReminder = findViewById(R.id.btn_set_reminder);
        tvReminderTime = findViewById(R.id.tv_reminder_time);
        Button btnSave = findViewById(R.id.btn_save_todo);

        // Check if editing an existing todo
        todo = (Todo) getIntent().getSerializableExtra(EXTRA_TODO);
        if (todo != null) {
            etTitle.setText(todo.getTitle());
            cbCompleted.setChecked(todo.isCompleted());
            reminderTime = todo.getReminderTime();
            updateReminderTimeText();
            setTitle("Edit Todo");
        } else {
            todo = new Todo();
            setTitle("Add Todo");
        }

        btnSetReminder.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        reminderTime = calendar.getTimeInMillis();
                        updateReminderTimeText();
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );
            timePickerDialog.show();
        });

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Please fill the title", Toast.LENGTH_SHORT).show();
                return;
            }

            todo.setTitle(title);
            todo.setCompleted(cbCompleted.isChecked());
            todo.setTimestamp(System.currentTimeMillis());
            todo.setReminderTime(reminderTime);

            Intent result = new Intent();
            result.putExtra(EXTRA_TODO, todo);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    private void updateReminderTimeText() {
        if (reminderTime > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvReminderTime.setText(sdf.format(reminderTime));
        } else {
            tvReminderTime.setText("No reminder set");
        }
    }
}