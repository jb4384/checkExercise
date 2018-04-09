/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

/**
 *
 * @author Tiffany
 */
@Named(value = "exercises")
@ManagedBean
@ApplicationScoped
public class exercises {

    private String header1;
    private String selectedName = "01";
    private String selectedExercise = "";
    Set<Integer> chapters;
    private List<SelectItem> names;
    private List<SelectItem> exes;
    private List<File> files;
    private String program;
    private String output;
    private String input;
    private String inputDisplay;
    private String webinf;
    private String ags10e;
    private String fileInfo;
    private Boolean hide;

    @PostConstruct
    public void init() {
        webinf = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF");
        ags10e = new File(webinf + "/../../../ags10e").toString();
        buildChapters();
        updateExes();
        header1 = selectedExercise;
        updateProgram();
    }

    public void buildChapters() {
        names = new ArrayList<>();
        chapters = new TreeSet<>();
        buildFiles("/exercisedescription/", "");
        files.forEach((File file) -> {
            String ch = file.getName().split("_")[0].trim().replaceAll("[^0-9]", "");
            chapters.add(Integer.parseInt(ch));
        });
        chapters.forEach(ch -> {
            names.add(new SelectItem("" + ch, "Chapter " + ch));
        });
    }

    public void updateExes() {
        exes = new ArrayList<>();
        buildFiles("/exercisedescription/", "Exercise" + selectedName);
        files.forEach((File file) -> {
            if (selectedExercise.isEmpty()) {
                selectedExercise = file.getName();
            }
            exes.add(new SelectItem("" + file.getName(), file.getName()));
        });
    }

    public void buildFiles(String endPath, String startsWith) {
        try {
            System.out.println(startsWith);
            System.out.println(endPath);
            files = Files.walk(Paths.get(ags10e + endPath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> file.getName().startsWith(startsWith))
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            System.out.println("failed");
        }
    }

    public String getHeader1() {
        return header1;
    }

    public void setHeader1(String header1) {
        this.header1 = header1;
        updateExes();
    }

    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    public void changeSelectedName(ValueChangeEvent event) {
        selectedName = event.getNewValue().toString();
        if (selectedName.length() < 2) {
            selectedName = "0" + selectedName;
        }
        updateExes();
    }

    public String getSelectedExercise() {
        return selectedExercise;
    }

    public void setSelectedExercise(String selectedExercise) {
        this.selectedExercise = selectedExercise;
    }

    public void changeSelectedExercise(ValueChangeEvent event) {
        selectedExercise = event.getNewValue().toString();
    }

    public List<SelectItem> getNames() {
        return names;
    }

    public List<SelectItem> getExes() {
        return exes;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    private void updateProgram() {
        program = "";
        String fileName = ags10e + "/exercisedescription/" + header1;
        parseFile(fileName);
        if (fileInfo.replaceAll("[^A-Za-z ]", "").trim().equalsIgnoreCase("programming exercise")) {
            program = "/* Paste your " + header1 + " here and click Automatic Check.\n"
                    + "For all programming projects, the numbers should be double\n"
                    + "unless it is explicitly stated as integer.\n"
                    + "If you get a java.util.InputMismatchException error, check if\n"
                    + "your code used input.readInt(), but it should be input.readDouble().\n"
                    + "For integers, use int unless it is explicitly stated as long. */";
        } else if (fileInfo.equalsIgnoreCase("This exercise can be compiled and submitted, but cannot be run and automatically graded.")) {
            program = "/* This exercise cannot be graded automatically because it may use random\n"
                    + "numbers, file input/output, or graphics. */";
        } else {
            program = "/* " + fileInfo + " */";
        }

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

    public String getInputDisplay() {
        return inputDisplay;
    }

    public void setInputDisplay(String inputDisplay) {
        this.inputDisplay = inputDisplay;
    }

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    private void updateInfo() {
        output = "";
        input = "";
        inputDisplay = "";
        System.out.println("update file info");
        System.out.println();
        System.out.println();
        buildFiles("/gradeexercise/", header1);
        files.forEach((File file) -> {
            String fileName = file.getAbsolutePath();
            Boolean cntn = true;
            if (!header1.endsWith("Extra") && fileName.contains("Extra")) {
                cntn = false;
            }
            if (cntn) {
                System.out.println(fileName);
                parseFile(fileName);
                if (fileName.endsWith("output")) {
                    if (!output.isEmpty()) {
                        output += "#";
                    }
                    output += fileInfo;
                }
                if (fileName.endsWith("input")) {
                    if (!input.isEmpty()) {
                        input += " ";
                    }
                    input += fileInfo;
                    if (inputDisplay.equals("")) inputDisplay += fileInfo;
                    hide = true;
                }
            }

        });
        System.out.println("output: " + output);
        System.out.println("input: " + input);
        if (input.isEmpty()) {
            hide = false;
        }
    }

    private void parseFile(String fileName) {
        fileInfo = "";
        File f = new File(fileName);
        if (f.exists() && !f.isDirectory()) {
            try (Stream<String> lines = Files.lines(Paths.get(fileName), StandardCharsets.ISO_8859_1)) {
                lines.forEach((String line) -> {
                    line = line.replaceAll("[\\u2018\\u2019]", "'")
                            .replaceAll("[\\u201C\\u201D]", "\"");
                    if (!fileInfo.isEmpty()) {
                        fileInfo += "\n";
                    }
                    fileInfo += line;
                });
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid file program build - " + fileName);
        }
    }

    public void submit() {
        header1 = selectedExercise;
        updateProgram();
        updateInfo();
        
    }
}
