package com.example.respiratorapp;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Represents results from running a test.
 */
public class TestResults {


    /** The string representing the testID
     * @note Takes on the format YYYY-MM-DD@HOUR:MINUTE:SECONDS
     *       ex. 2017-01-23@00:01:14.341
     */
    private String testID;

    /** The average heart rate (beats per minute,
     *  average respiratory frequencies (Hz),
     *  average blood oxygen levels
     */
    private int avgHR, avgRR, avgB02;

    /**
     * The risk assessments
     */
    private RiskAssessment overallRisk;
    private HR_RiskAssessment hrRisk;
    private RR_RiskAssessment rrRisk;
    private B02_RiskAssessment bo2Risk;

    private String infoString;

    /** The data type representing the overall risk assessment */
    protected enum RiskAssessment {
        LOW,
        MODERATE,
        HIGH
    }

    /** The data type representing the heart rate risk assessment */
    protected enum HR_RiskAssessment {
        LOW,
        MODERATE,
        HIGH
    }

    /** The data type representing the respiratory rate risk assessment */
    protected enum RR_RiskAssessment {
        LOW,
        MODERATE,
        HIGH
    }

    /** The data type representing the blood oxygen risk assessment */
    protected enum B02_RiskAssessment {
        LOW,
        MODERATE,
        HIGH
    }

    /**
     * Constructor used for creating a new test results.
     * @param hr The average heart rate
     * @param rr The average respiratory rate
     * @param bo2 The average B02
     * @param risk The risk assessment
     * @param hrRisk The hr risk assessment
     * @param rrRisk The rr risk assessment
     * @param bo2RiskAssessment The b02 risk assessment
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public TestResults (int hr, int rr, int bo2, RiskAssessment risk, HR_RiskAssessment hrRisk,
                        RR_RiskAssessment rrRisk, B02_RiskAssessment bo2RiskAssessment) {
        Random randNumGen = new Random();

        // create testID with denoted format
        this.testID = java.time.LocalDate.now().toString() + "@" + java.time.LocalTime.now().toString();

        avgHR = hr;
        avgRR = rr;
        avgB02 = bo2;
        overallRisk = risk;
        this.hrRisk = hrRisk;
        this.rrRisk = rrRisk;
        bo2Risk = bo2RiskAssessment;

    }

    /**
     * Constructor used for pulling an old test.
     * @param TestResultsString the string representing the test,
     *                          to be obtained by the retrieveTestResults method.
     */
    public TestResults (String TestResultsString) {
        String delim = "[ ]+";

        String[] info = TestResultsString.split(delim);
        if (info.length != 8){
            // error handle
            return;
        }

        testID = info[0];
        avgHR = Integer.parseInt(info[1]);
        avgRR = Integer.parseInt(info[2]);
        avgB02 = Integer.parseInt(info[3]);

        switch (info[4]) {
            case "LOW":
                overallRisk = RiskAssessment.LOW;
                break;
            case "MODERATE":
                overallRisk = RiskAssessment.MODERATE;
                break;
            case "HIGH":
                overallRisk = RiskAssessment.HIGH;
                break;
        }

        switch (info[5]) {
            case "LOW":
                hrRisk = HR_RiskAssessment.LOW;
                break;
            case "MODERATE":
                hrRisk = HR_RiskAssessment.MODERATE;
                break;
            case "HIGH":
                hrRisk = HR_RiskAssessment.HIGH;
                break;
        }

        switch (info[6]) {
            case "LOW":
                rrRisk = RR_RiskAssessment.LOW;
                break;
            case "MODERATE":
                rrRisk = RR_RiskAssessment.MODERATE;
                break;
            case "HIGH":
                rrRisk = RR_RiskAssessment.HIGH;
                break;
        }
        switch (info[7].trim()) {
            case "LOW":
                bo2Risk = B02_RiskAssessment.LOW;
                break;
            case "MODERATE":
                bo2Risk = B02_RiskAssessment.MODERATE;
                break;
            case "HIGH":
                bo2Risk = B02_RiskAssessment.HIGH;
                break;
        }
    }

    /**
     * Saves this TestResults information to phone storage.
     * @param context The Activity Context calling this method.
     */
    public void saveTestResults(Context context) {
        String filename = testID;
        String fileContents = this.toString();
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        }
        catch (IOException e) {
        }
    }

    /**
     * Retrieves the String contents of a particular TestResults from saved data.
     * @param context The context calling this method.
     * @param id The String of test ID to retrieve.
     * @return The String TestResults String.
     * @throws FileNotFoundException If file does not exist
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String retrieveTestResults(Context context, String id) throws FileNotFoundException {
        FileInputStream fis = context.openFileInput(id);
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            return "";
            // Error occurred when opening raw file for reading.
        } finally {
            String contents = stringBuilder.toString();
            return contents;
        }
    }

    /**
     * Getters for class fields
     * @return The corresponding field.
     */
    public RiskAssessment getOverallRisk(){ return overallRisk; }
    public HR_RiskAssessment getHrRisk() { return hrRisk; }
    public RR_RiskAssessment getRrRisk() { return rrRisk; }
    public B02_RiskAssessment getBo2Risk() { return bo2Risk; }
    public int getAvgHR(){ return avgHR; }
    public int getAvgRR() { return avgRR; }
    public int getAvgB02() { return avgB02; }
    public String getTestID() { return testID; }

    @Override
    public String toString() {
        return  testID +
                " " + avgHR +
                " " + avgRR +
                " " + avgB02 +
                " " + overallRisk +
                " " + hrRisk +
                " " + rrRisk +
                " " + bo2Risk;
    }
}
