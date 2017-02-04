package edu.kit.student.joana;

import java.util.Set;

/**
 */
public interface JoanaObjectPool {

    /**
     * Returns all {@link JavaSource}s in this {@link JoanaObjectPool}.
     * @return the set of {@link JavaSource}s
     */
    Set<JavaSource> getJavaSources();

    /**
     * Checks if this {@link JoanaObjectPool} has a {@link JavaSource} with the specified source name.
     * If yes this {@link JavaSource} is returned.
     * If not creates a new {@link JavaSource} with that name and returns it.
     * @param sourceName the name of the source
     * @return the {@link JavaSource}
     */
    JavaSource getJavaSource(String sourceName);
}
