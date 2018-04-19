/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author jabar
 */
public class FileParse {

    private static List<exercises> items;
    private static String text;

    public static void main(String[] args) {
        SQLConnector sql = new SQLConnector();
        sql.createDatabase();
        new exercises().createTable();
        
        if (1==1) System.exit(0);
        items = new ArrayList<>();
        if (!(new exercises().createTable())) {
            System.out.println("Table Created");
            String path = "C:\\selftest\\";
            try {
                //gradeexercise - this contains the input and output
                // parsing needs to be based from here
                //exercisedescription - contains the base description
                List<File> files = Files.walk(Paths.get(path + "gradeexercise"))
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".output"))
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                files.forEach((File file) -> {
                    //new FileParser(file.getAbsolutePath()).start();
                    exercises ex = new exercises();
                    String outfile = file.getName();
                    String baseName = outfile.replace(".output", "");
                    ex.setExercise(baseName);
                    String infile = baseName + ".input";
                    //parse end characters
                    if (!baseName.matches(".*\\d") && !baseName.endsWith("Extra")) {
                        baseName = baseName.substring(0, baseName.length() - 1);
                    }
                    ex.setDescription(parseFile(path + "exercisedescription\\" + baseName));
                    ex.setOutput(parseFile(path + "gradeexercise\\" + outfile));
                    ex.setInput(parseFile(path + "gradeexercise\\" + infile));
                    System.out.println(ex.toString());
                });
            } catch (IOException ex) {
            }
        } else {
            System.out.println("Table Creation Failed!");
        }
    }

    public static String parseFile(String fileName) {
        File f = new File(fileName);
        text = "";
        if ((f.exists() && !f.isDirectory())) {
            try (Stream<String> lines = Files.lines(Paths.get(fileName), StandardCharsets.ISO_8859_1)
                    .filter(line -> !line.isEmpty())) {
                lines.forEach((String line) -> {
                    if (!text.isEmpty()) text += "\n";
                    text += line;
                });
            } catch (IOException ex) {
            }
        }
        return text;
    }
}
