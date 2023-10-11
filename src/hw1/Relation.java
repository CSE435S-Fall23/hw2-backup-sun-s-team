package hw1;

import java.util.ArrayList;

/**
 * This class provides methods to perform relational algebra operations. It will be used
 * to implement SQL queries.
 * @author Doug Shook
 *
 */
public class Relation {

	private ArrayList<Tuple> tuples;
	private TupleDesc td;
	
	public Relation(ArrayList<Tuple> l, TupleDesc td) {
		//your code here
		this.tuples = new ArrayList<>(l);
        this.td = td;
	}
	
	/**
	 * This method performs a select operation on a relation
	 * @param field number (refer to TupleDesc) of the field to be compared, left side of comparison
	 * @param op the comparison operator
	 * @param operand a constant to be compared against the given column
	 * @return
	 */
	public Relation select(int field, RelationalOperator op, Field operand) {
		//your code here
		ArrayList<Tuple> selectedTuples = new ArrayList<>();

        for (Tuple tuple : tuples) {
            Field currentField = tuple.getField(field);
            if (currentField.compare(op, operand)) {
                selectedTuples.add(tuple);
            }
        }

        return new Relation(selectedTuples, td);
	}
	
	/**
	 * This method performs a rename operation on a relation
	 * @param fields the field numbers (refer to TupleDesc) of the fields to be renamed
	 * @param names a list of new names. The order of these names is the same as the order of field numbers in the field list
	 * @return
	 */
	public Relation rename(ArrayList<Integer> fields, ArrayList<String> names) {
	    TupleDesc newDesc = td.rename(fields, names); 
	    return new Relation(tuples, newDesc);
	}
	
	/**
	 * This method performs a project operation on a relation
	 * @param fields a list of field numbers (refer to TupleDesc) that should be in the result
	 * @return
	 */
	public Relation project(ArrayList<Integer> fields) {
		Type[] newTypes = new Type[fields.size()];
        String[] newFields = new String[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            newTypes[i] = td.getType(fields.get(i));
            newFields[i] = td.getFieldName(fields.get(i));
        }
        TupleDesc newDesc = new TupleDesc(newTypes, newFields);

        // Create new tuples based on the specified fields
        ArrayList<Tuple> newTuples = new ArrayList<>();
        for (Tuple tuple : tuples) {
//            Object[] newValues = new Object[fields.size()];
            Tuple newTuple = new Tuple(newDesc); 
            for (int i = 0; i < fields.size(); i++) {
//                newValues[i] = tuple.getField(fields.get(i));
                newTuple.setField(i, tuple.getField(fields.get(i)));
            }
            
            newTuples.add(newTuple);
        }

        return new Relation(newTuples, newDesc);
	}
	
	/**
	 * This method performs a join between this relation and a second relation.
	 * The resulting relation will contain all of the columns from both of the given relations,
	 * joined using the equality operator (=)
	 * @param other the relation to be joined
	 * @param field1 the field number (refer to TupleDesc) from this relation to be used in the join condition
	 * @param field2 the field number (refer to TupleDesc) from other to be used in the join condition
	 * @return
	 */
	public Relation join(Relation other, int field1, int field2) {
		Type[] joinTypes = new Type[td.numFields() + other.td.numFields()];
        String[] joinFields = new String[joinTypes.length];

        for (int i = 0; i < td.numFields(); i++) {
            joinTypes[i] = td.getType(i);
            joinFields[i] = td.getFieldName(i);
        }

        for (int i = 0; i < other.td.numFields(); i++) {
            joinTypes[td.numFields() + i] = other.td.getType(i);
            joinFields[td.numFields() + i] = other.td.getFieldName(i);
        }
        TupleDesc joinDesc = new TupleDesc(joinTypes, joinFields);

        // Create new tuples for the join result
        ArrayList<Tuple> joinTuples = new ArrayList<>();
        for (Tuple tuple1 : tuples) {
            for (Tuple tuple2 : other.tuples) {
                if (tuple1.getField(field1).equals(tuple2.getField(field2))) {
                    Object[] joinValues = new Object[joinTypes.length];
                    for (int i = 0; i < td.numFields(); i++) {
                        joinValues[i] = tuple1.getField(i);
                    }

                    for (int i = 0; i < other.td.numFields(); i++) {
                        joinValues[td.numFields() + i] = tuple2.getField(i);
                    }
                    Tuple joinTuple = new Tuple(joinDesc); 
                    joinTuples.add(joinTuple);
                }
            }
        }

        return new Relation(joinTuples, joinDesc);
    }
	
	
	/**
	 * Performs an aggregation operation on a relation. See the lab write up for details.
	 * @param op the aggregation operation to be performed
	 * @param groupBy whether or not a grouping should be performed
	 * @return
	 */
	public Relation aggregate(AggregateOperator op, boolean groupBy) {
		Aggregator agg = new Aggregator(op,groupBy,this.td);
		for(Tuple tuple:this.tuples) {
			agg.merge(tuple);
		}
		TupleDesc aggDesc;
		if(groupBy) {
			Type[] types = {Type.INT};
			String[] strings = {op.toString()};
			aggDesc = new TupleDesc(types,strings);
		}else {
			 Type[] types = {this.td.getType(0), Type.INT};
			 String[] strings = {this.td.getFieldName(0), op.toString()}; 
			 aggDesc = new TupleDesc(types, strings);
		}
		return new Relation(agg.getResults(), aggDesc);
	}


    public TupleDesc getDesc() {
        return td;
    }

    public ArrayList<Tuple> getTuples() {
        return new ArrayList<>(tuples);
    }
	
	/**
	 * Returns a string representation of this relation. The string representation should
	 * first contain the TupleDesc, followed by each of the tuples in this relation
	 */
	public String toString() {
		StringBuilder result = new StringBuilder(td.toString() + "\n");
        for (Tuple tuple : tuples) {
            result.append(tuple.toString()).append("\n");
        }
        return result.toString();
	}
}
