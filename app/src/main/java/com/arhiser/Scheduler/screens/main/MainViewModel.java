package com.arhiser.Scheduler.screens.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.arhiser.Scheduler.App;
import com.arhiser.Scheduler.model.Note;

import java.util.List;

public class MainViewModel extends ViewModel {
    private LiveData<List<Note>> noteLiveDate = App.getInstance().getNoteDao().getAllLiveData();

    public LiveData<List<Note>> getNoteLiveDate() {
        return noteLiveDate;
    }
}
