package org.tatoeba.searchsentences;

/**
 * Sentences that go through a filter are either selected or discarded
 * @author qdii
 *
 */
public interface Filter {
	/**
	 * Checks if the sentence is a match for the filter 
	 * @param sentence
	 * @return true if the sentence should be kept
	 */
	abstract public boolean select( Sentence sentence );
}
