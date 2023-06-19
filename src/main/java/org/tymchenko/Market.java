package org.tymchenko;

import java.util.List;

public class Market {
    private String name;
    private List<CoefficientLine> runners;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\t\t").append(name).append("\n");
        if (runners != null) {
            runners.forEach(coefficientLine -> sb.append(coefficientLine.toString()).append("\n"));
        }
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CoefficientLine> getRunners() {
        return runners;
    }

    public void setRunners(List<CoefficientLine> runners) {
        this.runners = runners;
    }
}
