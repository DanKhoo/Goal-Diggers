package com.example.danielk.goalchamp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    private EditText mEditTextTitle, mEditTextMessage;
    private FirebaseAuth auth;
    private Button dateShow, timeShow;
    private int mDate, mMonth, mYear, mHour, mMinute;
    private TextView mSetDate, mSetTime;
    private Switch mAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Initialize Widgets
        mEditTextTitle = findViewById(R.id.editTextTitle);
        mEditTextMessage = findViewById(R.id.editTextMessage);
        dateShow = findViewById(R.id.setDateBtn);
        timeShow = findViewById(R.id.setTimeBtn);
        dateShow.setEnabled(false);
        timeShow.setEnabled(false);
        mSetTime = findViewById(R.id.tvSetTime);
        mSetDate = findViewById(R.id.tvSetDate);
        mAlert = findViewById(R.id.switchAlarm);

        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mAlert.isChecked()) {
                    dateShow.setEnabled(true);
                    timeShow.setEnabled(true);
                } else {
                    dateShow.setEnabled(false);
                    timeShow.setEnabled(false);
                    mSetDate.setText("Not Set");
                    mSetTime.setText("Not Set");
                }
            }
        });

        // Open Date Dialog Picker
        dateShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                mDate = calendar.get(Calendar.DAY_OF_MONTH);
                mMonth = calendar.get(Calendar.MONTH);
                mYear = calendar.get(Calendar.YEAR);
                DatePickerDialog pickerDialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        mSetDate.setText(dayOfMonth + "/" + month + "/" + year);
                        mDate = dayOfMonth;
                        mMonth = month - 1;
                        mYear = year;
                    }
                }, mYear, mMonth, mDate);
                pickerDialog.show();
            }
        });

        // Open Time Dialog Picker
        timeShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                mHour = calendar.get(Calendar.HOUR_OF_DAY);
                mMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog pickerDialog = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String am_pm;
                        mHour = 0;
                        if (hourOfDay < 12) {
                            am_pm = "AM";
                        } else if (hourOfDay == 12) {
                            am_pm = "PM";
                        } else {
                            hourOfDay -= 12;
                            am_pm = "PM";
                            mHour = 12;
                        }

                        String minutes;
                        if (minute < 10) {
                            minutes = "0" + minute;
                        } else {
                            minutes = String.valueOf(minute);
                        }
                        String time = new StringBuilder().append(hourOfDay).append(':')
                                .append(minutes).append(" ").append(am_pm).toString();
                        mHour += hourOfDay;
                        mMinute = minute;
                        mSetTime.setText(time);
                    }
                }, mHour, mMinute, false);
                pickerDialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.saveTask:
                addNewGoal();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Display Add Button at action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add, menu);
        return true;
    }

    private void backToMainActivity() {
        Intent intent = new Intent(AddActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addNewGoal() {
        auth = FirebaseAuth.getInstance();
        Calendar cal = Calendar.getInstance();

        String title = mEditTextTitle.getText().toString().trim();
        String message = mEditTextMessage.getText().toString().trim();
        String date = mSetDate.getText().toString().trim();
        String time = mSetTime.getText().toString().trim();

        // check if email is empty
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(AddActivity.this, "Enter Title", Toast.LENGTH_SHORT).show();
            return;
        }

        // check if Date / Time is valid if the Alert status is on
        if (mAlert.isChecked()) {
            Calendar current = Calendar.getInstance();
            cal.set(mYear, mMonth, mDate, mHour, mMinute, 00);
            if (cal.compareTo(current) <= 0) {
                Toast.makeText(AddActivity.this, "Invalid Date/Time", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // write a message to database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("goals").child(auth.getUid());
        // goal object to store title and message
        String noteId = mDatabase.push().getKey();
        // goal object to store title and message
        Goal newGoal = new Goal();
        newGoal.setTitle(title);
        newGoal.setMessage(message);
        newGoal.setId(noteId);
        newGoal.setDate(date);
        newGoal.setTime(time);
        mDatabase.child(noteId).setValue(newGoal);
        int uNoteId = noteId.hashCode();
        if (mAlert.isChecked()) {
            setAlarm(cal, title, message, uNoteId);
        }
        Toast.makeText(AddActivity.this, "Goal saved", Toast.LENGTH_SHORT).show();
        backToMainActivity();
    }

    private void setAlarm (Calendar targetCal, String title, String message, int uNoteId) {
        Log.d("AlarmSet", String.valueOf(targetCal.getTimeInMillis()));
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("TITLE", title);
        intent.putExtra("MESSAGE", message);
        intent.putExtra("ID", uNoteId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,uNoteId, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
    }
}
