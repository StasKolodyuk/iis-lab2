package by.bsu.kolodyuk.model;


import java.util.List;

public class Type {

    private String name;
    private List<Example> examples;

    private double[] masses;
    private double[] deltas;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Example> getExamples() {
        return examples;
    }

    public void setExamples(List<Example> examples) {
        this.examples = examples;
    }

    public double[] getMasses() {
        return masses;
    }

    public void setMasses(double[] masses) {
        this.masses = masses;
    }

    public double[] getDeltas() {
        return deltas;
    }

    public void setDeltas(double[] deltas) {
        this.deltas = deltas;
    }
}
