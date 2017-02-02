package edu.kit.student.tests;

import edu.kit.student.graphmodel.viewable.GenericGraphModel;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Tests the creation of GenericGraphModels
 */
public class GenericGraphModelBuilderTest {

    private static List<GenericGraphModel> models = new LinkedList<>();
    private static Iterator<File> lastOpenedFile;
    private static Random randomGenerator;
    private static List<File> files;

    /**
     * Takes sample files from the resources and tries to import them.
     */
    @Test
    public void testSampleFiles() throws Exception {

    }

    @Test
    public void build() throws Exception {

    }

}