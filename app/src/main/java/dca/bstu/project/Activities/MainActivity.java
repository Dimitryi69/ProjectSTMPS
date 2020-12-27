package dca.bstu.project.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dca.bstu.project.DBHelper;
import dca.bstu.project.Data.User;
import dca.bstu.project.R;

public class MainActivity extends AppCompatActivity {

    EditText login, pass;
    CheckBox remember;
    DBHelper databaseHelper;
    SQLiteDatabase db;
    public static User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.login);
        pass = findViewById(R.id.pass);
        remember = findViewById(R.id.remember);
        databaseHelper = new DBHelper(getApplicationContext());
        db = databaseHelper.getReadableDatabase();
        currentUser = new User();
        File f = new File(super.getFilesDir(), "user.json");
        if (f.exists()) {
            Gson gson = new Gson();
            try {
                FileReader fileReader = new FileReader(f);
                char[] buf = new char[(int) f.length()];
                fileReader.read(buf);
                fileReader.close();
                String s = new String(buf);
                Log.d("Log_07", s);
                currentUser = gson.fromJson(s, User.class);
                Log.d("Log_07", "File read from json");
                Intent intent = new Intent(this, ListActivity.class);
                startActivity(intent);
                finish();
            } catch (FileNotFoundException e) {
                Log.d("Log_07", "readFromJson: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(this, "Sign in error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void onClickLog(View v) {
        Cursor c = db.rawQuery("select * from Users where login ='"+login.getText().toString()+"' AND password =" +
                " '"+pass.getText().toString()+"'", null);
        if (c.moveToFirst()) {


                if (login.getText().toString().equals(c.getString(1))
                        && pass.getText().toString().equals(c.getString(2))) {
                    currentUser = new User(c.getString(1), c.getString(2), c.getInt(0));
                    File f = new File(super.getFilesDir(), "user.json");
                    if (remember.isChecked()) {
                        try {
                            if (!f.exists()) f.createNewFile();
                            Gson gson = new Gson();
                            String jb = gson.toJson(currentUser);
                            FileWriter fileWriter = new FileWriter(f);
                            fileWriter.write(jb);
                            Log.d("Log_07", "Data recorded");
                            fileWriter.close();
                        } catch (IOException e) {
                            Toast.makeText(this, "WriteFile error", Toast.LENGTH_SHORT).show();
                        }
                    } else if (f.exists()) f.delete();
                    Intent intent = new Intent(this, ListActivity.class);
                    startActivity(intent);
                    finish();
                }

        }
        else
            Toast.makeText(this, "There is no such user", Toast.LENGTH_SHORT).show();

    }

    public void onClickReg(View v) {
        Cursor c = db.rawQuery("select login from Users", null);
        boolean check = true;
        String regex = "[a-zA-Z0-9._\\-]{3,60}";
        String log = login.getText().toString();
        String password = pass.getText().toString();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(log);
        if (!matcher.matches()) {
            Toast.makeText(this, "Wrong username format", Toast.LENGTH_SHORT).show();
            check = false;
        }
        matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            Toast.makeText(this, "Wrong username format", Toast.LENGTH_SHORT).show();
            check = false;
        }
        if (c.moveToFirst() && check) {

                do
                {
                    if (login.getText().toString().equals(c.getString(0))) {
                        check = false;
                        Toast.makeText(this, "This use is already in database", Toast.LENGTH_SHORT).show();
                    }
                }while (c.moveToNext());
                c.close();

        }
        if (check) {
            try {
                File f = new File(super.getFilesDir(), "user.json");
                if (remember.isChecked()) {
                    if (f.exists()) f.delete();
                    if (!f.exists()) f.createNewFile();
                    db.execSQL("insert into Users(login, password) values('"+login.getText().toString()+"', '"+pass.getText().toString()+"');");
                    c = db.rawQuery("select * from Users where login = '"+login.getText().toString()+"'", null);
                    if (c.moveToFirst())
                    {
                        currentUser.setId(c.getInt(0));
                        currentUser.setLogin(c.getString(1));
                        currentUser.setPassword(c.getString(1));
                    }
                    Gson gson = new Gson();
                    String jb = gson.toJson(currentUser);
                    FileWriter fileWriter = new FileWriter(f);
                    fileWriter.write(jb);
                    Log.d("Log_07", "Data recorded");
                    fileWriter.close();
                    Toast.makeText(this, "Registration succesful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ListActivity.class);
                    startActivity(intent);
                    finish();
                } else{
                    if (f.exists())
                    f.delete();
                    db.execSQL("insert into Users(login, password) values('"+login.getText().toString()+"', '"+pass.getText().toString()+"');");
                    c = db.rawQuery("select * from Users where login = '"+login.getText().toString()+"'", null);
                    if (c.moveToFirst())
                    {
                        currentUser.setId(c.getInt(0));
                        currentUser.setLogin(c.getString(1));
                        currentUser.setPassword(c.getString(1));
                    }
                    Toast.makeText(this, "Registration succesful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ListActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (IOException e) {
                Toast.makeText(this, "WriteFile error", Toast.LENGTH_SHORT).show();
            }
        }

    }
}