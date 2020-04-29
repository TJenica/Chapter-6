package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract.todoEntry;
import com.byted.camp.todolist.db.TodoDbHelper;


public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private RadioButton first;
    private RadioButton second;
    private RadioButton last;
    private TodoDbHelper todoDbHelper;
    private int priority = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);
        todoDbHelper = new TodoDbHelper(this);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);
        first = findViewById(R.id.btn_first);
        second = findViewById(R.id.btn_second);
        last = findViewById(R.id.btn_last);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(first.isChecked()){
                    priority = 2;
                }
                if(second.isChecked()){
                    priority = 1;
                }
                boolean succeed = saveNote2Database(content.toString().trim(), priority);
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean saveNote2Database(String content, int priority) {
        // TODO 插入一条新数据，返回是否插入成功
        boolean flag;
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(todoEntry.COLUMN_NAME_DATE, System.currentTimeMillis());
        values.put(todoEntry.COLUMN_NAME_CONTENT, content);
        values.put(todoEntry.COLUMN_NAME_STATE, String.valueOf(State.TODO));
        values.put(todoEntry.COLUMN_NAME_PRIORITY, priority);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(todoEntry.TABLE_NAME, null, values);
        Log.i("DB", "perform add data, result:" + newRowId);
        if(newRowId > 0) flag = true;
        else flag = false;
        return flag;
    }
}
