/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.util.HashMap;

/**
 *
 * @author jabar
 */
public class exercises {
//These variables will exist in all table accessores

    private final String tableName = "exercises";

//These variables are specific to this table
    private String exercise;
    private String description;
    private String output;
    private String input;

    public exercises() {
        setDefaults();
        //createTable();
    }

    public exercises(String exercise) {
        setDefaults();
        //createTable();
    }

    private void setDefaults() {
        exercise = "";
        description = "";
        output = "";
        input = "";
    }

    public final boolean createTable() {
        SQLConnector sql = new SQLConnector();
        Boolean exists = true;
        if (!sql.tableExists(tableName)) {
            HashMap<String, String> columns = new HashMap<>();
            columns.put("exercise", "varchar(100)");
            columns.put("description", "text");
            columns.put("output", "text");
            columns.put("input", "text");

            Boolean sc = sql.createTable(columns, tableName, "exercise");
            System.out.println("create table status: " + sc);
            System.out.println("create table error: " + sql.getError());
            exists = false;
        }
        return exists;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
    
    @Override
    public String toString(){
        String str = "exercise - " + this.exercise +
                "\ndescription - " + this.description +
                "\noutput - " + this.output +
                "\ninput - " + this.input;    
        return str;
    }

}
