package pt.ubi.di.pmd.pdm_p_team;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class MainActivity extends Activity {

    private static final char[] BYTE2HEX = (
            "000102030405060708090A0B0C0D0E0F" +
                    "101112131415161718191A1B1C1D1E1F" +
                    "202122232425262728292A2B2C2D2E2F" +
                    "303132333435363738393A3B3C3D3E3F" +
                    "404142434445464748494A4B4C4D4E4F" +
                    "505152535455565758595A5B5C5D5E5F" +
                    "606162636465666768696A6B6C6D6E6F" +
                    "707172737475767778797A7B7C7D7E7F" +
                    "808182838485868788898A8B8C8D8E8F" +
                    "909192939495969798999A9B9C9D9E9F" +
                    "A0A1A2A3A4A5A6A7A8A9AAABACADAEAF" +
                    "B0B1B2B3B4B5B6B7B8B9BABBBCBDBEBF" +
                    "C0C1C2C3C4C5C6C7C8C9CACBCCCDCECF" +
                    "D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF" +
                    "E0E1E2E3E4E5E6E7E8E9EAEBECEDEEEF" +
                    "F0F1F2F3F4F5F6F7F8F9FAFBFCFDFEFF").toCharArray();
    private final int iterations = 10000;
    private final int keyLength = 512;
    private SQLiteDatabase oSQLiteDB;
    private AjudanteBD oAPABD;
    private int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oAPABD = new AjudanteBD(this);
        oSQLiteDB = oAPABD.getWritableDatabase();
        //sharedpreferences para saber se é a primeira vez que o utilizador entra na aplicação
        SharedPreferences oSP = getPreferences(0);
        if (!oSP.getBoolean("recover", true)) {
            setContentView(R.layout.activity_main2);
            flag = 1;
        }else{
            setContentView(R.layout.activity_main);
        }



    }



    @Override
    protected void onPause() {
        super.onPause();
        oAPABD.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        oSQLiteDB = oAPABD.getWritableDatabase();

    }
    public void Registo(View v)  {
        SharedPreferences oSP = getPreferences(0);
        SharedPreferences.Editor oEditor = oSP.edit();
        oEditor.putBoolean("recover", false);
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[64];
        random.nextBytes(bytes);
        byte seed[] = random.generateSeed(64);
        String salt = getHexString(seed);
        ContentValues oCV = new ContentValues();
        EditText username = (EditText) findViewById(R.id.edittext1);
        EditText password = (EditText) findViewById(R.id.edittext);
        EditText email = (EditText) findViewById(R.id.edittext3);
        EditText nome = (EditText) findViewById(R.id.edittext4);
        EditText numero = (EditText) findViewById(R.id.edittext5);
        String usernamestring = ""+username.getText().toString();
        String pass = password.getText().toString();
        String emailstring = email.getText().toString();
        String nomestring = nome.getText().toString();
        String numerostring = numero.getText().toString();
        int numeroint = Integer.parseInt(numerostring);
        char[] passwordChars = pass.toCharArray();
        byte[] hashedPassword = hashPassword(passwordChars, seed, iterations, keyLength);
        String hashedPasswordClean = getHexString(hashedPassword);
        System.out.println(hashedPasswordClean+"    password registo");
        String rawQuery = "Select * From "+oAPABD.TABLE_NAME+ " where "+oAPABD.COL1+" = '"+usernamestring+"' ;";
           Cursor oCursor = oSQLiteDB.rawQuery(rawQuery,null);
           boolean bCarryOn = oCursor.moveToFirst();
           bCarryOn =oCursor.moveToNext();
           if (bCarryOn){
               Toast.makeText(MainActivity.this, "Username Indisponível",
                       Toast.LENGTH_SHORT).show();
           }else{
               oCV.put(oAPABD.COL1, usernamestring);
               oCV.put(oAPABD.COL2, hashedPasswordClean);
               oCV.put(oAPABD.COL3, emailstring);
               oCV.put(oAPABD.COL4, nomestring);
               oCV.put(oAPABD.COL5, numeroint);
               oCV.put(oAPABD.COL6, salt);
               oSQLiteDB.insert(oAPABD.TABLE_NAME, null, oCV);
               oEditor.commit();
               super.finish();
               Intent iActivity = new Intent(this, MainActivity.class);
               startActivity(iActivity);
           }
       }




    public void Login(View v){
        String user;
        String password;
        String salt;
        EditText username = (EditText) findViewById(R.id.edittext1);
        EditText passwordtexto = (EditText) findViewById(R.id.edittext);
        String rawQuery = "Select * From "+oAPABD.TABLE_NAME+ " where "+oAPABD.COL1+" = '"+username.getText().toString()+"' ;";
        Cursor oCursor = oSQLiteDB.rawQuery(rawQuery,null);
        boolean bCarryOn = oCursor.moveToFirst();
        if (bCarryOn){
            user = oCursor.getString(0);
            password = oCursor.getString(1);
            salt = oCursor.getString(5);
            byte saltBytes[] = hexStringToByteArray(salt);
            String passwordIntroduzida = passwordtexto.getText().toString();
            char[] passwordChars = passwordIntroduzida.toCharArray();
            byte[] hashedPassword = hashPassword(passwordChars, saltBytes, iterations, keyLength);
            String passwordcorreta = getHexString(hashedPassword);
            System.out.println(passwordcorreta+"   password introduzida");
            System.out.println(password+"       password da bd");
            if (password.equals(passwordcorreta)) {
                Intent iActivity = new Intent(this, MainPage.class);
                iActivity.putExtra("string1", "login efetuado");
                Toast.makeText(MainActivity.this, "login efetuado",
                        Toast.LENGTH_SHORT).show();
                startActivity(iActivity);
            } else {
                Toast.makeText(MainActivity.this, "Password incorrecta",
                        Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(MainActivity.this, "O username que inseriu não existe.",
                    Toast.LENGTH_SHORT).show();
        }


    }

    //metodo para converter hexstring para um array de bytes
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    //metodo para efetuar o hash das passwords
    public static byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    //metodo para converter um array de bytes para hexstring
    public static String getHexString(byte[] bytes) {

        final int len = bytes.length;
        final char[] chars = new char[len << 1];
        int hexIndex;
        int idx = 0;
        int ofs = 0;
        while (ofs < len) {
            hexIndex = (bytes[ofs++] & 0xFF) << 1;
            chars[idx++] = BYTE2HEX[hexIndex++];
            chars[idx++] = BYTE2HEX[hexIndex];
        }
        return new String(chars);
    }


}
