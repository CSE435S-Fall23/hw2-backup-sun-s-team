package hw1;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc {

	private Type[] types;
	private String[] fields;
    private static final int INT_SIZE = 4;
    private static final int STRING_SIZE = 129;
    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr array specifying the number of and types of fields in
     *        this TupleDesc. It must contain at least one entry.
     * @param fieldAr array specifying the names of the fields. Note that names may be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
    	//your code here
    	if (typeAr == null || typeAr.length == 0) {
            throw new IllegalArgumentException("typeAr must contain at least one entry");
        }
        this.types = typeAr;
        this.fields = fieldAr;
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        //your code here
    	return types.length;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        //your code here
    	if (i < 0 || i >= fields.length) {
            throw new NoSuchElementException("Invalid index: " + i);
        }
        return fields[i];
    }

    public TupleDesc rename(ArrayList<Integer> fields, ArrayList<String> names) {
        if (fields.size() != names.size()) {
            throw new IllegalArgumentException("Mismatch between fields and names size");
        }
        
        String[] newFields = Arrays.copyOf(this.fields, this.fields.length);
        for (int i = 0; i < fields.size(); i++) {
            newFields[fields.get(i)] = names.get(i);
        }

        return new TupleDesc(this.types, newFields);
    }


    /**
     * Find the index of the field with a given name.
     *
     * @param name name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException if no field with a matching name is found.
     */
    public int nameToId(String name) throws NoSuchElementException {
        //your code here
    	for (int i = 0; i < fields.length; i++) {
            if ((name == null && fields[i] == null) || (name != null && name.equals(fields[i]))) {
                return i;
            }
        }
        throw new NoSuchElementException("Field name not found: " + name);
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i The index of the field to get the type of. It must be a valid index.
     * @return the type of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public Type getType(int i) throws NoSuchElementException {
        //your code here
    	if (i < 0 || i >= types.length) {
            throw new NoSuchElementException("Invalid index: " + i);
        }
        return types[i];
    }
    /**
     * Returns a new TupleDesc with only the specified fields.
     *
     * @param fields the indices of the fields to project.
     * @return a new TupleDesc with only the specified fields.
     */
    public TupleDesc project(ArrayList<Integer> fields) {
        Type[] newTypes = new Type[fields.size()];
        String[] newFields = new String[fields.size()];

        for (int i = 0; i < fields.size(); i++) {
            newTypes[i] = this.types[fields.get(i)];
            newFields[i] = this.fields[fields.get(i)];
        }

        return new TupleDesc(newTypes, newFields);
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     * Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
    	//your code here
    	int size = 0;
        for (Type type : types) {
            size += type == Type.INT ? INT_SIZE : STRING_SIZE;
        }
        return size;
    }

    /**
     * Compares the specified object with this TupleDesc for equality.
     * Two TupleDescs are considered equal if they are the same size and if the
     * n-th type in this TupleDesc is equal to the n-th type in td.
     *
     * @param o the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
    	//your code here
    	//same memory address
    	if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleDesc that = (TupleDesc) o;
        if (this.getSize() != that.getSize()) return false;
        for (int i = 0; i < this.numFields(); i++) {
            if (!this.getType(i).equals(that.getType(i))) {
                return false;
            }
        }
        return true;
    }
    

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * @return String describing this descriptor.
     */
    public String toString() {
        //your code here
    	StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            sb.append(types[i].toString());
            sb.append("(");
            sb.append(fields[i]);
            sb.append(")");
            if (i < types.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
