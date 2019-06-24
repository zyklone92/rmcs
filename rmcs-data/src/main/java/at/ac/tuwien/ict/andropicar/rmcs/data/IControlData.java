package at.ac.tuwien.ict.andropicar.rmcs.data;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * A general class for all classes used to store control-data.<br>
 * Although the class itself does not provide any functionality (does not have attributes or methods),
 * it is necessary to have a common class for the {@link LinkedBlockingQueue}s that are used to send control-data-sets between the different modules / threads.
 * 
 * @author Boeck
 */
public interface IControlData extends IData {
}
