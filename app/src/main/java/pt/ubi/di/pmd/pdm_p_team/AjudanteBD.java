package pt.ubi.di.pmd.pdm_p_team;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AjudanteBD extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "P_TEAM";
    protected static final String TABLE_NAME = "Login";
    protected static final String COL1 = "username";
    protected static final String COL2 = "password";
    protected static final String COL3 = "email";
    protected static final String COL4 = "nome";
    protected static final String COL5 = "numero_aluno";
    protected static final String COL6 = "salt";
    protected static final String CREATE_ACCOUNT = ("CREATE TABLE "+TABLE_NAME + "("+COL1+" varchar(30) PRIMARY KEY ,"+COL2+" varchar(30),"+COL3+" varchar(30),"+COL4+" varchar(50),"+COL5+" INTEGER ,"+COL6+" varchar(128));"  );

    public AjudanteBD(Context context) {
        super(context,DB_NAME , null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+ TABLE_NAME +";");
        db.execSQL(CREATE_ACCOUNT);
    }
}
