package com.example.jacek.zbieracz_danych;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class AddPersonDetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText editTextName;
    private EditText editTextSurname;
    private TextView textViewBirthday;
    private int currentlyDay, currentlyMonth, currentlyYear;
    private int pickedDay, pickedMonth, pickedYear;

    private final static String EXTRA_NAME = "extra_name";
    private final static String EXTRA_SURNAME = "extra_surname";
    private final static String EXTRA_BIRTHDAY_DATE = "extra_birthday_date";

    private final static String SAVE_NAME = "saved_name";
    private final static String SAVE_SURNAME = "saved_surname";
    private final static String SAVE_BIRTHDAY = "saved_birthday";
    private final static String SAVE_PICKED_YEAR = "saved_picked_year";
    private final static String SAVE_PICKED_MONTH = "saved_picked_month";
    private final static String SAVE_PICKED_DAY = "saved_picked_day";
    private final static String SAVE_CURRENT_LANGUAGE = "saved_current_language";

    private final static String INFO_WHAT_TO_DO = "info_what_to_do";
    private final static String EDIT_ID = "edit_id";
    private final static String EDIT_NAME = "edit_name";
    private final static String EDIT_SURNAME = "edit_surname";
    private final static String EDIT_BIRTHDAY = "edit_birthday";
    private final static String EDIT_PHOTO_PATH = "edit_photo_path";
    private Intent intentMainActivity;

    private String language;

    private Button buttonExit;
    private Button buttonNext;
    private Button buttonPickDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person_details);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextSurname = (EditText) findViewById(R.id.editTextSurname);
        textViewBirthday = (TextView) findViewById(R.id.textViewBirthday);
        setCurrentlyDate();
        intentMainActivity = getIntent();
        checkIfEditPerson(intentMainActivity);
        language = Locale.getDefault().getDisplayLanguage();
        setButtons();
    }

    private void checkIfEditPerson(Intent intentMainActivity){

        if((intentMainActivity.getStringExtra(INFO_WHAT_TO_DO)).equals("EDIT")){
            editTextName.setText(intentMainActivity.getStringExtra(EDIT_NAME));
            editTextSurname.setText(intentMainActivity.getStringExtra(EDIT_SURNAME));
            textViewBirthday.setText(intentMainActivity.getStringExtra(EDIT_BIRTHDAY));
            StringBuffer birthDayDateBuffer = new StringBuffer(intentMainActivity.getStringExtra(EDIT_BIRTHDAY));
            pickedYear = setPickedYearAndCutFirstSlash(birthDayDateBuffer);
            pickedMonth = setPickedMonthAndCutSecondSlash(birthDayDateBuffer);
            pickedDay = setPickedDay(birthDayDateBuffer);
        }
    }

    private int setPickedYearAndCutFirstSlash(StringBuffer birthDayDateBuffer){
        String birthdayYear = birthDayDateBuffer.substring(0,4);
        birthDayDateBuffer.delete(0,5);
        return Integer.parseInt(birthdayYear);
    }

    private int setPickedMonthAndCutSecondSlash(StringBuffer birthDayDateBuffer){
        StringBuffer birthdayMonth = new StringBuffer();
        birthdayMonth.append(birthDayDateBuffer.charAt(0));
        birthDayDateBuffer.deleteCharAt(0);
        if(birthDayDateBuffer.charAt(0) != '/'){
            birthdayMonth.append(birthDayDateBuffer.charAt(0));
            birthDayDateBuffer.deleteCharAt(0);
        }
        birthDayDateBuffer.deleteCharAt(0);
        return Integer.parseInt(birthdayMonth.toString());
    }

    private int setPickedDay(StringBuffer birthDayDateBuffer){
        String birthdayDay = birthDayDateBuffer.substring(0,birthDayDateBuffer.length());
        return Integer.parseInt(birthdayDay);
    }


    public void toPhoto(View view){
        if(checkPersonDetails(editTextName.getText().toString()
                , editTextSurname.getText().toString()
                , textViewBirthday.getText().toString())){
            Intent intentPhotoActivity = new Intent(this,AddPersonPhotoActivity.class);
            intentPhotoActivity.putExtra(INFO_WHAT_TO_DO,intentMainActivity.getStringExtra(INFO_WHAT_TO_DO));
            intentPhotoActivity.putExtra(EXTRA_NAME,editTextName.getText().toString());
            intentPhotoActivity.putExtra(EXTRA_SURNAME,editTextSurname.getText().toString());
            intentPhotoActivity.putExtra(EXTRA_BIRTHDAY_DATE,textViewBirthday.getText().toString());

            if(intentMainActivity.getStringExtra(INFO_WHAT_TO_DO).equals("EDIT")){
                intentPhotoActivity.putExtra(EDIT_ID,intentMainActivity.getIntExtra(EDIT_ID,0));
                intentPhotoActivity.putExtra(EDIT_PHOTO_PATH,intentMainActivity.getStringExtra(EDIT_PHOTO_PATH));
            }

            startActivity(intentPhotoActivity);
            finish();
        }
    }

    private boolean checkPersonDetails(String name, String surname, String birthday){
        int controlCountDetails = 0;
        if(!name.equals("") && name.length() <= 20){
            controlCountDetails++;
        }
        if(!surname.equals("") && surname.length() <= 20){
            controlCountDetails++;
        }
        if(checkBirthdayIsBeforeToday(birthday)){
            controlCountDetails++;
        }
        if(controlCountDetails==3){
            return true;
        }
        return false;
    }

    public void clickExit(View view){
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        pickedYear = year;
        pickedMonth = month + 1;
        pickedDay = day;

        textViewBirthday.setText(pickedYear + "/" + pickedMonth + "/" + pickedDay);
    }

    private void setCurrentlyDate(){
        Calendar calendar = Calendar.getInstance();
        currentlyYear = calendar.get(Calendar.YEAR);
        currentlyMonth = calendar.get(Calendar.MONTH);
        currentlyDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void pickBirthdayDate(View view){
        if(textViewBirthday.getText().toString().equals("")){
            DatePickerDialog datePickerDialogTakeBirthday = new DatePickerDialog(this,this, currentlyYear, currentlyMonth, currentlyDay);
            datePickerDialogTakeBirthday.show();
        }
        else{
            DatePickerDialog datePickerDialogTakeBirthday = new DatePickerDialog(this,this, pickedYear, pickedMonth-1, pickedDay);
            datePickerDialogTakeBirthday.show();
        }
    }

    private boolean checkBirthdayIsBeforeToday(String birthday){
        if(!birthday.equals("")){

            int controlCountBirthday=0;

            if(checkBirthdayYear()){
                controlCountBirthday++;
            }
            if(checkBirthdayMonth()){
                controlCountBirthday++;
            }
            if(checkBirthdayDay()){
                controlCountBirthday++;
            }
            if(controlCountBirthday==3){
                return true;
            }

        }
        return false;
    }

    private boolean checkBirthdayMonth(){
        if(pickedYear==currentlyYear){
            if(pickedMonth>currentlyMonth+1){
                return false;
            }
        }
        return true;
    }

    private boolean checkBirthdayDay(){
        if(checkBirthdayMonth()){
            if(pickedYear==currentlyYear && pickedMonth==currentlyMonth+1){
                if(pickedDay> currentlyDay){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkBirthdayYear(){
        if(checkBirthdayMonth()){
            if(pickedYear>currentlyYear ){
                return false;
            }
            if(currentlyYear-pickedYear>120){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVE_NAME,editTextName.getText().toString());
        outState.putString(SAVE_SURNAME,editTextSurname.getText().toString());
        outState.putString(SAVE_BIRTHDAY,textViewBirthday.getText().toString());
        outState.putInt(SAVE_PICKED_YEAR,pickedYear);
        outState.putInt(SAVE_PICKED_MONTH,pickedMonth);
        outState.putInt(SAVE_PICKED_DAY,pickedDay);
        outState.putString(SAVE_CURRENT_LANGUAGE,language);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        editTextName.setText(savedInstanceState.getString(SAVE_NAME));
        editTextSurname.setText(savedInstanceState.getString(SAVE_SURNAME));
        textViewBirthday.setText(savedInstanceState.getString(SAVE_BIRTHDAY));
        pickedYear = savedInstanceState.getInt(SAVE_PICKED_YEAR);
        pickedMonth = savedInstanceState.getInt(SAVE_PICKED_MONTH);
        pickedDay = savedInstanceState.getInt(SAVE_PICKED_DAY);
        language = savedInstanceState.getString(SAVE_CURRENT_LANGUAGE);
        if(language.equals("polski")){
            updateLocale("pl");
        }
        setLanguage();
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setLanguage(){
        buttonNext.setText(getString(R.string.button_next));
        buttonExit.setText(getString(R.string.button_exit));
        buttonPickDate.setText(getString(R.string.pick_date_birthday));
        editTextName.setHint(getString(R.string.hint_first_name));
        editTextSurname.setHint(getString(R.string.hint_surname));
    }

    private void setButtons(){
        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonPickDate = (Button) findViewById(R.id.buttonPickDate);
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
