package zrz.triplediff;

import java.util.stream.Stream;

import zrz.triplediff.protobuf.TripleDiffProto.Term;

public interface SubjectDeltaStream {

  SubjectDeltaStream remove(Term predicate, Term object);

  SubjectDeltaStream add(Term predicate, Term object);

  default SubjectDeltaStream addAll(Term predicate, Term... objects) {
    for (Term object : objects) {
      add(predicate, object);
    }
    return this;
  }

  default SubjectDeltaStream addAll(Term predicate, Stream<Term> objects) {
    objects.forEach(object -> add(predicate, object));
    return this;
  }

  default SubjectDeltaStream addAll(Term predicate, Iterable<Term> objects) {
    objects.forEach(object -> add(predicate, object));
    return this;
  }

}
