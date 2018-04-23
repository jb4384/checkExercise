/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
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
    private String correct; //Holds the string if the program is correct or not.
    private String compile; //Holds the string from compiler response
    private Boolean hide; //This hides 
    private Boolean otherHide;
    private Boolean checkHide; //Hide the Automatic Check button. If gradeable = true, else = false;
    private Boolean resultHide;
    private Boolean appearWhenAutomaticCheck; //Renders message if Automatic Check produces the correct result after you click Automatic Check. 
    private Boolean matches;
    private Boolean showInputHide;
    private Boolean showOutputHide;
    String compareString;

    final static int EXECUTION_TIME_ALLOWED = 1000;
    final static int EXECUTION_TIME_INTERVAL = 100;

    @PostConstruct
    public void init() {
        //webinf = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF");
        //ags10e = new File(webinf + "/../../../ags10e").toString() + "\\";
        ags10e = "c:\\ags10e\\";
        buildChapters();
        updateExes();
        header1 = selectedExercise;
        updateProgram();
        updateInfo();
        hide = false;
        appearWhenAutomaticCheck = false;
    }

    public void buildChapters() {
        names = new ArrayList<>();
        chapters = new TreeSet<>();
        buildFiles("exercisedescription/", "");
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
        buildFiles("exercisedescription/", "Exercise" + selectedName);
        files.forEach((File file) -> {
            if (selectedExercise.isEmpty()) {
                selectedExercise = file.getName();
            }
            exes.add(new SelectItem("" + file.getName(), file.getName()));
        });
    }

    public void buildFiles(String endPath, String startsWith) {
        try {
            files = Files.walk(Paths.get(ags10e + endPath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> file.getName().startsWith(startsWith))
                    .collect(Collectors.toList());
        } catch (IOException ex) {
        }
    }

    public void changeSelectedName(ValueChangeEvent event) {
        selectedName = event.getNewValue().toString();
        if (selectedName.length() < 2) {
            selectedName = "0" + selectedName;
        }
        updateExes();
    }

    private void updateProgram() {
        checkHide = true;
        resultHide = false;
        program = "";
        String fileName = ags10e + "exercisedescription/" + header1;
        parseFile(fileName);
        if (fileInfo.replaceAll("[^A-Za-z ]", "").trim().equalsIgnoreCase("programming exercise")
                || fileInfo.startsWith("Programming Exercise")
                || fileInfo.startsWith("This is an extra exercise")) {
            program = "/* Paste your " + header1 + " here and click Automatic Check.\n"
                    + "For all programming projects, the numbers should be double\n"
                    + "unless it is explicitly stated as integer.\n"
                    + "If you get a java.util.InputMismatchException error, check if\n"
                    + "your code used input.readInt(), but it should be input.readDouble().\n"
                    + "For integers, use int unless it is explicitly stated as long. */";
        } else if (fileInfo.equalsIgnoreCase("This exercise can be compiled and submitted, but cannot be run and automatically graded.")) {
            program = "/* This exercise cannot be graded automatically because it may use random\n"
                    + "numbers, file input/output, or graphics. */";
            checkHide = false;
        } else {
            program = "/* " + fileInfo + " */";
        }

    }

    private void updateInfo() {
        checkHide = true; //display automatic check
        output = "";
        input = "";
        inputDisplay = "";
        buildFiles("gradeexercise/", header1);
        files.forEach((File file) -> {
            String fileName = file.getAbsolutePath();
            Boolean cntn = true;
            if (!header1.endsWith("Extra") && fileName.contains("Extra")) {
                cntn = false;
            }
            if (cntn) {
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
                    if (inputDisplay.equals("")) {
                        inputDisplay += fileInfo;
                    }
                    hide = true; // show input display box
                    otherHide = false; //hide 
                } else if (program.equals("/* This exercise cannot be graded automatically because it may use random\n"
                        + "numbers, file input/output, or graphics. */")) {
                    //If it cannot be graded, hide Automatic Check
                    inputDisplay = "This exercise cannot be auto graded. But you can still run it.";
                    hide = false;
                    checkHide = false;
                    otherHide = true;
                }
            }

        });
        if (input.isEmpty()) {
            hide = false;
            checkHide = true;
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
            }
        } else {
        }
    }

    public void submit() {
        header1 = selectedExercise;
        appearWhenAutomaticCheck = false;
        showOutputHide = false;
        resultHide = true;
        updateProgram();
        updateInfo();
    }

    private void purgeDirectory(File dir) {
        for (File file : dir.listFiles()) {
            file.delete();
        }
    }

    public void buildProgram() {
        purgeDirectory(new File(ags10e + "\\run"));
        try {
            resultHide = true;
            //Hide == true -> Compile and execute program with input
            String path = ags10e + "\\run\\" + header1 + ".java";
            Files.createDirectories(Paths.get(ags10e + "\\run\\"));
            Files.write(Paths.get(path), program.getBytes(), StandardOpenOption.CREATE);
            Output output = compileProgram();

            //Build compile output string
            compile = "command> javac " + header1 + ".java\n";
            compile += output.error + "\n\n";

            if (output.error.equals("Compiled successful")) {
                // check for input
                String prefix = "a";
                String inputFile = ags10e + "\\gradeexercise\\" + header1 + prefix + ".input";
                Path p = Paths.get(inputFile);
                boolean notExists = Files.notExists(p);
                if (notExists) {
                    inputFile = "";
                    prefix = "";
                }
                String outputFile = ags10e + "\\run\\" + header1 + prefix + ".output";
                output = executeProgram(inputFile, outputFile);

                //Add Execute string
                compile += "command> java " + header1 + "\n";
                Scanner input = new Scanner(new File(outputFile));
                if (output.isInfiniteLoop) {
                    compile += "Your program takes too long. It runs out of the allowed CPU time 10000ms. It may have an infinite loop or the expected input for the program is not provided or provided incorrectly.";
                } else {
                    while (input.hasNextLine()) {
                        compile += input.nextLine() + "\n";
                    }
                }

            }
            compile += "\ncommand>\n";

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
//#################################################################################################################################
//#################################################################################################################################
//#################################################################################################################################

    public void checkProgram() {
        if (!output.isEmpty()) {
            output = output.replaceAll("\n", "#");
        }
        compareString = "";
        matches = true;
        //Build program first
        purgeDirectory(new File(ags10e + "\\run"));
        try {
            //Hide == true -> Compile and execute program with input
            String path = ags10e + "\\run\\" + header1 + ".java";
            Files.createDirectories(Paths.get(ags10e + "\\run\\"));
            Files.write(Paths.get(path), program.getBytes(), StandardOpenOption.CREATE);
            Output compiler = compileProgram();

            //Build compile output string
            compile = "command> javac " + header1 + ".java\n";
            compile += compiler.error + "\n\n";

            if (compiler.error.equals("Compiled successful")) {
                files = Files.walk(Paths.get(ags10e + "\\gradeexercise"))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(file -> file.getName().startsWith(header1))
                        .filter(file -> file.getName().endsWith("output"))
                        .collect(Collectors.toList());
                files.forEach((File file) -> {
                    String outputComp = file.getAbsolutePath();
                    Boolean cntn = true;
                    if (!header1.endsWith("Extra") && outputComp.contains("Extra")) {
                        cntn = false;
                    }
                    if (cntn && matches) {
                        try {
                            String outFileName = file.getName();
                            String inputFile = outputComp.replace(".output", ".input");
                            Path p = Paths.get(inputFile);
                            boolean notExists = Files.notExists(p);
                            if (notExists) {
                                inputFile = "";
                            }
                            String outputFile = ags10e + "\\run\\" + outFileName;
                            Output outputer = executeProgram(inputFile, outputFile);

                            //Add Execute string
                            compile += "command> java " + header1 + "\n";
                            //If there is an infinite loop, display this message. Else,
                            //continue with comparing results.
                            if (outputer.isInfiniteLoop) {
                                resultHide = true;
                                compile += "Your program takes too long. It runs out of the allowed CPU time 10000ms. It may have an infinite loop or the expected input for the program is not provided or provided incorrectly.";
                                matches = false;
                            } else {
                                Scanner input = new Scanner(new File(outputFile));
                                while (input.hasNextLine()) {
                                    String temp = input.nextLine();
                                    compareString += temp;
                                    compile += temp + "\n";
                                    if (input.hasNext()) {
                                        compareString += "#";
                                    }
                                }
                            }
                            compile += "\ncommand>\n";
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(exercises.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                String text = output.replaceAll("#", "");
                text = text.replaceAll(".", "");
                if (text.matches("[0-9]+")) {
                    String testComp[] = output.split("#");
                    for (int i = 0; i < testComp.length; i++){
                        if (!compareString.contains(testComp[i])) matches = false;
                    }
                } else {
                    matches = output.equals(compareString);
                }
            } else {
                matches = false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //Compare compile message with the output\
        appearWhenAutomaticCheck = true;
        if (matches) {
            correct = "Your program is correct.";
            resultHide = false;
            showOutputHide = false;
        } else {
            correct = "Your program is incorrect.";
            compareString = compareString.replaceAll("#", "\n");
            output = output.replaceAll("#", "\n");
            resultHide = false;
            showOutputHide = true;

        }
    }

    private Output compileProgram() {
        String command = "javac";
        String sourceDirectory = ags10e + "\\run\\";
        String program = header1 + ".java";
        final Output result = new Output();
        ProcessBuilder pb;
        try {

            pb = new ProcessBuilder(command, "-classpath", ".;c:\\book",
                    "-Xlint:unchecked", "-nowarn", "-XDignore.symbol.file", program);
            pb.directory(new File(sourceDirectory));
            long startTime = System.currentTimeMillis();
            Process proc = null;

            proc = pb.start();

            // This separate thread destroy the process if it takes too long time
            final Process proc1 = proc;

            new Thread() {
                public void run() {
                    Scanner scanner1 = new Scanner(proc1.getInputStream());

                    while (scanner1.hasNext()) {
                        result.output += scanner1.nextLine().replaceAll(" ", "&nbsp;") + "\n";
                        //  scanner1.close(); // You could have closed it too soon
                    }
                }
            }.start();

            new Thread() {
                public void run() {
                    // Process output from proc
                    Scanner scanner2 = new Scanner(proc1.getErrorStream());

                    while (scanner2.hasNext()) {
                        result.error += scanner2.nextLine() + "\n";
                    }
                    // scanner2.close(); // You could have closed it too soon
                }
            }.start();

            //Wait for the external process to finish
            int exitCode = proc.waitFor();

            result.output.replaceAll(" ", "&nbsp;");
            result.error.replaceAll(" ", "&nbsp;");

            // Ignore warnings
            if (result.error.indexOf("error") < 0) {
                result.error = "Compiled successful";
            }

//        if (result.error.indexOf("error") >= 0 || result.error.indexOf("Error") >= 0)
//          result.error = "";
            result.timeUsed = (int) (System.currentTimeMillis() - startTime);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;

    }

    private Output executeProgram(String inputFile, String outputFile) {
        String command = "java";
        String program = header1;
        String programDirectory = ags10e + "\\run\\";
        final Output result = new Output();
        ProcessBuilder pb;
        try {
            new File(outputFile).createNewFile();
            // For Java security, added c:/etext.policy in c:\program files\jre\bin\security\java.security
            pb = new ProcessBuilder(command, program);
            pb.directory(new File(programDirectory));
            pb.redirectErrorStream(true);
            if (inputFile.length() > 0) {
                pb.redirectInput(Redirect.from(new File(inputFile)));
            }

            pb.redirectOutput(Redirect.to(new File(outputFile)));
            long startTime = System.currentTimeMillis();
            Process proc = null;

            proc = pb.start();

            // This separate thread destroy the process if it takes too long time
            final Process proc1 = proc;

            new Thread() {
                public void run() {
                    int sleepTime = 0;
                    boolean isFinished = false;

                    while (sleepTime <= EXECUTION_TIME_ALLOWED && !isFinished) {
                        try {
                            Thread.sleep(EXECUTION_TIME_INTERVAL);
                            sleepTime += EXECUTION_TIME_INTERVAL;
                            if (!proc1.isAlive()) {
                                int exitValue = proc1.exitValue();
                                isFinished = true;
                            }
                        } catch (IllegalThreadStateException | InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (!isFinished) {
                        proc1.destroy();
                        result.isInfiniteLoop = true;
                    }
                }
            }.start();

            int exitCode = proc.waitFor();
            result.timeUsed = (int) (System.currentTimeMillis() - startTime);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }

        return result;

    }

    public static class Output {

        public String output = "";
        public String error = "";
        public boolean isInfiniteLoop = false;
        public int timeUsed = 100;
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

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public Boolean getAppearWhenAutomaticCheck() {
        return appearWhenAutomaticCheck;
    }

    public void setAppearWhenAutomaticCheck(Boolean appearWhenAutomaticCheck) {
        this.appearWhenAutomaticCheck = appearWhenAutomaticCheck;
    }

    public String getCompile() {
        return compile;
    }

    public void setCompile(String compile) {
        this.compile = compile;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public Boolean getResultHide() {
        return resultHide;
    }

    public void setResultHide(Boolean resultHide) {
        this.resultHide = resultHide;
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

    public Boolean getOtherHide() {
        return otherHide;
    }

    public void setOtherHide(Boolean otherHide) {
        this.otherHide = otherHide;
    }

    public Boolean getCheckHide() {
        return checkHide;
    }

    public void setCheckHide(Boolean checkHide) {
        this.checkHide = checkHide;
    }

    public Boolean getShowInputHide() {
        return showInputHide;
    }

    public void setShowInputHide(Boolean showInputHide) {
        this.showInputHide = showInputHide;
    }

    public Boolean getShowOutputHide() {
        return showOutputHide;
    }

    public void setShowOutputHide(Boolean showOutputHide) {
        this.showOutputHide = showOutputHide;
    }

    public String getCompareString() {
        return compareString;
    }

    public void setCompareString(String compareString) {
        this.compareString = compareString;
    }

}
