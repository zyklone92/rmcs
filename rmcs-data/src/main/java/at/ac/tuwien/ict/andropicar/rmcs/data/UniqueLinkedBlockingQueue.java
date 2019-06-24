package at.ac.tuwien.ict.andropicar.rmcs.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An extension of the {@link LinkedBlockingQueue} that adds the functionality of only allowing one Object per class in the queue.
 * 
 * @author Boeck
 */
public class UniqueLinkedBlockingQueue<E> extends LinkedBlockingQueue<E> {

	
	private static final long serialVersionUID = 5849392414143441592L;
	
	
	/**
	 * @inheritDoc
	 * Adds the functionality of only allowing one Object per class in the queue.
	 * If there is a request to put a new object into the queue, all older objects of the same class are deleted from the queue, before the new one is added.
	 */
	@Override
	public void put(E e) throws InterruptedException{
		if(e == null)
			return;
		
		Collection<E> duplicateElements = new ArrayList<>();
		for(E element : this) {
			if(element.getClass().equals(e.getClass()))
				duplicateElements.add(element);
		}
		this.removeAll(duplicateElements);
		super.put(e);
	}
	
	
	/**
	 * 
	 * Adds the functionality of only allowing one Object per class in the queue.
	 * If there is a request to add a new object into the queue, all older objects of the same class are deleted from the queue, before the new one is added.
	 */
	@Override
	public boolean add(E e) throws IllegalStateException{
		if(e == null)
			return false;
		
		Collection<E> duplicateElements = new ArrayList<>();
		for(E element : this) {
			if(element.getClass().equals(e.getClass()))
				duplicateElements.add(element);
		}
		this.removeAll(duplicateElements);
		return super.add(e);
	}

	
	/**
	 * 
	 * Adds the functionality of only allowing one Object per class in the queue.
	 * If there is a request to add a list of new objects into the queue, all older objects of the same class are deleted from the queue, before the list is added.
	 * The list is not added, if it cointains 2 or more objects of the same class.
	 * @return false, if the passed Collection is null or it contains 2 or more objects of the same class. True if all objects were successfully added to the queue.
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) throws IllegalStateException{
		if(c == null)
			return false;
		
		for(E e : c) {
			for(E e2 : c){
				if(e != e2 && e.getClass().equals(e2.getClass()))
					return false;
			}
		}
		
		Collection<E> duplicateElements = new ArrayList<>();
		for(E element : this) {
			for(E e : c){
				if(element.getClass().equals(e.getClass()))
					duplicateElements.add(element);
			}
		}
		this.removeAll(duplicateElements);
		return super.addAll(c);
	}
	
}
