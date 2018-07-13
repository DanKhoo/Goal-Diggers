package com.example.danielk.goalchamp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EditActivity extends AppCompatActivity {

    private EditText mEditTextTitle, mEditTextMessage;
    private TextView mSetDate, mSetTime;
    private String goalId;
    private Button dateShow, timeShow;
    private int mDate, mMonth, mYear, mHour, mMinute;
    private FirebaseAuth auth;
    private Switch mAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Initialize Widgets
        mEditTextTitle = findViewById(R.id.editTextTitle);
        mEditTextMessage = findViewById(R.id.editTextMessage);
        mSetDate = findViewById(R.id.tvSetDate);
        mSetTime = findViewById(R.id.tvSetTime);
        dateShow = findViewById(R.id.setDateBtn);
        timeShow = findViewById(R.id.setTimeBtn);
        mAlert = findViewById(R.id.switchAlarm);
        auth = FirebaseAuth.getInstance();

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

        // Get Goal from bundle
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            Goal goal = b.getParcelable("goal");
            mEditTextTitle.setText(goal.getTitle());
            mEditTextMessage.setText(goal.getMessage());
            mSetDate.setText(goal.getDate());
            mSetTime.setText(goal.getTime());
            if (mSetDate.getText().equals("Not Set")) {
                mAlert.setChecked(false);
            } else {
                mAlert.setChecked(true);
                String[] dateParts = goal.getDate().split("/");
                mDate = Integer.parseInt(dateParts[0]);
                mMonth = Integer.parseInt(dateParts[1]) - 1;
                mYear = Integer.parseInt(dateParts[2]);
                String timeParts[] = goal.getTime().substring(0,5).split(":");
                mHour = Integer.parseInt(timeParts[0]);
                mMinute = Integer.parseInt(timeParts[1].trim());
            }
            goalId = goal.getId();
        } else {
            new NullPointerException();
        }

        // Open Date Dialog Picker
        dateShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                mDate = calendar.get(Calendar.DAY_OF_MONTH);
                mMonth = calendar.get(Calendar.MONTH);
                mYear = calendar.get(Calendar.YEAR);
                DatePickerDialog pickerDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month+=1;
                        mSetDate.setText(dayOfMonth+"/"+month+"/"+year);
                        mDate = dayOfMonth;
                        mMonth = month -1;
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
                TimePickerDialog pickerDialog = new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String am_pm;
                        mHour = 0;
                        if (hourOfDay < 12) {
                            am_pm = "AM";
                        } else if (hourOfDay==12) {
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
                        mSetTime.setText(time);
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

            case R.id.deleteTask:
                showDeleteConfirmationDialog();
                return true;

            case R.id.saveTask:
                updateGoal();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Display Add Button at action bar
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder((this));
        builder.setMessage("Delete this Goal?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteGoal();
                backToMainActivity();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteGoal() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("goals").child(auth.getUid());
        mDatabase.child(goalId).removeValue();
        cancelAlarm(goalId.hashCode());
        Toast.makeText(EditActivity.this, "Goal deleted", Toast.LENGTH_SHORT).show();
    }

    private void updateGoal() {
        Calendar cal = Calendar.getInstance();
        // check if Date / Time is valid if the Alert status is on
        if (mAlert.isChecked()) {
            Calendar current = Calendar.getInstance();
            cal.set(mYear, mMonth, mDate, mHour, mMinute, 00);
            if (cal.compareTo(current) <= 0) {
                Toast.makeText(EditActivity.this, "Invalid Date/Time", Toast.LENGTH_LONG).show();
                return;
            }
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("goals").child(auth.getUid());
        Goal goal = new Goal();
        goal.setTitle(mEditTextTitle.getText().toString());
        goal.setMessage(mEditTextMessage.getText().toString());
        goal.setDate(mSetDate.getText().toString());
        goal.setTime(mSetTime.getText().toString());
        goal.setId(goalId);
        int uGoalId = goalId.hashCode();
        mDatabase.child(goalId).setValue(goal);
        if (!mAlert.isChecked()) {
            cancelAlarm(uGoalId);
        } else {
            setAlarm(cal, mEditTextTitle.getText().toString(), mEditTextMessage.getText().toString(), uGoalId);
        }
        Toast.makeText(EditActivity.this, "Goal updated", Toast.LENGTH_SHORT).show();
        backToMainActivity();
    }

    private void backToMainActivity() {
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void cancelAlarm(int uGoalId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), uGoalId, intent, 0);
        alarmManager.cancel(pendingIntent);

    }

    private void setAlarm (Calendar targetCal, String title, String message, int uNoteId) {
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("TITLE", title);
        intent.putExtra("MESSAGE", message);
        intent.putExtra("ID", uNoteId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,uNoteId, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
    }
}
