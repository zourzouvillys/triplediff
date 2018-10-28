package zrz.triplediff;

import static org.junit.Assert.assertEquals;
import static zrz.triplediff.DeltaTerms.iri;
import static zrz.triplediff.DeltaTerms.literal;
import static zrz.triplediff.DeltaTerms.blankNode;
import static zrz.triplediff.DeltaTerms.prefixedName;

import org.junit.Test;

import triplediff.protobuf.TripleDiffProto.Delta;

public class DeltaProcessorTest {

  // do a round trip, generate diff convert to bytes, feed in to generate another builder and check the results.

  @Test
  public void test() {

    DeltaBuilder b = new DeltaBuilder();

    b.prefixAdd("example", "https://example.com/");

    b.add(iri("urn:xyz"), prefixedName("rdf", "type"), prefixedName("example", "Host"));

    b.remove(iri("urn:xyz"), prefixedName("rdf", "type"), prefixedName("example", "OldHost"));

    b.subject(iri("urn:xyz"))
        .add(prefixedName("rdf", "type"), iri("test:Class"))
        .add(iri("http://some.name"), literal("Theo"))
        .add(iri("key:disabled"), literal(true))
        .add(blankNode("AAA"), literal("Theo"));

    Delta delta = b.build();

    DeltaBuilder dupl = new DeltaBuilder();
    DeltaProcessor.apply(delta.toByteArray(), dupl);

    // the two should be equal.
    assertEquals(dupl.build().toString(), delta.toString());

  }

}
