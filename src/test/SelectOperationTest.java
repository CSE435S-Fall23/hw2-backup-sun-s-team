package test;

import static org.junit.Assert.*;
import hw1.Catalog;
import hw1.Database;
import hw1.IntField;
import hw1.Relation;
import hw1.RelationalOperator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SelectOperationTest {

    private Catalog c;

    @Before
    public void setup() {
        try {
            Files.copy(new File("testfiles/A.dat.bak").toPath(), new File("testfiles/A.dat").toPath(), 
            		StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Unable to copy files");
            e.printStackTrace();
        }

        c = Database.getCatalog();
        c.loadSchema("testfiles/A.txt");
    }

    @Test
    public void testSelectGreaterThan() {
        int tableId = c.getTableId("A");
        Relation ar = new Relation(c.getDbFile(tableId).getAllTuples(), c.getTupleDesc(tableId));
        ar = ar.select(0, RelationalOperator.GT, new IntField(500));

        assertTrue(ar.getTuples().size() > 0);  
    }

    
}
