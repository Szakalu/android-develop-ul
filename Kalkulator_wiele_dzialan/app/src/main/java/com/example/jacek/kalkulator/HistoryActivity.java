package com.example.jacek.kalkulator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {

    final static String CHECK_DELETE = "Check_Delete";
    String deleteHistory = "Nope";
    TextView textViewHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent intent = getIntent();
        String history = intent.getStringExtra("Extra_history");
        textViewHistory = (TextView) findViewById(R.id.textViewHistory);
        textViewHistory.setText(history);


    }

    public void backClick(View view){
        Log.i("Clicked button: ","Back");
        Intent intentMainActivity = new Intent();
        intentMainActivity.putExtra(CHECK_DELETE,deleteHistory);
        setResult(RESULT_OK,intentMainActivity);
        finish();
    }

    public void deleteHistory(View view){
        Log.i("Clicked button: ","Delete");
        deleteHistory = "delete";
        textViewHistory.setText("");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(CHECK_DELETE,deleteHistory);
        setResult(RESULT_OK,intent);
        finish();
    }
}
