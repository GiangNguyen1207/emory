package com.example.emory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EntriesActivity extends AppCompatActivity {
    private TextView month;
    private DayMonthYear monthYear;
    private BottomNavigationView bottomNavigationView;
    private ArrayList<DiaryList> diaryList = new ArrayList<>();
    private ListView listView;
    private static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries);

        month = findViewById(R.id.month);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.entries);

        monthYear = new DayMonthYear();
        String currentMonthYear = monthYear.getCurrentMonthYear();
        month.setText(currentMonthYear);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.entries:
                    return true;

                case R.id.addMood:
                    startActivity(new Intent(this, AddMoodActivity.class));
                    return true;

                case R.id.toDoList:
                    startActivity(new Intent(this, TodoDetailsActivity.class));
                    return true;

                case R.id.settings:
                    startActivity(new Intent(this, SettingsActivity.class));
                    return true;
            }
            return false;
        });

        createListView(false);
    }

    public void onBack(View v) {
        month = findViewById(R.id.month);
        String prevMonthYear = monthYear.getPrevMonthYear(month.getText().toString());
        month.setText(prevMonthYear);
        removeExistingListView();
        createListView(true);
    }

    public void onNext(View v) {
        month = findViewById(R.id.month);
        String nextMonthYear = monthYear.getNextMonthYear(month.getText().toString());
        month.setText(nextMonthYear);
        removeExistingListView();
        createListView(true);
    }

    public void removeExistingListView() {
        DiaryListAdapter diaryListAdapter = (DiaryListAdapter) listView.getAdapter();
        diaryListAdapter.clearData();
    }

    public void createListView(Boolean movement) {
        Gson gson = new Gson();
        ArrayList<Diary> diaries;
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        String date = month.getText().toString();
        Integer daysOfMonths = monthYear.getDaysInMonth(date);

        for (int i = 1; i <= daysOfMonths; i++) {
            String data = sharedPreferences.getString(i + ". " + date, String.valueOf(new ArrayList<Diary>()));
            Type diaryType = new TypeToken<ArrayList<Diary>>() {
            }.getType();
            diaries = gson.fromJson(data, diaryType);
            if (diaries.size() > 0) {
                diaryList.add(new DiaryList(i + ". " + date, diaries));
            }
        }

        listView = findViewById(R.id.listView);
        listView.setAdapter(new DiaryListAdapter(this, diaryList));
    }

}