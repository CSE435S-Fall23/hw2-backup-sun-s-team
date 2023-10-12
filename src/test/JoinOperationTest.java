package test;

import static org.junit.Assert.*;
import hw1.Catalog;
import hw1.Database;
import hw1.Relation;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class JoinOperationTest {

    private Catalog c;

    @Before
    public void setup() {
        try {
            Files.copy(new File("testfiles/A.dat.bak").toPath(), 
            		new File("testfiles/A.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(new File("testfiles/test.dat.bak").toPath(), new File("testfiles/test.dat").toPath(),
            		StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Unable to copy files");
            e.printStackTrace();
        }

        c = Database.getCatalog();
        c.loadSchema("testfiles/A.txt");
        c.loadSchema("testfiles/test.txt");
    }

    @Test
    public void testUnsuccessfulJoin() {
        int tableIdTest = c.getTableId("test");
        int tableIdA = c.getTableId("A");

        Relation testRelation = new Relation(c.getDbFile(tableIdTest).getAllTuples(), c.getTupleDesc(tableIdTest));
        Relation aRelation = new Relation(c.getDbFile(tableIdA).getAllTuples(), c.getTupleDesc(tableIdA));

        
        Relation joinedRelation = testRelation.join(aRelation, 0, 1); 

        assertTrue(joinedRelation.getTuples().size() == 0);  
    }
}
