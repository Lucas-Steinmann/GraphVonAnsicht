package edu.kit.student.graphmodel.action;

public abstract class EdgeAction implements Action {
    private String name;
    private String description;

    public EdgeAction(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }
}
