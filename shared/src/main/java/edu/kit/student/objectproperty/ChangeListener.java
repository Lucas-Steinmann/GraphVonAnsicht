package edu.kit.student.objectproperty;

/**
 * Slimmed down version of a JavaFx ChangeListener.
 *
 * @param <T> the type of the value to observe
 */
@FunctionalInterface
public interface ChangeListener<T> {

    /**
     * This method needs to be provided by an implementation of ChangeListener.
     * It is called if the value of the {@link ReadOnlyGAnsProperty} changes.
     *
     * @param property The property whose value changed
     * @param oldValue The old value
     * @param newValue The new value
     */
    void changed(ReadOnlyGAnsProperty<? extends T> property, T oldValue, T newValue);
}
