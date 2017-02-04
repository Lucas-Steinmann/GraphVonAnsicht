package edu.kit.student.joana;

import java.util.Objects;

/**
 * Object which represents a Java source file in the Joana GraphModel.
 *
 * @author Lucas Steinmann
 */
public class JavaSource {

    private String fileName;

    public JavaSource(String fileName) {
        this.fileName = fileName; {
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaSource that = (JavaSource) o;
        return Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName);
    }

}
