package univpm.iot_for_emergency;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    public static final String TAG =DbHelper.class.getSimpleName();
    public static final String DB_NAME ="myapp.db";
    public static final int DB_VERSION=5;
    public static final String USER_TABLE ="users";
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_NOME ="nome";
    public static final String COLUMN_COGNOME ="cognome";
    public static final String COLUMN_PASS ="password";
    public static final String COLUMN_DATA ="data";
    public static final String COLUMN_USER ="user";
    public static final String COLUMN_PROBLEMI ="problemi";

    public static final String CREATE_TABLE_USERS = "CREATE TABLE " + USER_TABLE + "( "+
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
            COLUMN_USER + " TEXT,"+
            COLUMN_NOME + " TEXT,"+
            COLUMN_COGNOME + " TEXT,"+
            COLUMN_PASS + " TEXT,"+
            COLUMN_PROBLEMI + " TEXT,"+
            COLUMN_DATA + " TEXT);";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    public void addUser(String username, String name,String cognome, String password, String date, String problemi){

        SQLiteDatabase db =this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER,username);
        values.put(COLUMN_NOME,name);
        values.put(COLUMN_COGNOME,cognome);
        values.put(COLUMN_PASS,password);
        values.put(COLUMN_DATA,date);
        values.put(COLUMN_PROBLEMI,problemi);
        long id=db.insert(USER_TABLE,null,values);
        db.close();

        Log.d(TAG, "id utente registrato " + id);

    }


    public boolean getUser(String user, String pass){
        //HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "select * from  " + USER_TABLE + " where " +
                COLUMN_USER + " = " + "'"+user+"'" + " and " + COLUMN_PASS + " = " + "'"+pass+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {

            return true;
        }
        cursor.close();
        db.close();

        return false;
    }

    public String[] getUtente(String user){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] utente =new String[5];
        Cursor c = db.rawQuery("select * from "   + USER_TABLE + " where " +
                COLUMN_USER + " = " + "'"+user+"'", null);
        if(c.moveToFirst()){

            utente[1]= c.getString(1);
            utente[2]= c.getString(2);
            utente[3]= c.getString(3);
            utente[4]= c.getString(4);
//                utente[5]= c.getString(5);

            Log.d(TAG, "user " + utente[1]);
            Log.d(TAG, "nome " + utente[2]);
            Log.d(TAG, "cognome " + utente[3]);
            Log.d(TAG, "password " + utente[4]);
            //Log.d(TAG, "eta " + utente[5]);


        }
        c.close();
        db.close();
        return  utente;
    }
}
