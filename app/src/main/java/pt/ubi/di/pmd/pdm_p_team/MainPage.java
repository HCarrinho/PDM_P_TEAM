package pt.ubi.di.pmd.pdm_p_team;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;


public class MainPage extends Activity {
    private SQLiteDatabase oSQLiteDB;
    private AjudanteBD oAPABD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        oAPABD = new AjudanteBD(this);
        oSQLiteDB = oAPABD.getWritableDatabase();

        }
    @Override
    protected void onPause() {
        super.onPause();
        oAPABD.close();
    }


    @Override
    protected void onResume() {
        super.onResume();
        oAPABD.close();
    }
    }


