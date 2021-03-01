package com.example.emory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.ArrayList;

public class Diary {
    private int mood;
    private ArrayList<Action> actions;
    private String note;
    private String pic;

    public Diary(int mood, ArrayList<Action> actions, String note, String pic) {
        this.mood = mood;
        this.actions = actions;
        this.note = note;
        this.pic = pic;
    }

    public int getMood() {
        return this.mood;
    }

    public String getNote() {
        if (this.note.isEmpty()) {
            return "Nothing was written...";
        }
        return this.note;
    }

    public ArrayList<Action> getActions() {
        return this.actions;
    }

    public Bitmap decodePic() {
        Bitmap bitmap = null;
        if (this.pic != null) {
            byte[] decodedByte = Base64.decode(this.pic, 0);
            bitmap = BitmapFactory
                    .decodeByteArray(decodedByte, 0, decodedByte.length);
        }
        return bitmap;
    }

    public int getMoodIndicator(String moodName) {
        int indicator = 0;
        switch (moodName) {
            case "excited":
                indicator = 5;
                break;

            case "happy":
                indicator = 4;
                break;

            case "good":
                indicator = 3;
                break;

            case "sad":
                indicator = 2;
                break;

            case "awful":
                indicator = 1;
                break;

            default:
                break;
        }
        return indicator;
    }

    public String toString() {
        return this.mood + " " + this.actions;
    }
}


