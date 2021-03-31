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
import java.util.List;

/**
 * @class Represents a typical user of the UGA Respiratory Risk Assessment App.
 */
public class RespiratoryUser {

    /**
     * The user name and password for this user
     */
    private String username, password;

    /**
     * The first and last name of a user, separated by a whitespace
     */
    private String name;

    /**
     * The biological sex of this user
     */
    private Sex sex;

    /**
     * The activity level of this user
     */
    private ActivityLevel activityLevel;

    /**
     * This lists of tests (identified by test ID) this user has completed
     */
    private List<String> testResultsList;

    /**
     * The age, height, and weight for this user
     *
     * @note Age in years, height in centimeters, weight in kilograms.
     */
    private int age, height, weight;

    /**
     * Represents biological sexes a user can have
     */
    protected enum Sex {
        MALE,
        FEMALE
    }

    /**
     * Represents an activity level a user can have
     */
    protected enum ActivityLevel {
        LOW,
        MODERATE,
        HIGH
    }

    /**
     * Constructor used to create a new user.
     *
     * @param username      The username
     * @param password      The password
     * @param name          The first and last name
     * @param sex           The biological sex
     * @param activityLevel The activity level
     * @param age           The age (years)
     * @param height        The height (cm)
     * @param weight        The weight (kg)
     */
    RespiratoryUser(String username, String password, String name, Sex sex, ActivityLevel activityLevel, int age, int height, int weight) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.activityLevel = activityLevel;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    /**
     * Constructor for retrieving an already existing user
     *
     * @param RespiratoryUserString The string representing the Respiratory User,
     *                              to be obtained by the retrieveUser method.
     */
    public RespiratoryUser(String RespiratoryUserString) {
        String delim = "[ ,]+";

        String[] info = RespiratoryUserString.split(delim);

        username = info[0];
        password = info[1];
        name = info[2];
        age = Integer.parseInt(info[5]);
        height = Integer.parseInt(info[6]);
        weight = Integer.parseInt(info[7]);

        switch (info[3]) {
            case "MALE":
                sex = Sex.MALE;
                break;
            case "FEMALE":
                sex = Sex.FEMALE;
                break;
        }

        switch (info[4]) {
            case "LOW":
                activityLevel = ActivityLevel.LOW;
                break;
            case "MODERATE":
                activityLevel = ActivityLevel.MODERATE;
                break;
            case "HIGH":
                activityLevel = ActivityLevel.HIGH;
                break;
        }

        int start = RespiratoryUserString.indexOf('[');
        int end = RespiratoryUserString.indexOf(']');

        // if the testResultsList is empty, start and end should be -1
        if (start != -1 && end != -1) {
            String testListString = RespiratoryUserString.substring(start + 1, end);
            delim = "[ ,]+";
            info = testListString.split(delim);

            for (int i = 0; i < info.length; i++) {
                testResultsList.add(info[i]);
            }
        }

    }

    /**
     * Saves this User information to phone storage.
     *
     * @param context The Activity Context calling this method.
     * @throws IOException Will throw this on file creation error
     */
    public void saveUser(Context context) throws IOException {
        String filename = username;
        String fileContents = this.toString();
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Retrieves the String contents of a particular RespiratoryUser from saved data.
     *
     * @param context  The context calling this method.
     * @param username The username of the RepsiratoryUser to retrieve
     * @return The String RespiratoryUserString
     * @throws FileNotFoundException If file does not exist
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String retrieveUser(Context context, String username) throws FileNotFoundException {
        FileInputStream fis = context.openFileInput(username);
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("");
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
     * Registers a test results object to this user
     *
     * @param id
     */
    public void addTestResult(String id) {
        testResultsList.add(id);
    }

    @Override
    public String toString() {
        if (testResultsList == null) {
            return username + " " + password + " " + name + " " + sex +
                    " " + activityLevel + " " + age + " " + height + " " + weight;
        } else {
            return username + " " + password + " " + name + " " + sex +
                    " " + activityLevel + " " + age + " " + height + " " + weight + " " + testResultsList;
        }
    }

    /**
     * Getters for class fields.
     *
     * @return The corresponding field.
     */
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public Sex getSex() {
        return sex;
    }

    public List<String> getTestResultsList() {
        return testResultsList;
    }


}
