package com.zju.rascontroller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class OxygenDatabaseHelper extends SQLiteOpenHelper {
	public static final String CREATE_OXYGEN="create table Oxygen ("
			+"id integer primary key autoincrement, "
			+"dateQ text,"//////≤È—Ø”√
			+"frequency integer,"
            +"timeX real,"
            +"oxygen real)";

	public OxygenDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_OXYGEN);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists Oxygen");
		onCreate(db);

	}

}
