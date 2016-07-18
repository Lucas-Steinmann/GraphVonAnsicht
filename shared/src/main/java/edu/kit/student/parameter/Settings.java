package edu.kit.student.parameter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A compound object to store parameters.
 */
public class Settings {
    Map<String, Parameter<?,?>> parameters;

    /**
     * Constructs a new Settings-Object and sets its parameters.
     * @param parameters The parameters the Settings-Object will have.
     */
    public Settings(Map<String, Parameter<?,?>> parameters) {
        this.parameters = parameters;
    }

    /**
     * Returns the amount of parameters in the Settings.
     * @return The amount of parameters in the Settings.
     */
    public int size() {
        return parameters.size();
    }

    /**
     * Returns the Parameter associated with the given key.
     * @param key The key which is associated with the Parameter.
     * @return The Parameter associated with the given key.
     */
    public Parameter<?,?> get(String key) {
        return parameters.get(key);
    }

    public static double unpackDouble(Parameter<?, Double> parameter) {
        return parameter.getValue();
    }

    public static int unpackInteger(Parameter<?, Integer> parameter) {
        return parameter.getValue();
    }

    /**
     * Returns all the Parameters in the Settings-Object.
     * @return All the Parameters in the Settings-Object.
     */
    public Collection<Parameter<?,?>> values() {
        return parameters.values();
    }

    /**
     * Returns a Set of all the parameters.
     * @return A Set of all the parameters.
     */
    public Set<Map.Entry<String, Parameter<?,?>>> entrySet() {
        return parameters.entrySet();
    }
}
