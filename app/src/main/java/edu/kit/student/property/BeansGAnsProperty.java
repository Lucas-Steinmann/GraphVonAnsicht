package edu.kit.student.property;

import edu.kit.student.objectproperty.GAnsProperty;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;

/**
 * This class is a wrapper for the slimmed down {@link GAnsProperty} of the GAns-Framework,
 * to make them compatible with JavaFX-Properties.
 * @param <T> The type object of the property
 */
public final class BeansGAnsProperty<T>
        extends ReadOnlyBeansGAnsProperty<T>
        implements Property<T>, WritableObjectValue<T> {

    private final Object bean;
    private final GAnsProperty<T> property;
    private final Map<ChangeListener<? super T>,
                      edu.kit.student.objectproperty.ChangeListener<? super T>>
            changeListenerMap = new HashMap<>();
    private final Map<InvalidationListener,
                      edu.kit.student.objectproperty.ChangeListener<? super T>>
            invalidationListenerMap = new HashMap<>();

    private ChangeListener<T> binding;
    private ObservableValue<? extends T> bindTarget;

    /**
     * Constructs a new Beans GAnsProperty wrapper with the given bean as parent
     * and the property to wrap.
     * @param parent the parent bean
     * @param property the property to wrap
     */
    public BeansGAnsProperty(Object parent, GAnsProperty<T> property) {
        this.bean = parent;
        this.property = property;
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public T get() {
        return property.getValue();
    }

    @Override
    public void set(T value) {

    }

    @Override
    public void addListener(ChangeListener<? super T> listener) {
        edu.kit.student.objectproperty.ChangeListener<T> forwardListener =
            (observedProp, oldValue, newValue) -> listener.changed(this, oldValue, newValue);

        changeListenerMap.put(listener, forwardListener);
        property.addListener(forwardListener);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        edu.kit.student.objectproperty.ChangeListener<T> forwardListener =
            (observedProp, oldValue, newValue) -> listener.invalidated(this);

        invalidationListenerMap.put(listener, forwardListener);
        property.addListener(forwardListener);
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
        property.removeListener(changeListenerMap.get(listener));
        changeListenerMap.remove(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        property.removeListener(invalidationListenerMap.get(listener));
        invalidationListenerMap.remove(listener);
    }

    @Override
    public void bind(ObservableValue<? extends T> observable) {
        if (isBound()) {
            unbind();
        }
        binding = ((observable1, oldValue, newValue) -> setValue(newValue));
        bindTarget = observable;
        observable.addListener(binding);
    }

    @Override
    public void unbind() {
        if (isBound()) {
            bindTarget.removeListener(binding);
            binding = null;
        }
    }

    @Override
    public boolean isBound() {
        return binding != null;
    }

    @Override
    public void bindBidirectional(final Property<T> other) {
        Bindings.bindBidirectional(other, this);
    }

    @Override
    public void unbindBidirectional(final Property<T> other) {
        Bindings.unbindBidirectional(other, this);
    }

    @Override
    public void setValue(final T value) {
        property.setValue(value);
    }

    public static <T> BeansGAnsProperty<T> wrap(GAnsProperty<T> property) {
        return new BeansGAnsProperty<>(null, property);
    }
}
