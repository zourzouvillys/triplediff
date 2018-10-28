package zrz.triplediff;

import java.nio.charset.StandardCharsets;

import com.google.protobuf.ByteString;

import triplediff.protobuf.TripleDiffProto.Any;
import triplediff.protobuf.TripleDiffProto.Literal;
import triplediff.protobuf.TripleDiffProto.PrefixName;
import triplediff.protobuf.TripleDiffProto.Term;
import triplediff.protobuf.TripleDiffProto.Undefined;

public class DeltaTerms {

  public static Term iri(String iriString) {
    return Term.newBuilder().setIri(iriString).build();
  }

  public static Term blankNode(String label) {
    return Term.newBuilder().setBlankNode(label).build();
  }

  public static Term prefixedName(String prefix, String localName) {
    return Term.newBuilder().setPrefixedName(PrefixName.newBuilder().setPrefix(prefix).setLocalName(localName).build()).build();
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
