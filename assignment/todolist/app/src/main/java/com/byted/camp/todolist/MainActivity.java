package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract.todoEntry;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDbHelper todoDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        todoDbHelper = new TodoDbHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        SQLiteDatabase db = todoDbHelper.getReadableDatabase();
        List<Note> noteList = new ArrayList<Note>();
        Note note;
        // How you want the results sorted in the resul
        String sortOrder = todoEntry.COLUMN_NAME_PRIORITY +" DESC, " +
                todoEntry.COLUMN_NAME_STATE +" DESC, " + todoEntry.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(
                todoEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        Log.i("DB", "perfrom query data:");
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(todoEntry.COLUMN_NAME_ID));
            Date date = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(todoEntry.COLUMN_NAME_DATE)));
            String content = cursor.getString(cursor.getColumnIndex(todoEntry.COLUMN_NAME_CONTENT));
            State state = State.valueOf(cursor.getString(cursor.getColumnIndex(todoEntry.COLUMN_NAME_STATE)));
            int priority = cursor.getInt(cursor.getColumnIndexOrThrow(todoEntry.COLUMN_NAME_PRIORITY));
            note=new Note(itemId);
            note.setContent(content);
            note.setDate(date);
            note.setState(state);
            note.setPriority(priority);
            noteList.add(note);
            Log.i("DB", "itemId:" + itemId + ", date:" + date + ", content:" + content
                    + ", state:" + state + ", priority:" + priority);
        }
        cursor.close();
        return noteList;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = todoEntry.COLUMN_NAME_ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(note.id)};
        // Issue SQL statement.
        int deletedRows = db.delete(todoEntry.TABLE_NAME, selection, selectionArgs);
        Log.i("DB", "perform delete data, result:" + deletedRows);

    }

    private void updateNode(Note note) {
        // 更新数据
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //Date date = new Date(System.currentTimeMillis());
        values.put(todoEntry.COLUMN_NAME_DATE, System.currentTimeMillis());
        values.put(todoEntry.COLUMN_NAME_CONTENT, note.getContent());
        values.put(todoEntry.COLUMN_NAME_STATE, String.valueOf(State.DONE));

        String selection = todoEntry.COLUMN_NAME_ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(note.id)};

        int updateRows = db.update(todoEntry.TABLE_NAME, values, selection, selectionArgs);
        Log.i("DB", "perform update data, result:" + updateRows);
    }

}
