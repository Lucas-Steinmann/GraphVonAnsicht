package edu.kit.student.objectproperty;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.LinkedList;
import java.util.List;


/**
 * This class is an implementation for a writable property of an object in the GAns-Framework.
 * The value can be get and set with the corresponding accessors.
 *
 * @param <T>
 *            The type of element that is to be stored in the GAnsProperty.
 *
 * @author Nicolas Boltz, Lucas Steinmann
 */
public class GAnsProperty<T> implements ReadOnlyGAnsProperty<T> {

    @NotNull
    private String name;
    @Nullable
    private T value;

    private final List<ChangeListener<? super T>> changeListeners = new LinkedList<>();

    /**
     * Constructor, setting the name and value of the property.
     *
     * @param name  The string will be set as the name of the GAnsProperty.
     * @param value The value that will be set in the GAnsProperty.
     */
    public GAnsProperty(String name, T value) {
        setName(name);
        setValue(value);
    }

    /**
     * Sets the name/identifier of the GAnsProperty.
     *
     * @param name The string will be set as the name of the GAnsProperty.
     */
    public void setName(String name) {
        this.name = name == null ? "" : name;
    }

    @Override
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Sets the value and its string-representation of the GAnsProperty.
     *
     * @param value The value that will be set in the GAnsProperty.
     */
    public void setValue(T value) {
        T oldValue = this.value;
        this.value = value;
        for (ChangeListener<? super T> listener : changeListeners) {
            listener.changed(this, oldValue, this.value);
        }
    }

    @Override
    public T getValue() {
        return this.value;
    }


    @Override
    public void addListener(ChangeListener<? super T> listener) {
        changeListeners.add(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
        changeListeners.remove(listener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GAnsProperty)) return false;

        GAnsProperty<?> that = (GAnsProperty<?>) o;

        if (!getName().equals(that.getName())) return false;
        return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }
}
