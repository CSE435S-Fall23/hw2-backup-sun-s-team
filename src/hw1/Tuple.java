package hw1;

import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This class represents a tuple that will contain a single row's worth of information
 * from a table. It also includes information about where it is stored
 * @author Sam Madden modified by Doug Shook
 *
 */
public class Tuple {
	private TupleDesc tupleDesc; // The schema for this tuple
    private int pid; // Page ID
    private int id;  // Tuple or slot ID
    private Field[] fields; // The data of the tuple

	
	/**
	 * Creates a new tuple with the given description
	 * @param t the schema for this tuple
	 */
	public Tuple(TupleDesc t) {
		//your code here
		this.tupleDesc = t;
	    this.fields = new Field[t.numFields()];
	}
	
	public TupleDesc getDesc() {
		//your code here
		return tupleDesc;
	}
	
	/**
	 * retrieves the page id where this tuple is stored
	 * @return the page id of this tuple
	 */
	public int getPid() {
		//your code here
		return pid;
	}

	public void setPid(int pid) {
		//your code here
		this.pid = pid;
	}

	/**
	 * retrieves the tuple (slot) id of this tuple
	 * @return the slot where this tuple is stored
	 */
	public int getId() {
		//your code here
		return id;
	}

	public void setId(int id) {
		//your code here
		this.id = id;
	}
	
	public void setDesc(TupleDesc td) {
		//your code here;
		this.tupleDesc = td;
        this.fields = new Field[td.numFields()];
	}
	
	/**
	 * Stores the given data at the i-th field
	 * @param i the field number to store the data
	 * @param v the data
	 */
	public void setField(int i, Field v) {
		//your code here
		if (i < 0 || i >= fields.length) {
            throw new IllegalArgumentException("Invalid index: " + i);
        }
        fields[i] = v;
	}
	
	public Field getField(int i) {
		//your code here
		if (i < 0 || i >= fields.length) {
            throw new IllegalArgumentException("Invalid index: " + i);
        }
        return fields[i];
	}
	
	/**
	 * Creates a string representation of this tuple that displays its contents.
	 * You should convert the binary data into a readable format (i.e. display the ints in base-10 and convert
	 * the String columns to readable text).
	 */
	public String toString() {
		//your code here
		return Arrays.stream(fields)
	            .map(field -> field == null ? "null" : field.toString())
	            .reduce((field1, field2) -> field1 + ", " + field2)
	            .orElse("");
	}
}
	