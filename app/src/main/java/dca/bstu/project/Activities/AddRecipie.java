package dca.bstu.project.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import dca.bstu.project.DBHelper;
import dca.bstu.project.Data.Recipie;
import dca.bstu.project.R;

public class AddRecipie extends AppCompatActivity {

    EditText edName, edTime, edAdress, edcomp;
    ImageView imageView;
    DBHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipie);
        this.setTitle("Add");
        edName = findViewById(R.id.recName);
        edTime = findViewById(R.id.recTime);
        edAdress = findViewById(R.id.recAdress);
        edcomp = findViewById(R.id.comp);
        imageView = findViewById(R.id.RecImage);
        databaseHelper = new DBHelper(getApplicationContext());
        db = databaseHelper.getReadableDatabase();
        edcomp.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tryParseInt(s.toString())) {
                    if (Integer.parseInt(s.toString()) > 0) {
                        for (int i = 1; i < 21; i++) {
                            int id = getResources().getIdentifier("comp" + i, "id", getPackageName());
                            EditText v = findViewById(id);
                            if (i <= Integer.parseInt(s.toString())) {
                                v.setVisibility(View.VISIBLE);
                            } else {
                                v.setVisibility(View.GONE);
                            }
                        }
                    } else edcomp.setText("1");
                }
            }
        });

    }

    public void onImageClick(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri img;
                        img = data.getData();
                        InputStream imageStream = this.getContentResolver().openInputStream(img);
                        Bitmap bitmapimg = BitmapFactory.decodeStream(imageStream);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapimg.compress(Bitmap.CompressFormat.PNG, 20, stream);
                        bitmapimg = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().length);
                        imageView.setImageBitmap(bitmapimg);
                        break;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    public void onAddClick(View v) {
        String components = "";
        boolean check = true;
        if (!edName.getText().toString().isEmpty() && !edTime.getText().toString().isEmpty()
                && !edAdress.getText().toString().isEmpty() && !((EditText) findViewById(R.id.comp1)).getText().toString().isEmpty()
                && ((BitmapDrawable)imageView.getDrawable()).getBitmap()!=null) {
            Cursor c = db.rawQuery("select * from Recipies", null);
            if (c.moveToFirst()) {
                if(c.getCount()>1) {
                    while (c.moveToNext()) {
                        if (c.getString(2).equals(edName.getText().toString())) {
                            check = false;
                            Toast.makeText(this, "Recipie with this name is already in the list", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else if (c.getString(2).equals(edName.getText().toString())) {
                    check = false;
                    Toast.makeText(this, "Recipie with this name is already in the list", Toast.LENGTH_SHORT).show();
                }
            }
            c.close();
            if (check) {
                String name = edName.getText().toString();
                String time = edTime.getText().toString();
                String adress = edAdress.getText().toString();
                int comp = Integer.parseInt(edcomp.getText().toString());
                for (int i = 1; i < 21; i++) {
                    int id = getResources().getIdentifier("comp" + i, "id", getPackageName());
                    EditText view = findViewById(id);
                    if (view.getVisibility() == View.VISIBLE && !view.getText().toString().isEmpty()) {
                        components += view.getText().toString() + "; ";
                    }
                }
                components += ".";
                if (!components.isEmpty()) {
                    Bitmap bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    ContentValues cv = new ContentValues();
                    cv.put("allcomponents", components);
                    cv.put("name", name);
                    cv.put("leftcomponents", components);
                    cv.put("user_creator", MainActivity.currentUser.getId());
                    cv.put("ready", 0);
                    cv.put("Image", byteArray);
                    cv.put("SubscribedUsers", MainActivity.currentUser.getId()+"; .");
                    cv.put("Adress", adress);
                    cv.put("Time", time);
                    db.insert( "Recipies", null, cv );
                    Toast.makeText(this, "Added succesfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else Toast.makeText(this, "Wrong components", Toast.LENGTH_SHORT).show();
            }
        } else Toast.makeText(this, "Wrong data input", Toast.LENGTH_SHORT).show();

    }

    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}