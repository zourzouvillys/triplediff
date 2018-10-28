package zrz.triplediff;

import triplediff.protobuf.TripleDiffProto.Term;

/**
 * API for receiving a stream of changes to a triplestore.
 * 
 * @author theo
 *
 */

public interface DeltaStream {

  /**
   * map a prefix to an IRI.
   * 
   * @param prefix
   * @param iri
   */

  void prefixAdd(String prefix, String iri);

  /**
   * remove a mapped prefix.
   * 
   * @param prefix
   * @param iri
   * 
   */

  void prefixRemove(String prefix, String iri);

  /**
   * add a triple.
   * 
   * @param subject
   * @param predicate
   * @param object
   */

  void add(Term subject, Term predicate, Term object);

  /**
   * remove a triple.
   * 
   * @param subject
   * @param predicate
   * @param object
   */

  void remove(Term subject, Term predicate, Term object);

}
