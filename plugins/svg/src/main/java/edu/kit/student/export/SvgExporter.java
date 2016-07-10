package edu.kit.student.export;


import edu.kit.student.graphmodel.serialize.SerializedGraph;
import edu.kit.student.plugin.Exporter;

import java.io.FileOutputStream;

public class SvgExporter implements Exporter {

    @Override
    public String getSupportedFileEnding() {
        return "*.svg";
    }

    @Override
    public void exportGraph(SerializedGraph graph, FileOutputStream filestream) {
        // TODO Auto-generated method stub
        
    }

}
