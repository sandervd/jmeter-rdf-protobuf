package be.vlaanderen.rdfproto;
import java.io.*;

import org.apache.jena.riot.protobuf.wire.PB_RDF.*;
import org.apache.jena.riot.protobuf.wire.PB_RDF.RDF_StreamRow.RowCase;
import org.apache.jena.riot.protobuf.wire.PB_RDF.RDF_Term.TermCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.*;

public class RDFProtobufTransformer {
    private static final Logger log = LoggerFactory.getLogger(RDFProto.class);
    final byte[] protobuf;
    AtomicInteger counter = new AtomicInteger();
    public RDFProtobufTransformer(byte[] protobuf_) {
        protobuf = protobuf_;
    }

    public ByteArrayOutputStream getNext() throws IOException {
        InputStream inputStream = new DataInputStream(new ByteArrayInputStream(protobuf));
        return appendSubjects(inputStream, counter.getAndIncrement());
    }

    private static ByteArrayOutputStream appendSubjects(InputStream inputStream, int counter) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        RDF_StreamRow stream_row = RDF_StreamRow.parseDelimitedFrom(inputStream);
        if (null == stream_row)
            log.debug("Protobuf file is empty?");
        while (null != stream_row) {
            boolean isFlushed = false;
            RowCase stream_row_type = stream_row.getRowCase();
            // @todo Support quads?
            if (stream_row_type.equals(RowCase.TRIPLE)) {
                RDF_Triple triple = stream_row.getTriple();
                RDF_Term subject = triple.getS();
                TermCase subject_type = subject.getTermCase();
                if (subject_type.equals(TermCase.IRI)) {
                    RDF_IRI subject_iri = subject.getIri();
                    RDF_Triple new_triple = RDF_Triple.newBuilder()
                        .setS(
                            RDF_Term.newBuilder()
                                .setIri(RDF_IRI.newBuilder().setIri(subject_iri.getIri() + "/" + counter))
                            )
                        .setP(triple.getP())
                        .setO(triple.getO())
                        .build();
                    RDF_StreamRow new_row = RDF_StreamRow.newBuilder().setTriple(new_triple).build();
                    new_row.writeDelimitedTo(outputStream);
                    isFlushed=true;
                }
            }
            if (!isFlushed) {
                stream_row.writeDelimitedTo(outputStream);
            }
            // Next row
            stream_row = RDF_StreamRow.parseDelimitedFrom(inputStream);
        }
        return outputStream;
    }

    public static String format(InputStream protobufStream) throws IOException {
        String out = "";
        RDF_StreamRow stream_row = RDF_StreamRow.parseDelimitedFrom(protobufStream);
        if (null == stream_row)
            log.warn("Formatting empty protobuf message.");
        while (null != stream_row) {
            out += stream_row.toString();
            stream_row = RDF_StreamRow.parseDelimitedFrom(protobufStream);
        }
        return out;
    }

}
