package com.arhiser.Scheduler.screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.arhiser.Scheduler.AlertReceiver;
import com.arhiser.Scheduler.App;
import com.arhiser.Scheduler.R;
import com.arhiser.Scheduler.model.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class NoteActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "NoteDetailsActivity.EXTRA_NOTE";

    Note note;

    private EditText editText;

    Button setTime;

    View delete;

    TextView time_check;

    Calendar calendar = Calendar.getInstance();

    public static void start(Activity caller, Note note) {
        Intent intent = new Intent(caller, NoteActivity.class);
        if (note != null) {
            intent.putExtra(EXTRA_NOTE, note);
        }
        caller.startActivity(intent);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(getString(R.string.note_name));

        editText = findViewById(R.id.names);

        time_check = findViewById(R.id.time_check);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yyyy", Locale.getDefault());

        if (getIntent().hasExtra(EXTRA_NOTE)){
            note = getIntent().getParcelableExtra(EXTRA_NOTE);
            editText.setText(note.text);
            time_check.setText(sdf.format(note.time));
        } else {
            note = new Note();
            time_check.setText(sdf.format(calendar.getTimeInMillis()));
        }

        setTime = findViewById(R.id.alarm_button);

        setTime.setOnClickListener(v -> {

            View dialogView1 = View.inflate(this, R.layout.date_picker, null);
            AlertDialog alertDialog1 = new AlertDialog.Builder(this).create();

            DatePicker datePicker = (DatePicker) dialogView1.findViewById(R.id.date_picker);

            dialogView1.findViewById(R.id.date_set).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calendar.set(Calendar.YEAR, datePicker.getYear());
                    calendar.set(Calendar.MONTH, datePicker.getMonth());
                    calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                    time_check.setText(sdf.format(calendar.getTimeInMillis()));
                    alertDialog1.dismiss();

                    View dialogView2 = View.inflate(NoteActivity.this, R.layout.time_picker, null);
                    AlertDialog alertDialog2 = new AlertDialog.Builder(NoteActivity.this).create();

                    TimePicker timePicker = (TimePicker) dialogView2.findViewById(R.id.time_picker);
                    timePicker.setIs24HourView(true);
                    timePicker.setHour(12);
                    timePicker.setMinute(0);

                    dialogView2.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                            calendar.set(Calendar.MINUTE, timePicker.getMinute());
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            time_check.setText(sdf.format(calendar.getTimeInMillis()));
                            alertDialog2.dismiss();
                        }
                    });
                    alertDialog2.setView(dialogView2);
                    alertDialog2.show();
                }
            });
            alertDialog1.setView(dialogView1);
            alertDialog1.show();
        });

        delete = findViewById(R.id.delete_note);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().hasExtra(EXTRA_NOTE)) {
                    cancel();
                    App.getInstance().getNoteDao().delete(note);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                if (editText.getText().length() > 0) {

                    note.text = editText.getText().toString();

                    if (getIntent().hasExtra(EXTRA_NOTE)) {
                        cancel();
                    }

                    note.time = calendar.getTimeInMillis();

                    if (getIntent().hasExtra(EXTRA_NOTE)) {
                        App.getInstance().getNoteDao().update(note);
                    } else {
                        App.getInstance().getNoteDao().insert(note);
                    }

                    setAlarm();

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yyyy", Locale.getDefault());
                    Toast.makeText(this, "Напоминание установлено на " + sdf.format(note.time), Toast.LENGTH_SHORT).show();

                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private PendingIntent getAlarmActionPendingIntent() {
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("EXTRA_TEXT", note.text);
        return PendingIntent.getBroadcast(this, note.uid, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    public void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, note.time, getAlarmActionPendingIntent());
    }

    public void cancel() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getAlarmActionPendingIntent());
    }
}
