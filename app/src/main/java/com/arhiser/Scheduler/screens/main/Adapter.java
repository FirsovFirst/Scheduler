package com.arhiser.Scheduler.screens.main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.arhiser.Scheduler.R;
import com.arhiser.Scheduler.model.Note;
import com.arhiser.Scheduler.screens.NoteActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.NoteViewHolder> {

    private SortedList<Note> sortedlist;

    public Adapter() {

        sortedlist = new SortedList<>(Note.class, new SortedList.Callback<Note>() {

            @Override
            public int compare(Note o1, Note o2) {
                return Long.compare(o1.time, o2.time);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Note oldItem, Note newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Note item1, Note item2) {
                return item1.uid == item2.uid;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.items_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(sortedlist.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedlist.size();
    }

    public void setItems(List<Note> notes) {
        sortedlist.replaceAll(notes);
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView note_text;
        TextView text_time;

        Note note;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            note_text = itemView.findViewById(R.id.note_text);
            text_time = itemView.findViewById(R.id.text_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NoteActivity.start((Activity) itemView.getContext(), note);
                }
            });
        }

        public void bind(Note note) {
            this.note = note;

            note_text.setText(note.text);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yyyy", Locale.getDefault());
            text_time.setText(sdf.format(note.time));
        }
    }
}
