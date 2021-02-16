package ru.senina.lab5;

import ru.senina.lab5.labwork.LabWork;

import java.util.Scanner;

public abstract class Command {
    private final String name;
    private LabWork labWorkElement;
    private String stringParam;
    private Scanner in = null;


    public void setIn(Scanner in) {
        this.in = in;
    }

    public String getStringParam() {
        return stringParam;
    }

    public void setStringParam(String stringParam) {
        this.stringParam = stringParam;
    }

    public Command(String name) {
        this.name = name;

    }

    public LabWork getLabWorkElement() {
        return labWorkElement;
    }

    public void setLabWorkElement(LabWork labWorkElement) {
        this.labWorkElement = labWorkElement;
    }

    public String getName() {
        return name;
    }

    public abstract String run();

}
