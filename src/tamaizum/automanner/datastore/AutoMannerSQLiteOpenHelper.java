package tamaizum.automanner.datastore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AutoMannerSQLiteOpenHelper extends SQLiteOpenHelper {
	private static final String DB = "automannar.db";
	private static final int DB_VER = 100;

	public AutoMannerSQLiteOpenHelper(Context context){
		super(context, DB,null, DB_VER);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + "MAP" + 
				   " ( _id integer primary key , title text, latitude double , longitude double ,radius double ,isOn int) ");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
