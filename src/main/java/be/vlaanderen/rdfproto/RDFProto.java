package be.vlaanderen.rdfproto;

import org.apache.commons.io.IOUtils;
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
import java.util.Base64;


public class RDFProto extends ConfigTestElement
        implements NoThreadClone, LoopIterationListener, NoConfigMerge, TestStateListener {

    private static final long serialVersionUID = 7383500755324202605L;

    private static final Logger log = LoggerFactory.getLogger(RDFProto.class);

    public static final String FILECONTENTS = "filecontents";


    private byte[] protobuf;

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

    public byte[] getProtobuf() {
        String protobuf_encoded = getPropertyAsString(FILECONTENTS);
        return Base64.getDecoder().decode(protobuf_encoded);
    }

    public void setProtobuf(String filename) throws IOException {
        if (null == filename)
            return;
        System.out.print("filename: " + filename);
        File inputFile = new File(filename);
        InputStream inputStream = new FileInputStream(inputFile);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String protobuf_encoded = Base64.getEncoder().encodeToString(bytes);
        setProperty(FILECONTENTS, protobuf_encoded);
    }

    public void trySetFinalFilename() {
        if (protobuf == null) {
            protobuf = getProtobuf();
            protobufRDFTransform = new RDFProtobufTransformer(protobuf);
        }
    }

   

}
