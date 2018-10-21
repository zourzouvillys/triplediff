package zrz.triplediff;

import zrz.triplediff.protobuf.TripleDiffProto.IRI;
import zrz.triplediff.protobuf.TripleDiffProto.Literal;

import java.nio.charset.StandardCharsets;

import com.google.protobuf.ByteString;

import zrz.triplediff.protobuf.TripleDiffProto.Any;
import zrz.triplediff.protobuf.TripleDiffProto.BNode;
import zrz.triplediff.protobuf.TripleDiffProto.PrefixName;
import zrz.triplediff.protobuf.TripleDiffProto.Term;
import zrz.triplediff.protobuf.TripleDiffProto.Undefined;
import zrz.triplediff.protobuf.TripleDiffProto.Variable;

public class DeltaTerms {

  private static final Term UNDEFINED_TERM = Term.newBuilder().setUndefined(Undefined.getDefaultInstance()).build();
  private static final Term ANY_TERM = Term.newBuilder().setAny(Any.getDefaultInstance()).build();

  public static Term iri(String iriString) {
    return Term.newBuilder().setIri(IRI.newBuilder().setIriString(iriString)).build();
  }

  public static Term blankNode(String label) {
    return Term.newBuilder().setBlankNode(BNode.newBuilder().setLabel(label)).build();
  }

  public static Term var(String label) {
    return Term.newBuilder().setVar(Variable.newBuilder().setName(label).build()).build();
  }

  public static Term undefined() {
    return UNDEFINED_TERM;
  }

  public static Term any() {
    return ANY_TERM;
  }

  public static Term prefixedName(String prefix, String localName) {
    return Term.newBuilder().setPrefixName(PrefixName.newBuilder().setPrefix(prefix).setLocalName(localName).build()).build();
  }

  public static Term literal(String lexicalForm, String dataType) {
    return literal(ByteString.copyFrom(lexicalForm, StandardCharsets.UTF_8), dataType);
  }

  public static Term literal(byte[] binary, String dataType) {
    return literal(ByteString.copyFrom(binary), dataType);
  }

  public static Term literal(ByteString lexicalForm, String dataType) {
    return Term.newBuilder().setLiteral(Literal.newBuilder().setLexicalForm(lexicalForm).setDataType(dataType)).build();
  }

  public static Term literal(long i) {
    return Term.newBuilder().setIntegerLiteral(i).build();
  }

  public static Term literal(String value) {
    return Term.newBuilder().setStringLiteral(value).build();
  }

  public static Term literal(double i) {
    return Term.newBuilder().setDoubleLiteral(i).build();
  }

  public static Term literal(boolean i) {
    return Term.newBuilder().setBooleanLiteral(i).build();
  }

}
