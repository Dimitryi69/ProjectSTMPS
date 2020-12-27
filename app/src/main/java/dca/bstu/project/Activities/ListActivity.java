package dca.bstu.project.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dca.bstu.project.DBHelper;
import dca.bstu.project.Data.Recipie;
import dca.bstu.project.Data.User;
import dca.bstu.project.ListAdapter;
import dca.bstu.project.R;

public class ListActivity extends AppCompatActivity {

    public List<Recipie> list;
    DBHelper databaseHelper;
    SQLiteDatabase db;
    @Override
    public void onResume()
    {
        super.onResume();
        reloadData();
        this.setTitle(MainActivity.currentUser.getLogin());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        databaseHelper = new DBHelper(getApplicationContext());
        db = databaseHelper.getReadableDatabase();
        list = new ArrayList<Recipie>();
        reloadData();
        Cursor cursor = db.rawQuery("Select leftcomponents, name, ready from Recipies where user_creator = "+MainActivity.currentUser.getId()+" AND ready = 0", null);
        if(cursor.moveToFirst())
        {
            do{
                if(cursor.getString(0).equals(".")&&cursor.getInt(2)!=1)
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(ListActivity.this).create();
                    alertDialog.setTitle("Ready to cook");
                    alertDialog.setMessage("Your dish "+cursor.getString(1)+" gathered enough components to be cooked");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }while(cursor.moveToNext());
        }
        cursor = db.rawQuery("Select Recipies.ready, Recipies.name, Recipies.Time, Recipies.Adress, Users.login, Recipies.SubscribedUsers from Recipies " +
                "inner join Users on Recipies.user_creator = " +
                "Users.id where Recipies.user_creator != "+MainActivity.currentUser.getId()+" AND Recipies.ready = 1", null);
        if(cursor.moveToFirst())
        {
            do{
                List<String> users = new ArrayList<>();
                users = Divide(cursor.getString(5));
                for(String userid:users)
                {
                    if(MainActivity.currentUser.getId() == Integer.parseInt(userid))
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(ListActivity.this).create();
                        alertDialog.setTitle("Someone is waiting for you");
                        alertDialog.setMessage(cursor.getString(4)+" is ready to cook their dish "+cursor.getString(1)+". They are waiting for you at " +
                                cursor.getString(2)+" on this adress "+ cursor.getString(3)+". Good luck!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        break;
                    }
                }
            }while(cursor.moveToNext());
        }
    }
    public List<String> Divide(String k)
    {
        List<String> lr = new ArrayList<>();
        String Component = "";
        for(int i = 0; i<k.length();i++)
        {
            if(k.charAt(i) != ';' && k.charAt(i) != ' ' && k.charAt(i) != '.')
            {
                Component += String.valueOf(k.charAt(i));
            }
            else if (k.charAt(i) == ';')
            {
                lr.add(Component);
                Component ="";
            }
            else if(k.charAt(i) == '.') break;
        }
        return lr;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addOption:
                Intent intentAdd = new Intent(this, AddRecipie.class);
                startActivity(intentAdd);
                return true;
            case R.id.reloadOption:
                reloadData();
                return true;
            case R.id.logoutOption:
                MainActivity.currentUser =null;
                File f = new File(super.getFilesDir(), "user.json");
                if (f.exists()) f.delete();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void reloadData()
    {
        list.clear();
        Cursor c = db.rawQuery("select * from Recipies", null);
        if (c.moveToFirst()) {
            if(c.getCount()>1) {
                do {
                    list.add(new Recipie(c.getString(2),
                            c.getInt(0),
                            Divide(c.getString(1)),
                            Divide(c.getString(3)),
                            c.getInt(4),
                            c.getInt(5),
                            BitmapFactory.decodeByteArray(c.getBlob(6), 0, c.getBlob(6).length),
                            c.getString(8),
                            c.getString(9)));
                } while (c.moveToNext());
            }
            else list.add(new Recipie(c.getString(2),
                    c.getInt(0),
                    Divide(c.getString(1)),
                    Divide(c.getString(3)),
                    c.getInt(4),
                    c.getInt(5),
                    BitmapFactory.decodeByteArray(c.getBlob(6), 0, c.getBlob(6).length),
                    c.getString(8),
                    c.getString(9)));
        }
        c.close();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ListAdapter adapter = new ListAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }
}