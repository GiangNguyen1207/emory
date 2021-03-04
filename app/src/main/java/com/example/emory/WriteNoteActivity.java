package com.example.emory;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class WriteNoteActivity extends AppCompatActivity {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final int GALLERY_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private ActionList actionList;
    private ArrayList<Action> chosenActions =new ArrayList<>();
    private String icon, date, note, encodedPic;
    private ArrayList<Diary> diaries = new ArrayList<>();
    private Bitmap bitmap;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);
        getDataFromAddMood();
        //getActivity();
        handleActionIcons();
        getImage();
        saveData();
    }

    public void getDataFromAddMood() {
        Intent intent = getIntent();
        icon = intent.getStringExtra("icon");
        date = intent.getStringExtra("date");

        int resourceId = getResources().getIdentifier("com.example.emory:drawable/" + icon, null, null);
        ImageView imageView = findViewById(R.id.iconChosen);
        Drawable drawable = ContextCompat.getDrawable(this, resourceId);
        imageView.setImageDrawable(drawable);
    }

    public void handleActionIcons() {
        actionList = new ActionList(this);
        GridView grid = findViewById(R.id.actionList);
        grid.setAdapter(new ActionList(this, actionList.getAllActions()));

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view = grid.getChildAt(position);
                ColorDrawable viewColor = (ColorDrawable) view.getBackground();

                if (viewColor == null) {
                    view.setBackgroundColor(getColor(R.color.highlight));
                    chosenActions.add(actionList.getAction(position));
                    return;
                }

                if (viewColor.getColor() == getColor(R.color.highlight)) {
                    view.setBackgroundColor(getColor(R.color.blur));
                    chosenActions.remove(actionList.getAction(position));
                    return;
                }
            }
        });
    }

    public void getNote() {
        EditText editText = findViewById(R.id.writeNote);
        note = editText.getText().toString();
    }

    public void getImage() {
        ImageButton addImage = findViewById(R.id.addPhoto);
        addImage.setOnClickListener((View v) -> {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_import_picture);
            checkBtnClick(dialog);
            dialog.show();
        });
    }

    public void checkBtnClick(Dialog dialog) {
        ImageButton btnCamera = dialog.findViewById(R.id.takePhotoBtn);
        ImageButton btnGallery = dialog.findViewById(R.id.openGalleryBtn);
        btnCamera.setOnClickListener((View v) -> {
            openCamera();
        });
        btnGallery.setOnClickListener((View v) -> {
            openGallery();
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    public void openGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
        cameraIntent.setType("image/*");
        cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(cameraIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                        encodeBitmap();
                        //Bitmap bMapScaled = Bitmap.createScaledBitmap(bitmap, 150, 100, true);
                        //Uri selectedImage = data.getData();
                        //InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        ImageView imageView = findViewById(R.id.photoChosen);
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading file", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    bitmap = (Bitmap) extras.get("data");
                    encodeBitmap();
                    ImageView imageView = findViewById(R.id.photoChosen);
                    imageView.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(this, "Error loading file", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
        dialog.dismiss();
    }

    public void encodeBitmap() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] b = output.toByteArray();
        encodedPic = Base64.encodeToString(b, Base64.DEFAULT);
    }


    public void saveDiary() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String data = sharedPreferences.getString(date, null);

        if (data != null) {
            Type diaryType = new TypeToken<ArrayList<Diary>>() {
            }.getType();
            diaries = gson.fromJson(data, diaryType);
        }

        diaries.add(new Diary(icon, chosenActions, note, encodedPic));
        editor.putString(date, gson.toJson(diaries));
        editor.apply();
    }

    public void saveData() {
        FloatingActionButton floatBtn = findViewById(R.id.doneIcon);
        floatBtn.setOnClickListener(view -> {
            getNote();
            saveDiary();
            Intent intent = new Intent(this, EntriesActivity.class);
            startActivity(intent);
        });
    }
}