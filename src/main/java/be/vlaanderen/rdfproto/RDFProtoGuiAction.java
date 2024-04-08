package be.vlaanderen.rdfproto;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JMeterStopThreadException;
import org.apache.log.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class RDFProtoGuiAction implements ActionListener {

    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
    private final RDFProtoGui RDFProtoGui;

    public RDFProtoGuiAction(RDFProtoGui randomCSVConfigGui) {
        this.RDFProtoGui = randomCSVConfigGui;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final RDFProto config = (RDFProto) RDFProtoGui.createTestElement();
        JTextArea checkArea = RDFProtoGui.getCheckArea();
        
        try {
            //config.setProtobuf();
            //config.setFilename(compoundValue(config.getFilename()));
            
            JMeterVariables jMeterVariables = new JMeterVariables();
            JMeterContextService.getContext().setVariables(jMeterVariables);

            String previewText = "";

            config.trySetFinalFilename();
            config.testStarted();
            try {
                config.iterationStart(null);
                ByteArrayOutputStream protobuf = (ByteArrayOutputStream) jMeterVariables.getObject("rdf_body");
                ByteArrayInputStream input = new ByteArrayInputStream(protobuf.toByteArray());
                previewText = RDFProtobufTransformer.format(input);

            } catch (JMeterStopThreadException | IOException ex) {
                // OK
            }

            config.testEnded();
            checkArea.setText(previewText);
            // move scroll to top
            checkArea.setCaretPosition(0);
        } catch (RuntimeException  ex) {
            checkArea.setText(ex.getMessage());
            LOGGER.warn("Failed to test protobuf reading ", ex);
        }
    }

    private String compoundValue(String val) throws InvalidVariableException {
        CompoundVariable compoundVariable = new CompoundVariable();
        compoundVariable.setParameters(val);
        return compoundVariable.execute();
    }
}
