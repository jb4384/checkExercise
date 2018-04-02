/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 *
 * @author Tiffany
 */
@Named(value = "exercises")
@ManagedBean
@ApplicationScoped
public class exercises {

    private final String exerciseDescription = "/exercisedescription/";
    private String header1;
    private String selectedName = "1";
    private String selectedExercise = "Exercise03_01";
    private List<SelectItem> names;
    private List<SelectItem> exes;
    private String program;

    @PostConstruct
    public void init() {
        String webinf = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF");
        String ags10e = new File(webinf + "/../../../ags10e").toString();
        names = new ArrayList<>();
        exes = new ArrayList<>();
        for (int i = 1; i < 44; i++) {
            names.add(new SelectItem("" + i, "Chapter " + i));
        }
        if (selectedName.length() < 2) selectedName =  "0" + selectedName;

        try {
            List<File> files = Files.walk(Paths.get(ags10e + exerciseDescription))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> file.getName().startsWith("Exercise"+selectedName))
                    .collect(Collectors.toList());
            files.forEach((File file) -> {
                exes.add(new SelectItem("" + file.getName(), file.getName()));
            });
        } catch (IOException ex) {
            System.out.println("failed");
        }
        header1 = "CheckExercise: " + selectedExercise + ".java";
    }

    public void updateExes(String numb) {
        File folder = new File(exerciseDescription);
        File[] listOfFiles = folder.listFiles();
        int num = Integer.parseInt(numb);
        for (File file : listOfFiles) {
            if (num < 10) {
                if (file.getName().startsWith("Exercise0" + num + "_")) {
                    exes.add(new SelectItem("" + file.getName(), file.getName()));
                }
            } else {
                if (file.getName().startsWith("Exercise" + num + "_")) {
                    exes.add(new SelectItem("" + file.getName(), file.getName()));
                }
            }
        }
    }

    public void submit() {
        header1 = "CheckExercise: Exercise01_01.java";
        System.out.println("Selected item: ");
    }

    public String getHeader1() {
        return header1;
    }

    public void setHeader1(String header1) {
        this.header1 = header1;
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

    public List<SelectItem> getNames() {
        return names;
    }

    public List<SelectItem> getExes() {
        return exes;
    }

    public String getProgram() {
        program = "/* Paste your " + selectedExercise + " here and click Automatic Check. \n"
                + "For all programming projects, the numbers should be double \n"
                + "unless it is explicitly stated as integer. \n"
                + "If you get a java.util.InputMismatchException error, check if \n"
                + "your code used input.readInt(), but it should be input.readDouble(). \n"
                + "For integers, use int unless it is explicitly stated as long. */";
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

}
