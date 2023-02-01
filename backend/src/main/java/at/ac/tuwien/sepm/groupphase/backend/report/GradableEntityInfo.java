package at.ac.tuwien.sepm.groupphase.backend.report;

public class GradableEntityInfo {
    private String name = "";
    private Double results;

    public GradableEntityInfo(Double results) {
        this.results = results;
    }

    public GradableEntityInfo(String name) {
        this.name = name;
    }

    public GradableEntityInfo() {

    }

    public GradableEntityInfo(String name, Double results) {
        this.name = name;
        this.results = results;
    }

    public String getName() {
        return this.name;
    }

    public Double getResults() {
        return this.results;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResults(Double results) {
        this.results = results;
    }
}
