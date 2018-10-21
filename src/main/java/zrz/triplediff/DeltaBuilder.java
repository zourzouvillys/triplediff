package zrz.triplediff;

import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import zrz.triplediff.protobuf.TripleDiffProto.Delta;
import zrz.triplediff.protobuf.TripleDiffProto.Delta.Builder;
import zrz.triplediff.protobuf.TripleDiffProto.Diff;
import zrz.triplediff.protobuf.TripleDiffProto.Diff.ValueCase;
import zrz.triplediff.protobuf.TripleDiffProto.PrefixDecl;
import zrz.triplediff.protobuf.TripleDiffProto.Row;
import zrz.triplediff.protobuf.TripleDiffProto.SubjectList;
import zrz.triplediff.protobuf.TripleDiffProto.Term;
import zrz.triplediff.protobuf.TripleDiffProto.Triple;
import zrz.triplediff.protobuf.TripleDiffProto.TupleList;

/**
 * builds a diff proto which is as small as possible given the changes.
 * 
 * @author theo
 *
 */

public class DeltaBuilder implements DeltaStream {

  private Delta.Builder d;
  private Map<Term, SubjectDeltaBuilder> currentContext = new HashMap<>();

  public DeltaBuilder() {
    this.d = Delta.newBuilder();
  }

  /**
   * creates the delta and resets internal state. this instance may be reused.
   */

  public Delta build() {
    flush();
    Delta res = d.build();
    this.clear();
    return res;
  }

  /**
   * resets internal state. may be reused.
   */

  public void clear() {
    d.clearDiffs();
    d.clearId();
    d.clearParent();
    this.currentContext.clear();
  }

  @Override
  public void prefixAdd(String prefix, String iri) {
    flush();
    d.addDiffs(add(prefixDecl(prefix, iri)));
  }

  @Override
  public void prefixRemove(String prefix, String iri) {
    flush();
    d.addDiffs(remove(prefixDecl(prefix, iri)));
  }

  @Override
  public void add(Term subject, Term predicate, Term object) {
    getSubject(subject).add(predicate, object);
  }

  @Override
  public void remove(Term subject, Term predicate, Term object) {
    getSubject(subject).remove(predicate, object);
  }

  //

  public SubjectDeltaStream subject(Term subject) {
    return getSubject(subject);
  }

  private class SubjectDeltaBuilder implements SubjectDeltaStream {

    private Term subject;
    private List<TupleList.Builder> pending = new LinkedList<>();
    private ValueCase type;

    public SubjectDeltaBuilder(Term subject) {
      this.subject = subject;
    }

    //

    @Override
    public SubjectDeltaBuilder add(Term predicate, Term object) {
      flushFor(ValueCase.ADD, null);
      merge(predicate, object);
      return this;
    }

    //

    @Override
    public SubjectDeltaBuilder remove(Term predicate, Term object) {
      flushFor(ValueCase.DELETE, null);
      merge(predicate, object);
      return this;
    }

    private void merge(Term predicate, Term object) {

      for (TupleList.Builder b : this.pending) {
        if (b.getPredicate().equals(predicate)) {
          b.addObjects(object);
          return;
        }
      }

      pending.add(
          TupleList.newBuilder()
              .addObjects(object)
              .setPredicate(predicate));
    }

    /**
     * 
     * @param delete
     * @param contexts
     */

    private void flushFor(ValueCase type, Iterable<Term> contexts) {

      if (this.type == null) {
        this.type = type;
      }
      else if (this.type != type) {
        // need to flush
        DeltaBuilder.this.flush();
      }

      this.type = type;
      currentContext.put(subject, this);

    }

    //

    private void flush() {

      Row.Builder row = Row.newBuilder();

      if (this.pending.size() == 1 && pending.get(0).getObjectsCount() == 1) {
        TupleList.Builder t = this.pending.remove(0);
        Triple.Builder tb = Triple.newBuilder()
            .setSubject(subject)
            .setPredicate(t.getPredicate())
            .setObject(t.getObjectsList().get(0));
        row.setTriple(tb);
      }
      else if (!this.pending.isEmpty()) {
        SubjectList.Builder slb = SubjectList.newBuilder();
        slb.setSubject(subject);
        this.pending.stream().forEach(tl -> slb.addValues(tl));
        row.setSubjectList(slb);
      }

      Diff.Builder diff = Diff.newBuilder();

      if (type == ValueCase.ADD) {
        diff.setAdd(row);
      }
      else {
        diff.setDelete(row);
      }

      d.addDiffs(diff);

      this.pending.clear();

    }

  }

  //

  private void flush() {
    this.currentContext.values().forEach(SubjectDeltaBuilder::flush);
    this.currentContext.clear();
  }

  private SubjectDeltaBuilder getSubject(Term subject) {
    return currentContext.computeIfAbsent(subject, _s -> new SubjectDeltaBuilder(_s));
  }

  // ---- helpers

  private static Row.Builder prefixDecl(String prefix, String iri) {
    return Row.newBuilder().setPrefix(PrefixDecl.newBuilder().setPrefix(prefix).setIri(iri));
  }

  private static Diff.Builder add(Row.Builder row) {
    return Diff.newBuilder().setAdd(row);
  }

  private static Diff.Builder remove(Row.Builder row) {
    return Diff.newBuilder().setDelete(row);
  }

}
