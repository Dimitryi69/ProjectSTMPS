package dca.bstu.project.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dca.bstu.project.DBHelper;
import dca.bstu.project.Data.Recipie;
import dca.bstu.project.Data.User;
import dca.bstu.project.R;

public class DetailsActivity extends AppCompatActivity {

    TextView name, creator, time, adr, id, done;
    ImageView img;
    DBHelper databaseHelper;
    SQLiteDatabase db;
    Recipie ParcelRecipie;
    int idObj;
    List<User> subs;

    @Override
    public void onResume() {
        super.onResume();
        reload();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        databaseHelper = new DBHelper(getApplicationContext());
        db = databaseHelper.getReadableDatabase();
        Intent i = getIntent();
        idObj = i.getIntExtra("RecipieObj", 0);
        Cursor c = db.rawQuery("Select * from Recipies where id = " + idObj, null);
        c.moveToFirst();
        ParcelRecipie = new Recipie(c.getString(2),
                c.getInt(0),
                Divide(c.getString(1)),
                Divide(c.getString(3)),
                c.getInt(4),
                c.getInt(5),
                BitmapFactory.decodeByteArray(c.getBlob(6), 0, c.getBlob(6).length),
                c.getString(9),
                c.getString(8));

        name = findViewById(R.id.name);
        creator = findViewById(R.id.creator);
        time = findViewById(R.id.time);
        adr = findViewById(R.id.adress);
        id = findViewById(R.id.recId);
        done = findViewById(R.id.Done);
        img = findViewById(R.id.imageView);
        fillData(ParcelRecipie);
    }

    public void fillData(Recipie r) {
        name.setText("Recipie Name: " + r.getName());
        creator.setText("Creator: " + String.valueOf(r.getUserCreator()));
        time.setText("Time: " + r.getTime());
        adr.setText("Adress: " + r.getAdress());
        id.setText("id: " + String.valueOf(r.getId()));
        done.setText("Ready to be cooked: " + String.valueOf(r.getReady()));
        img.setImageBitmap(r.getImage());

        for (int i = 1; i < 21; i++) {
            int id = getResources().getIdentifier("check" + i, "id", getPackageName());
            CheckBox view = findViewById(id);
            view.setChecked(false);
            if (i <= r.getLeftComponents().size()) {
                view.setVisibility(View.VISIBLE);
                view.setText(r.getLeftComponents().get(i - 1));
            } else {
                view.setVisibility(View.GONE);
            }
        }
        if (MainActivity.currentUser.getId() == ParcelRecipie.getUserCreator()) {
            findViewById(R.id.delete).setVisibility(View.VISIBLE);
            findViewById(R.id.close).setVisibility(View.VISIBLE);
            List<String> temp = ParcelRecipie.getComponents();
            temp.removeAll(ParcelRecipie.getLeftComponents());
            for (int k = 1; k < 21-ParcelRecipie.getLeftComponents().size(); k++) {
                int id = getResources().getIdentifier("check" + (int)(k + ParcelRecipie.getLeftComponents().size()), "id", getPackageName());
                CheckBox view = findViewById(id);
                if (k <= temp.size()) {
                    view.setVisibility(View.VISIBLE);
                    view.setText(temp.get(k - 1));
                    view.setChecked(true);
                    view.setEnabled(false);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }
        subs = Divide();
        for (User user : subs) {
            if (user.getId() == MainActivity.currentUser.getId()) {
                ((Button) findViewById(R.id.sub)).setText("Add more components");
            }
        }
        this.setTitle(ParcelRecipie.getName());
    }

    public void onDeleteClick(View v) {
        db.execSQL("Delete from Recipies where id = " + ParcelRecipie.getId());
        Toast.makeText(this, "Deleted succesfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onSubscribeClick(View v) {
        List<String> components = new ArrayList<>();
        List<String> componentstemp = new ArrayList<>();
        boolean check = false;
        for (int k = 1; k < 21; k++) {
            int id = getResources().getIdentifier("check" + k, "id", getPackageName());
            CheckBox view = findViewById(id);

            if (view.isChecked()) {
                for (String item : ParcelRecipie.getLeftComponents()) {
                    if (item.equals(view.getText().toString())) {
                        check = true;
                        components.add(view.getText().toString());
                    }
                }
            }
        }
        if (check) {
            if (!((Button) findViewById(R.id.sub)).getText().toString().equals("Add more components")) {
                subs.add(MainActivity.currentUser);
                ((Button) findViewById(R.id.sub)).setText("Add more components");
            }
            componentstemp = ParcelRecipie.getLeftComponents();
            componentstemp.removeAll(components);
            ParcelRecipie.setLeftComponents(componentstemp);
            String newUsers = "";
            String newComponents = "";
            for (User us : subs) {
                newUsers += us.getId() + "; ";
            }
            newUsers += ".";
            for (String Component : ParcelRecipie.getLeftComponents()) {
                newComponents += Component + "; ";
            }
            newComponents += ".";
            db.execSQL("update Recipies set SubscribedUsers = '" + newUsers + "', leftcomponents = '" + newComponents + "' where id = " + ParcelRecipie.getId());
            reload();
        } else Toast.makeText(this, "Choose one more component", Toast.LENGTH_SHORT).show();

    }

    public void onCloseClick(View v) {
        db.execSQL("Update Recipies set ready = 1 where id = " + ParcelRecipie.getId());
        done.setText("1");
        ParcelRecipie.setReady(1);
        Toast.makeText(this, "Subscribed users will see the notification as soon as they will sign in in app", Toast.LENGTH_SHORT).show();
    }

    public List<User> Divide() {
        Cursor c = db.rawQuery("select SubscribedUsers from Recipies where id = " + ParcelRecipie.getId(), null);
        c.moveToFirst();
        String k = c.getString(0);
        List<User> lr = new ArrayList<>();
        String id = "";
        for (int i = 0; i < k.length(); i++) {
            if (k.charAt(i) != ';' && k.charAt(i) != ' ' && k.charAt(i) != '.') {
                id += String.valueOf(k.charAt(i));
            } else if (k.charAt(i) == ';') {
                c = db.rawQuery("select * from Users where id = " + String.valueOf(id), null);
                c.moveToFirst();
                lr.add(new User(c.getString(1), c.getString(2), c.getInt(0)));
                id = "";
            } else if (k.charAt(i) == '.') break;
        }
        c.close();
        return lr;
    }

    public void reload() {
        Cursor c = db.rawQuery("Select * from Recipies where id = " + ParcelRecipie.getId(), null);
        c.moveToFirst();
        ParcelRecipie = new Recipie(c.getString(2),
                c.getInt(0),
                Divide(c.getString(1)),
                Divide(c.getString(3)),
                c.getInt(4),
                c.getInt(5),
                BitmapFactory.decodeByteArray(c.getBlob(6), 0, c.getBlob(6).length),
                c.getString(9),
                c.getString(8));
        c.close();
        fillData(ParcelRecipie);
    }


    public List<String> Divide(String k) {
        List<String> lr = new ArrayList<>();
        String Component = "";
        for (int i = 0; i < k.length(); i++) {
            if (k.charAt(i) != ';' && k.charAt(i) != ' ' && k.charAt(i) != '.') {
                Component += String.valueOf(k.charAt(i));
            } else if (k.charAt(i) == ';') {
                lr.add(Component);
                Component = "";
            } else if (k.charAt(i) == '.') break;
        }
        return lr;
    }
}