package com.zju.rascontroller;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class OxygenDatabaseProvider extends ContentProvider {
	public static final int OXYGEN_DIR=0;
	public static final int OXYGEN_ITEM=1;
	public static final String AUTHORITY = "com.zju.rascontroller.provider";
	private static UriMatcher uriMatcher;
	private OxygenDatabaseHelper dbHelper;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "oxygen",OXYGEN_DIR );
		uriMatcher.addURI(AUTHORITY, "oxygen/#",OXYGEN_ITEM);
	}

	public OxygenDatabaseProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		dbHelper = new OxygenDatabaseHelper(getContext(), "WaterQuality.db", null, 1);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		switch(uriMatcher.match(uri)){
		case OXYGEN_DIR:
			cursor = db.query("Oxygen", projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case OXYGEN_ITEM:
			String oxygenId=uri.getPathSegments().get(1);
			cursor = db.query("Oxygen", projection, "id=?",  new String[] { oxygenId }, null, null,
					sortOrder);
			break;
		default:
			break;
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch(uriMatcher.match(uri))
		{
		case OXYGEN_DIR:
			return "vnd.android.cursor.dir/vnd.com.zju.rascontroller.provider.oxygen";
		case OXYGEN_ITEM:
			return "vnd.android.cursor.item/vnd.com.zju.rascontroller.provider.oxygen";
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Uri uriReturn = null;
		switch(uriMatcher.match(uri))
		{
		case OXYGEN_DIR:
		case OXYGEN_ITEM:
			long newOxygenId=db.insert("Oxygen", null, values);
			uriReturn = Uri.parse("content://" + AUTHORITY + "/oxygen/" + newOxygenId);
			break;
		default:
			break;
		}
		return uriReturn;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		int deletedRows = 0;
		switch(uriMatcher.match(uri))
		{
		case OXYGEN_DIR:
			deletedRows=db.delete("Oxygen", selection, selectionArgs);
			break;
		case OXYGEN_ITEM:
			String oxygenId=uri.getPathSegments().get(1);
			deletedRows=db.delete("Oxygen", "id=?", new String[]{oxygenId});
			break;
		default:
			break;
		
		}
		return deletedRows;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int updatedRows = 0;
		switch(uriMatcher.match(uri))
		{
		case OXYGEN_DIR:
			updatedRows=db.update("Oxygen", values, selection, selectionArgs);
		case OXYGEN_ITEM:	
			String oxygenId=uri.getPathSegments().get(1);
			updatedRows=db.update("Oxygen", values, "id=?", new String[]{oxygenId});
			break;
		default:
			break;
		}
		return updatedRows;
	}

}
