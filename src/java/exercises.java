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
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
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

    @PostConstruct
    public void init() {
        buildChapters();
        updateExes();
        header1 = "CheckExercise: " + selectedExercise + ".java";
    }

    public void buildChapters() {
        names = new ArrayList<>();
        chapters = new TreeSet<>();
        try {
            String webinf = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF");
            String ags10e = new File(webinf + "/../../../ags10e").toString();
            files = Files.walk(Paths.get(ags10e + "/exercisedescription/"))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            files.forEach((File file) -> {
                String ch = file.getName().split("_")[0].trim().replaceAll("[^0-9]","");
                chapters.add(Integer.parseInt(ch));
            });
        } catch (IOException ex) {
            System.out.println("failed build chapters");
        }
        chapters.forEach(ch -> {
            names.add(new SelectItem("" + ch, "Chapter " + ch));
        });
    }

    public void updateExes() {
        exes = new ArrayList<>();
        buildFiles("/exercisedescription/");
        files.forEach((File file) -> {
            if (selectedExercise.isEmpty()) {
                selectedExercise = file.getName();
            }
            exes.add(new SelectItem("" + file.getName(), file.getName()));
        });
    }

    public void buildFiles(String endPath) {
        try {
            String webinf = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF");
            String ags10e = new File(webinf + "/../../../ags10e").toString();
            files = Files.walk(Paths.get(ags10e + endPath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> file.getName().startsWith("Exercise" + selectedName))
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
