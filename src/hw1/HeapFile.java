package hw1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A heap file stores a collection of tuples. It is also responsible for managing pages.
 * It needs to be able to manage page creation as well as correctly manipulating pages
 * when tuples are added or deleted.
 * @author Sam Madden modified by Doug Shook
 *
 */
public class HeapFile {
	
	public static final int PAGE_SIZE = 4096;
	private File file;
	private TupleDesc td;
	/**
	 * Creates a new heap file in the given location that can accept tuples of the given type
	 * @param f location of the heap file
	 * @param types type of tuples contained in the file
	 */
	public HeapFile(File f, TupleDesc type) {
		//your code here
		this.file = f;
		this.td = type;
	}
	
	public File getFile() {
		//your code here
		return file;
	}
	
	public TupleDesc getTupleDesc() {
		//your code here
		return td;
	}
	
	/**
	 * Creates a HeapPage object representing the page at the given page number.
	 * Because it will be necessary to arbitrarily move around the file, a RandomAccessFile object
	 * should be used here.
	 * @param id the page number to be retrieved
	 * @return a HeapPage at the given page number
	 */
	public HeapPage readPage(int id) {
		//your code here
		byte[] bytes = new byte[PAGE_SIZE];
		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			raf.seek(id * PAGE_SIZE);
			raf.readFully(bytes);
			return new HeapPage(id, bytes, this.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns a unique id number for this heap file. Consider using
	 * the hash of the File itself.
	 * @return
	 */
	public int getId() {
		//your code here
		return file.getAbsolutePath().hashCode();
	}
	
	/**
	 * Writes the given HeapPage to disk. Because of the need to seek through the file,
	 * a RandomAccessFile object should be used in this method.
	 * @param p the page to write to disk
	 */
	public void writePage(HeapPage p) {
		//your code here
		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
			byte[] data = p.getPageData();
			//This line sets the file-pointer offset
			raf.seek(p.getId() * PAGE_SIZE);
			raf.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a tuple. This method must first find a page with an open slot, creating a new page
	 * if all others are full. It then passes the tuple to this page to be stored. It then writes
	 * the page to disk (see writePage)
	 * @param t The tuple to be stored
	 * @return The HeapPage that contains the tuple
	 */
	public HeapPage addTuple(Tuple t) {
		//your code here
		int numPages = this.getNumPages();
		for (int i = 0; i < numPages; i++) {
			HeapPage page = readPage(i);
			try {
				page.addTuple(t);
				writePage(page);
				return page;
			} catch (Exception e) {
				continue;
			}
		}
		
		// If we reach here, no available page was found, so we create a new page.
		try {
			HeapPage newPage = new HeapPage(numPages, new byte[PAGE_SIZE], getId());
			newPage.addTuple(t);
			writePage(newPage);
			return newPage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method will examine the tuple to find out where it is stored, then delete it
	 * from the proper HeapPage. It then writes the modified page to disk.
	 * @param t the Tuple to be deleted
	 */
	public void deleteTuple(Tuple t){
		//your code here
		int pageId = t.getPid();
		HeapPage page = readPage(pageId);
		try {
			page.deleteTuple(t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writePage(page);
	}
	
	/**
	 * Returns an ArrayList containing all of the tuples in this HeapFile. It must
	 * access each HeapPage to do this (see iterator() in HeapPage)
	 * @return
	 */
	public ArrayList<Tuple> getAllTuples() {
		//your code here
		ArrayList<Tuple> allTuples = new ArrayList<>();
		int numPages = this.getNumPages();
		for (int i = 0; i < numPages; i++) {
			HeapPage page = readPage(i);
			Iterator<Tuple> iterator = page.iterator();
			while (iterator.hasNext()) {
				allTuples.add(iterator.next());
			}
		}
		return allTuples;
	}
	
	/**
	 * Computes and returns the total number of pages contained in this HeapFile
	 * @return the number of pages
	 */
	public int getNumPages() {
		//your code here
		return (int) (file.length() / PAGE_SIZE);
	}
}
