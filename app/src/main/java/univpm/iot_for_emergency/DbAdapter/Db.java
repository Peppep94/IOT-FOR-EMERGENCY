package univpm.iot_for_emergency.DbAdapter;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Db extends SQLiteOpenHelper {

    public static final String TAG =Db.class.getSimpleName();
    public static final String DB_NAME ="myapp.db";
    public static final int DB_VERSION=6;
    public static final String TABELLA_UTENTE ="users";
    public static final String ID ="_id";
    public static final String NOME ="nome";
    public static final String COGNOME ="cognome";
    public static final String PASSWORD ="password";
    public static final String DATAN ="data";
    public static final String USER ="user";
    public static final String PROBLEMI ="problemi";
    public static final String SESSO ="sesso";

    public static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABELLA_UTENTE + "( "+
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
            USER + " TEXT,"+
            NOME + " TEXT,"+
            COGNOME + " TEXT,"+
            PASSWORD + " TEXT,"+
            PROBLEMI + " TEXT,"+
            DATAN + " TEXT,"+
            SESSO + " TEXT);";

    public Db(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELLA_UTENTE);
        onCreate(db);
    }

    public void InserisciUtenti(String username, String name,String cognome, String password, String data, String problemi,String sesso){

        SQLiteDatabase db =this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER,username);
        values.put(NOME,name);
        values.put(COGNOME,cognome);
        values.put(PASSWORD,password);
        values.put(PROBLEMI,problemi);
        values.put(DATAN,data);
        values.put(SESSO,sesso);
        long id=db.insert(TABELLA_UTENTE,null,values);
        db.close();

        Log.d(TAG, "id utente registrato " + id);

    }


    public boolean getUser(String user, String pass){
        String selectQuery = "select * from  " + TABELLA_UTENTE + " where " +
                USER + " = " + "'"+user+"'" + " and " + PASSWORD + " = " + "'"+pass+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Vai alla prima riga
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
        String[] utente =new String[8];
        Cursor c = db.rawQuery("select * from "   + TABELLA_UTENTE + " where " +
                USER + " = " + "'"+user+"'", null);
        if(c.moveToFirst()){

            utente[0]=c.getString(0);
            utente[1]= c.getString(1);
            utente[2]= c.getString(2);
            utente[3]= c.getString(3);
            utente[4]= c.getString(4);
            utente[5]= c.getString(5);
            utente[6]= c.getString(6);
            utente[7]= c.getString(7);

            Log.d(TAG, "user " + utente[1]);
            Log.d(TAG, "nome " + utente[2]);
            Log.d(TAG, "cognome " + utente[3]);
            Log.d(TAG, "password " + utente[4]);
            Log.d(TAG, "problemi " + utente[5]);
            Log.d(TAG, "DataN " + utente[6]);
            Log.d(TAG, "sesso " + utente[7]);


        }
        c.close();
        db.close();
        return  utente;
    }

    public int MaxId(){
        int id;
        String countQuery="SELECT MAX("+ID+") FROM "+TABELLA_UTENTE;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);
        cursor.moveToFirst();
        id= cursor.getInt(0);
        Log.d(TAG,"max id"+id);
        cursor.close();
        db.close();
        return id;
    }

    public void modificaUtente(String username, String name,String cognome, String password, String data, String problemi,String sesso,int identificativo){

        SQLiteDatabase db =this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER,username);
        values.put(NOME,name);
        values.put(COGNOME,cognome);
        values.put(PASSWORD,password);
        values.put(PROBLEMI,problemi);
        values.put(DATAN,data);
        values.put(SESSO,sesso);
        long id=db.update(TABELLA_UTENTE,values,"_id= ?",new String[] {String.valueOf(identificativo)});
        db.close();
    }
}
