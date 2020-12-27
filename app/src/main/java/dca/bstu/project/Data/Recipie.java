package dca.bstu.project.Data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.sql.Time;
import java.util.List;

public class Recipie
{
    private int id;
    private String Name;
    private List<String> Components;
    private List<String> LeftComponents;
    private int UserCreator;
    private int Ready;
    private Bitmap image;
    private String Time;
    private String Adress;

    public Recipie(String name, int id, List<String> components, List<String> leftComponents, int userCreator, int ready, Bitmap img, String time, String adress) {
        this.id = id;
        Name = name;
        Components = components;
        LeftComponents = leftComponents;
        UserCreator = userCreator;
        Ready = ready;
        image = img;
        Time = time;
        Adress = adress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getComponents() {
        return Components;
    }

    public void setComponents(List<String> components) {
        Components = components;
    }

    public List<String> getLeftComponents() {
        return LeftComponents;
    }

    public void setLeftComponents(List<String> leftComponents) {
        LeftComponents = leftComponents;
    }

    public int getUserCreator() {
        return UserCreator;
    }

    public void setUserCreator(int userCreator) {
        UserCreator = userCreator;
    }

    public int getReady() {
        return Ready;
    }

    public void setReady(int ready) {
        Ready = ready;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getAdress() {
        return Adress;
    }

    public void setAdress(String adress) {
        Adress = adress;
    }
}
