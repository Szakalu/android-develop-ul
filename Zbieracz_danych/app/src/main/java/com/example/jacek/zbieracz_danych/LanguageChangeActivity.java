package com.example.jacek.zbieracz_danych;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class LanguageChangeActivity extends AppCompatActivity {

    private final static String SAVE_CURRENT_LANGUAGE = "saved_current_language";

    String language;

    private String currentLanguage;

    Button buttonPolish;
    Button buttonBack;
    Button buttonEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_change);
        buttonPolish = (Button) findViewById(R.id.buttonPolish);
        buttonBack = (Button) findViewById(R.id.buttonBackToMainActivity);
        buttonEnglish = (Button) findViewById(R.id.buttonEnglish);
        currentLanguage = Locale.getDefault().getDisplayLanguage();
    }

    public void clickChangeLanguageToPolish(View view) {
        language = "pl";
        updateLocale();
        buttonPolish.setText(getString(R.string.button_language_change_activity_polish));
        buttonBack.setText(getString(R.string.button_language_change_activity_back));
        buttonEnglish.setText(getString(R.string.button_language_change_activity_english));
        currentLanguage = Locale.getDefault().getDisplayLanguage();
    }

    public void clickChangeLanguageToEnglish(View view) {
        language = "en";
        updateLocale();
        buttonPolish.setText(getString(R.string.button_language_change_activity_polish));
        buttonBack.setText(getString(R.string.button_language_change_activity_back));
        buttonEnglish.setText(getString(R.string.button_language_change_activity_english));
        currentLanguage = Locale.getDefault().getDisplayLanguage();
    }

    private void updateLocale()
    {
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void backToMainActivity(View view){
        Intent intentMainActivity = new Intent(this,MainActivity.class);
        startActivity(intentMainActivity);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVE_CURRENT_LANGUAGE,currentLanguage);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        currentLanguage = savedInstanceState.getString(SAVE_CURRENT_LANGUAGE);
        if(currentLanguage.equals("polski")){
            language = "pl";
            updateLocale();
        }
        buttonPolish.setText(getString(R.string.button_language_change_activity_polish));
        buttonBack.setText(getString(R.string.button_language_change_activity_back));
        buttonEnglish.setText(getString(R.string.button_language_change_activity_english));
        super.onRestoreInstanceState(savedInstanceState);
    }
}
