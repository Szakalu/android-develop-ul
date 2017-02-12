package com.example.jacek.zbieracz_danych;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DatabasePeople databasePeople = new DatabasePeople(this);
    ArrayList<Person> peopleList = new ArrayList<>();
    LinearLayout linearLayoutPeople;
    private final static String INFO_WHAT_TO_DO = "info_what_to_do";
    private final static String EDIT_ID = "edit_id";
    private final static String EDIT_NAME = "edit_name";
    private final static String EDIT_SURNAME = "edit_surname";
    private final static String EDIT_BIRTHDAY = "edit_birthday";
    private final static String EDIT_PHOTO_PATH = "edit_photo_path";
    private final static String SAVE_CURRENT_LANGUAGE = "saved_current_language";

    private String language;

    private Button buttonLanguage;
    private Button buttonAddPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayoutPeople = (LinearLayout) findViewById(R.id.linearLayoutPeople);
        linearLayoutPeople.setOrientation(LinearLayout.VERTICAL);
        language = Locale.getDefault().getDisplayLanguage();
        setButtons();
    }

    public void clearPeopleList(){
        peopleList.clear();
        linearLayoutPeople.removeAllViews();
    }

    public void toAddPersonActivity(View view){
        Intent intentAddPerson = new Intent(this,AddPersonDetailsActivity.class);
        intentAddPerson.putExtra(INFO_WHAT_TO_DO,"ADD");
        startActivity(intentAddPerson);
    }

    public void toPeopleList(){
        Cursor cursor = databasePeople.getRow();
        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String surname = cursor.getString(2);
            String birthdayDate = cursor.getString(3);
            String photoPath = cursor.getString(4);
            Person person = new Person(id, name, surname, birthdayDate, photoPath);
            peopleList.add(person);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearPeopleList();
        toPeopleList();
        displayPeopleList();
    }
    public void displayPeopleList(){
        for (final Person person: peopleList) {

            LinearLayout firstLinearLayout = new LinearLayout(this);

            TextView textViewDetails = new TextView(this);
            textViewDetails.setText( getString(R.string.main_activity_name) + " " + person.getName() +
                    "\n" + getString(R.string.main_activity_surname) + " " + person.getSurname() +
                    "\n" + getString(R.string.main_activity_birthday) + " " + person.getBirthDayDate());
            textViewDetails.setMinimumWidth(400);
            firstLinearLayout.addView(textViewDetails);

            ImageView imageViewPerson = new ImageView(this);
            setPhoto(person.getPhotoPath(),imageViewPerson);
            firstLinearLayout.addView(imageViewPerson);

            LinearLayout secondLinearLayout = new LinearLayout(this);

            setButtonDelete(secondLinearLayout,person);

            setButtonEdit(secondLinearLayout,person);

            this.linearLayoutPeople.addView(firstLinearLayout);
            this.linearLayoutPeople.addView(secondLinearLayout);
        }


    }
    private void setButtonDelete(LinearLayout linearLayout, final Person person){
        final Button buttonDeletePerson = new Button(this);
        buttonDeletePerson.setText(getString(R.string.button_delete));
        buttonDeletePerson.setWidth(300);
        linearLayout.addView(buttonDeletePerson);
        buttonDeletePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File filePhoto = new File(person.getPhotoPath());
                filePhoto.delete();
                databasePeople.deletePerson(person.getId());
                clearPeopleList();
                toPeopleList();
                displayPeopleList();
            }
        });
    }

    private void setButtonEdit(LinearLayout linearLayout, final Person person){
        final Button buttonEditPerson = new Button(this);
        buttonEditPerson.setText(getString(R.string.button_edit));
        buttonEditPerson.setWidth(300);
        linearLayout.addView(buttonEditPerson);;
        buttonEditPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditPerson = new Intent(v.getContext(),AddPersonDetailsActivity.class);
                intentEditPerson.putExtra(INFO_WHAT_TO_DO,"EDIT");
                intentEditPerson.putExtra(EDIT_ID,person.getId());
                intentEditPerson.putExtra(EDIT_NAME,person.getName());
                intentEditPerson.putExtra(EDIT_SURNAME,person.getSurname());
                intentEditPerson.putExtra(EDIT_BIRTHDAY,person.getBirthDayDate());
                intentEditPerson.putExtra(EDIT_PHOTO_PATH,person.getPhotoPath());
                startActivity(intentEditPerson);
            }
        });
    }

    private void setPhoto(String photoAbsolutePath, ImageView imageViewPhoto){
        Matrix deviceMatrix = new Matrix();

        ExifInterface photoInterface = null;
        try {
            photoInterface = new ExifInterface(photoAbsolutePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int photoOrientation = photoInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,99);

        if(photoOrientation==3){
            deviceMatrix.postRotate(180);
        }
        else if(photoOrientation!=1){
            deviceMatrix.postRotate(90);
        }

        int photoWidthAndHeight = 200;


        Bitmap bitmapPicture = BitmapFactory.decodeFile(photoAbsolutePath);
        deviceMatrix.postScale((float)photoWidthAndHeight/bitmapPicture.getWidth(),(float)photoWidthAndHeight/bitmapPicture.getWidth());
        Bitmap processedBitmap = Bitmap.createBitmap(bitmapPicture , 0, 0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), deviceMatrix, true);
        imageViewPhoto.setImageBitmap(processedBitmap);
    }

    public void toChangeLanguageActivity(View view){
        Intent intentChangeLanguageActivity = new Intent(this,LanguageChangeActivity.class);
        startActivity(intentChangeLanguageActivity);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVE_CURRENT_LANGUAGE,language);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        language = savedInstanceState.getString(SAVE_CURRENT_LANGUAGE);
        if(language.equals("polski")){
            Log.i("Language",language);
            updateLocale("pl");
        }
        setLanguage();
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setLanguage(){
        buttonLanguage.setText(getString(R.string.button_main_activity_to_language_change_activity));
        buttonAddPerson.setText(getString(R.string.button_add_person_name));
    }

    private void setButtons(){
        buttonLanguage = (Button) findViewById(R.id.buttonLanguage);
        buttonAddPerson = (Button) findViewById(R.id.button_add_person);
    }

    private void updateLocale(String country)
    {
        Locale locale=new Locale(country);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
