package edu.kit.student.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Test Runner for all plugins related tests.
 */
public class PluginTestRunner {

    /**
     * Executes all tests.
     * @param args ignored
     */
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(PluginManagerTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }
}
