package zrz.triplediff;

import java.util.function.Consumer;

import com.google.protobuf.InvalidProtocolBufferException;

import triplediff.protobuf.TripleDiffProto.Delta;
import triplediff.protobuf.TripleDiffProto.Delta.Builder;
import triplediff.protobuf.TripleDiffProto.Diff;
import triplediff.protobuf.TripleDiffProto.Diff.ValueCase;
import triplediff.protobuf.TripleDiffProto.PrefixDecl;
import triplediff.protobuf.TripleDiffProto.Row;
import triplediff.protobuf.TripleDiffProto.SubjectList;
import triplediff.protobuf.TripleDiffProto.Term;
import triplediff.protobuf.TripleDiffProto.Triple;
import triplediff.protobuf.TripleDiffProto.TupleList;
/**
 * given a incoming binary diff, applies to the specified stream.
 * 
 * @author theo
 *
 */

public class DeltaProcessor {

  public static void apply(byte[] data, DeltaStream stream) {
    try {
      Delta diff = Delta.parseFrom(data);
      apply(diff, stream);
    }
    catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }

  public static void apply(Delta delta, DeltaStream stream) {
    Consumer<Diff> proc = diff -> apply(diff, stream);
    delta.getDiffsList()
        .stream()
        .forEach(proc);
  }

  public static void apply(Diff diff, DeltaStream stream) {
    switch (diff.getValueCase()) {
      case ADD:
        applyAdd(diff.getAdd(), stream);
        break;
      case DELETE:
        applyDelete(diff.getDelete(), stream);
        break;
      case VALUE_NOT_SET:
      default:
        throw new IllegalArgumentException(diff.getValueCase().toString());
    }
  }

  public static void applyAdd(Row add, DeltaStream stream) {
    switch (add.getValueCase()) {
      case PREFIX:
        stream.prefixAdd(add.getPrefix().getPrefix(), add.getPrefix().getIri());
        break;
      case TRIPLE: {
        Triple t = add.getTriple();
        stream.add(t.getSubject(), t.getPredicate(), t.getObject());
        break;
      }
      case SUBJECT_LIST: {
        SubjectList sl = add.getSubjectList();
        for (TupleList vl : sl.getValuesList()) {
          vl.getObjectsList().forEach(object -> stream.add(sl.getSubject(), vl.getPredicate(), object));
        }
        break;
      }
      case VALUE_NOT_SET:
      default:
        throw new IllegalArgumentException(add.getValueCase().toString());
    }
  }

  public static void applyDelete(Row delete, DeltaStream stream) {
    switch (delete.getValueCase()) {
      case PREFIX:
        stream.prefixRemove(delete.getPrefix().getPrefix(), delete.getPrefix().getIri());
        break;
      case TRIPLE: {
        Triple t = delete.getTriple();
        stream.remove(t.getSubject(), t.getPredicate(), t.getObject());
        break;
      }
      case SUBJECT_LIST: {
        SubjectList sl = delete.getSubjectList();
        for (TupleList vl : sl.getValuesList()) {
          vl.getObjectsList().forEach(object -> stream.remove(sl.getSubject(), vl.getPredicate(), object));
        }
        break;
      }
      case VALUE_NOT_SET:
      default:
        throw new IllegalArgumentException(delete.getValueCase().toString());
    }
  }

}
