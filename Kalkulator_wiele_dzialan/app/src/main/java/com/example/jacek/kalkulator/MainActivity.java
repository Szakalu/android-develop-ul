package com.example.jacek.kalkulator;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    final static int MY_REQUEST_ID = 1;
    final static String CHECK_DELETE = "Check_Delete";
    TextView textViewCalculator;
    StringBuffer textToDisplay;
    StringBuffer historyOfMathOperations;
    boolean firstNumberIsDone;
    List<String> mathSignsList = new ArrayList<>();
    Button buttonEquals;
    Button buttonPlus;
    Button buttonMinus;
    Button buttonMultiplication;
    Button buttonDivision;
    Button buttonDot;
    Button buttonZero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToDisplay = new StringBuffer();
        historyOfMathOperations = new StringBuffer();

        textViewCalculator = (TextView) findViewById(R.id.textViewCalculator);
        buttonEquals = (Button) findViewById(R.id.buttonEquals);
        buttonDot = (Button) findViewById(R.id.buttonDot);
        buttonZero = (Button) findViewById(R.id.buttonZero);

        firstNumberIsDone = false;

        initializeMathSignsButtons();
        addMathSignsToList(mathSignsList);
        disableEqualsButton();
        disableMathSignsButtons();
        enableMinusButton();
        disableDotButton();
    }

    public void addNumberToTextViewCalculator(View view) {
        if(!firstNumberIsDone){
            enableMathSignsButtons();
        }
        else{
            disableMathSignsButtons();
            enableEqualsButton();
        }

        Button button = (Button) view;
        textToDisplay.append(button.getText());
        displayText();

        checkIfAddingDotEnable(button);

        Log.i("Clicked button: ", button.getText().toString());
    }

    public void displayText() {
        textViewCalculator.setText(textToDisplay.toString());
    }

    public void addDotToTextViewCalculator(View view) {
        Button button = (Button) view;
        textToDisplay.append(button.getText());
        displayText();
        disableDotButton();
        disableMathSignsButtons();
        disableEqualsButton();
        enableButtonZero();
        Log.i("Clicked button: ", button.getText().toString());
    }

    public void clear(View view) {
        if (textToDisplay.length() > 0) {
            setVariablesSuchAsStartMainActivity();
            displayText();
        }
        Log.i("Clicked button: ", "Clear");
    }

    public void addMathSignToTextViewCalculator(View view) {
        Button button = (Button) view;
        if(buttonPlus.isEnabled()){
            disableMathSignsButtons();
            enableMinusButton();
            disableDotButton();
            firstNumberIsDone = true;
            enableButtonZero();
        }
        else if(buttonMinus.isEnabled()){
            disableMathSignsButtons();
        }
        textToDisplay.append(button.getText());
        displayText();
        Log.i("Clicked button: ", button.getText().toString());
    }

    public void clickEquals(View view) {

        String textToDisplayAfterEquals = textToDisplay.toString();

        StringBuffer textFirstNumber = new StringBuffer();
        StringBuffer textSecondNumber = new StringBuffer();
        Character mathOperationSign = ' ';

        setFirstNumber(textToDisplay,textFirstNumber);
        mathOperationSign = setOperationSign(mathOperationSign);
        setSecondNumber(textToDisplay,textSecondNumber);

        BigDecimal firstNumber = new BigDecimal(textFirstNumber.toString());
        BigDecimal secondNumber = new BigDecimal(textSecondNumber.toString());

        if(!zeroAfterDivisionCheck(mathOperationSign,secondNumber)){
            Log.i("Error: ", "Incorrect operation: " + textToDisplayAfterEquals);
            saveForHistory(textToDisplayAfterEquals, "Error");
        }
        else{

            firstNumber = checkForGoodZeroFormat(firstNumber);
            secondNumber = checkForGoodZeroFormat(secondNumber);

            BigDecimal resultOfMathOperation = makeMathOperation(firstNumber,secondNumber,mathOperationSign);

            saveForHistory(textToDisplayAfterEquals,resultOfMathOperation+"");

        }
        setVariablesSuchAsStartMainActivity();

    }

    public void sendHistory(View view) {
        Log.i("Clicked button: ", "History");
        Intent intentHistoryActivity = new Intent(this, HistoryActivity.class);
        intentHistoryActivity.putExtra("Extra_history", historyOfMathOperations.toString());
        startActivityForResult(intentHistoryActivity, MY_REQUEST_ID);
    }

    public void deleteHistory(View view) {
        if(historyOfMathOperations.length() > 0){
            historyOfMathOperations.delete(0, historyOfMathOperations.length());
        }
        Log.i("Clicked button: ", "Delete History");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                deleteHistory(data.getStringExtra(CHECK_DELETE));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteHistory(String string) {
        if (string.equals("delete")) {
            historyOfMathOperations.delete(0, historyOfMathOperations.length());
        }
    }

    private void disableEqualsButton() {
        buttonEquals.setEnabled(false);
    }

    private void enableEqualsButton() {
        buttonEquals.setEnabled(true);
    }

    private void disableMathSignsButtons(){
        buttonPlus.setEnabled(false);
        buttonMinus.setEnabled(false);
        buttonDivision.setEnabled(false);
        buttonMultiplication.setEnabled(false);
    }

    private void enableMathSignsButtons(){
        buttonPlus.setEnabled(true);
        buttonMinus.setEnabled(true);
        buttonDivision.setEnabled(true);
        buttonMultiplication.setEnabled(true);
    }

    private void enableMinusButton(){
        buttonMinus.setEnabled(true);
    }

    private void enableDotButton(){
        buttonDot.setEnabled(true);
    }

    private void disableDotButton(){
        buttonDot.setEnabled(false);
    }

    private void addMathSignsToList(List<String> mathSignList){
        mathSignList.add("+");
        mathSignList.add("-");
        mathSignList.add("/");
        mathSignList.add("*");
    }

    private void initializeMathSignsButtons(){
        buttonPlus = (Button) findViewById(R.id.buttonPlus);
        buttonMinus = (Button) findViewById(R.id.buttonMinus);
        buttonMultiplication = (Button) findViewById(R.id.buttonMultiplication);
        buttonDivision = (Button) findViewById(R.id.buttonDivision);
    }

    private void checkIfAddingDotEnable(Button button){
        String currentlyCheckedSign = "";
        int textToDisableLength = textToDisplay.length()-1;
        while(textToDisableLength>=0 && !mathSignsList.contains(currentlyCheckedSign)){
            if(currentlyCheckedSign.equals(".")){
                disableDotButton();
                break;
            }
            else{
                enableDotButton();
            }
            currentlyCheckedSign=textToDisplay.charAt(textToDisableLength)+"";
            textToDisableLength--;
        }
        disableUnlimitedZeroBeforeDot(button);
    }


    private boolean zeroAfterDivisionCheck(Character operationSign, BigDecimal secondNumber){
        if(operationSign.equals('/')){
            if(BigDecimal.ZERO.compareTo(secondNumber)==0){
                return false;
            }
        }
        return true;
    }

    private void setFirstNumber(StringBuffer textToDisplay, StringBuffer textFirstNumber){
        textFirstNumber.append(textToDisplay.charAt(0));
        textToDisplay.deleteCharAt(0);

        while(!mathSignsList.contains(textToDisplay.charAt(0) + "")){
            textFirstNumber.append(textToDisplay.charAt(0));
            textToDisplay.deleteCharAt(0);
        }
    }

    private void setSecondNumber(StringBuffer textToDisplay, StringBuffer textSecondNumber){
        textSecondNumber.append(textToDisplay.toString());
        textToDisplay.delete(0,textToDisplay.length());
    }

    private Character setOperationSign(Character operationSign){
        operationSign = textToDisplay.charAt(0);
        textToDisplay.deleteCharAt(0);
        return operationSign;
    }

    private BigDecimal makeMathOperation(BigDecimal firstNumber, BigDecimal secondNumber, Character operationSign){
        switch (operationSign){
            case '+':
                return firstNumber.add(secondNumber);
            case '-':
                return firstNumber.subtract(secondNumber);
            case '*':
                return firstNumber.multiply(secondNumber);
            case '/':
                return firstNumber.divide(secondNumber);
        }
        return new BigDecimal("0.0");
    }

    private void setVariablesSuchAsStartMainActivity(){
        textToDisplay.delete(0,textToDisplay.length());
        disableEqualsButton();
        disableMathSignsButtons();
        enableMinusButton();
        disableDotButton();
        enableButtonZero();
        firstNumberIsDone = false;
    }

    private void saveForHistory(String textToDisplayAfterEquals, String resultOfOperation){
        textToDisplay.append(textToDisplayAfterEquals + " = " + resultOfOperation);
        displayText();
        historyOfMathOperations.append(textToDisplay.toString() + System.lineSeparator() + System.lineSeparator());
    }

    private void disableButtonZero(){
        buttonZero.setEnabled(false);
    }

    private void enableButtonZero(){
        buttonZero.setEnabled(true);
    }

    private void disableUnlimitedZeroBeforeDot(Button button){
        if(button.getText().equals("0")){
            if(textToDisplay.length()==1){
                disableButtonZero();
            }
            else if(mathSignsList.contains(textToDisplay.charAt(textToDisplay.length()-2)+"")){
                disableButtonZero();
            }
        }
    }

    private BigDecimal checkForGoodZeroFormat(BigDecimal firstOrSecondNumber){
        if(BigDecimal.ZERO.compareTo(firstOrSecondNumber)==0){
            firstOrSecondNumber = new BigDecimal("0");
        }
        return  firstOrSecondNumber;
    }
}
