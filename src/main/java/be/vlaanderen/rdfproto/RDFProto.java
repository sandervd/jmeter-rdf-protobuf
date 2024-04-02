package be.vlaanderen.rdfproto;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.engine.util.NoConfigMerge;
import org.apache.jmeter.engine.util.NoThreadClone;
 
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RDFProto extends ConfigTestElement
        implements NoThreadClone, LoopIterationListener, NoConfigMerge, TestStateListener {

    private static final long serialVersionUID = 7383500755324202605L;

    private static final Logger log = LoggerFactory.getLogger(RDFProto.class);

    public static final String FILENAME = "filename";

    public static final String VARIABLE_NAMES = "variableNames";

    private String filename;    // Real filename, with substituted variables

    private RDFProtobufTransformer protobufRDFTransform = null;

    @Override
    public void iterationStart(LoopIterationEvent event) {
        JMeterVariables variables = JMeterContextService.getContext().getVariables();
        try {
            ByteArrayOutputStream rdf_body = protobufRDFTransform.getNext();
            variables.putObject("rdf_body", rdf_body);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void testEnded(String host) {
        testEnded();
    }

    @Override
    public void testEnded() {
        protobufRDFTransform = null;
    }
    @Override
    public void testStarted(String host) {
        testStarted();
    }

    @Override
    public void testStarted() {
        trySetFinalFilename();
    }
    
    public String getFilename() {
        return getPropertyAsString(FILENAME);
    }

    public void setFilename(String filename) {
        setProperty(FILENAME, filename);
    }

    public void trySetFinalFilename() {
        if (filename == null) {
            filename = getFinalFilename();
            protobufRDFTransform = new RDFProtobufTransformer(filename);
        }
    }

    private String getFinalFilename() {
        String ret = getFilename();

        JMeterVariables variables = JMeterContextService.getContext().getVariables();
        Pattern pattern = Pattern.compile("\\$\\{([a-z]+)}");
        Matcher matcher = pattern.matcher(ret);
        while (matcher.find()) {
            String contents;
            try {
                contents = variables.get(matcher.group(1));
            } catch (NullPointerException e) {
                contents = null;
            }

            if (contents != null)
                ret = ret.replace(matcher.group(), contents);
        }

        return ret;
    }

   

}
