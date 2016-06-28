package edu.kit.student.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.kit.student.plugin.PluginManager;

/**
 * Test Class for PluginManager.
 */
public class PluginManagerTest {
   
    @Test
    public void testPluginLoad() {
        PluginManager mgr = PluginManager.getPluginManager();

        assertTrue(mgr.getPlugins().size() == 4);
    }
}