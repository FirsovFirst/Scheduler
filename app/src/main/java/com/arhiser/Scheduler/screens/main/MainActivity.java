package com.arhiser.Scheduler.screens.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arhiser.Scheduler.R;
import com.arhiser.Scheduler.model.Note;
import com.arhiser.Scheduler.screens.NoteActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Adapter adapter = new Adapter();
        recyclerView.setAdapter(adapter);

        Button noteAdd = findViewById(R.id.noteAdd);
        noteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteActivity.start(MainActivity.this, null);
            }
        });

        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getNoteLiveDate().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.setItems(notes);
            }
        });
    }
}