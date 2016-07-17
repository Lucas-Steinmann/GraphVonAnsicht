package edu.kit.student.graphmodel.action;

public abstract class SubGraphAction implements Action {
    

    private String name;
    private String description;
    
    public SubGraphAction(String name, String description) {
        this.setName(name);
        this.setDescription(description);
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
