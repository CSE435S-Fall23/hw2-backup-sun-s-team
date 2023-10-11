package hw1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A class to perform various aggregations, by accepting one tuple at a time
 * @author Doug Shook
 *
 */
public class Aggregator {
    private AggregateOperator op;
    private boolean groupBy;
    private TupleDesc td;
    private HashMap<Field, ArrayList<Tuple>> groups;

    public Aggregator(AggregateOperator o, boolean groupBy, TupleDesc td) {
        this.op = o;
        this.groupBy = groupBy;
        this.td = td;
        this.groups = new HashMap<>();
    }

	/**
	 * Merges the given tuple into the current aggregation
	 * @param t the tuple to be aggregated
	 */

    public void merge(Tuple t) {
        Field groupingField = groupBy ? t.getField(0) : null;

        if (!groups.containsKey(groupingField)) {
            groups.put(groupingField, new ArrayList<>());
        }
        groups.get(groupingField).add(t);
    }
	
	/**
	 * Returns the result of the aggregation
	 * @return a list containing the tuples after aggregation
	 */
	public ArrayList<Tuple> getResults() {
		ArrayList<Tuple> results = new ArrayList<>();

	    for (Field group : groups.keySet()) {
	        ArrayList<Tuple> groupedTuples = groups.get(group);

	        // Variables to hold aggregate results
	        int count = 0;
	        int sum = 0;
	        IntField minField = null;
	        IntField maxField = null;

	        for (Tuple t : groupedTuples) {
	            IntField currentField = (IntField) t.getField(0);

	            // For COUNT
	            count++;

	            // For SUM
	            sum += currentField.getValue();

	            // For MIN
	            if (minField == null || currentField.getValue() < minField.getValue()) {
	                minField = currentField;
	            }

	            // For MAX
	            if (maxField == null || currentField.getValue() > maxField.getValue()) {
	                maxField = currentField;
	            }
	        }

	        IntField resultField = null;
	        switch(op) {
	            case COUNT:
	                resultField = new IntField(count);
	                break;
	            case SUM:
	                resultField = new IntField(sum);
	                break;
	            case AVG:
	                resultField = new IntField(sum / count); // Assumes integer division
	                break;
	            case MIN:
	                resultField = minField;
	                break;
	            case MAX:
	                resultField = maxField;
	                break;
	        }

	        Tuple resultTuple = new Tuple(td);
	        if (groupBy) {
	            resultTuple.setField(0, group);
	            resultTuple.setField(1, resultField);
	        } else {
	            resultTuple.setField(0, resultField);
	        }

	        results.add(resultTuple);
	    }

	    return results;
    }

}
