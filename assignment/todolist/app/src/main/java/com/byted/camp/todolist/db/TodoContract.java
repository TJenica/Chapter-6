package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + todoEntry.TABLE_NAME + " (" +
                    todoEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    todoEntry.COLUMN_NAME_DATE + " INTEGER," +
                    todoEntry.COLUMN_NAME_CONTENT + " TEXT," +
                    todoEntry.COLUMN_NAME_STATE + " INTEGER," +
                    todoEntry.COLUMN_NAME_PRIORITY + " INTEGER DEFAULT 0)";

    public static final String SQL_DROP_ENTRIES = "DROP TABLE IF EXISTS " + todoEntry.TABLE_NAME;
//    public static final String SQL_ALTER_ENTRIES = "ALTER TABLE " + todoEntry.TABLE_NAME + " ADD " +
//            todoEntry.COLUMN_NAME_PRIORITY + "INTEGER";
    private TodoContract() {

    }
    public static class todoEntry implements BaseColumns {

        public static final String TABLE_NAME = "todo";

        public static final String COLUMN_NAME_ID = "id";

        public static final String COLUMN_NAME_DATE = "date";

        public static final String COLUMN_NAME_STATE = "state";

        public static final String COLUMN_NAME_CONTENT = "content";

        public static final String COLUMN_NAME_PRIORITY = "priority";
    }

}
