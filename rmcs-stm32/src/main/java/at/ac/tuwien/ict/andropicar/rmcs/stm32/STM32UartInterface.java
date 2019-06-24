package at.ac.tuwien.ict.andropicar.rmcs.stm32;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.StopBits;

import at.ac.tuwien.ict.andropicar.rmcs.data.IData;
import at.ac.tuwien.ict.andropicar.rmcs.data.SensorData;
import at.ac.tuwien.ict.andropicar.rmcs.data.DistanceSensorData;

/**
 * This class reads data from the serial interface of the PI, which receives data from the STM32.
 * @author Boeck, Valiko
 */
public class STM32UartInterface extends RMCSSensorComponent{
	
	
	/** The object that is used to listen on the PIs serial interface. */
    private final Serial serial = SerialFactory.createInstance();
    
    /** The minimum time (in ms) that has to pass between processed data, for incoming data to be processed. */
	private int updateInterval = 50;
	
	/** The last time that incoming data was processed. */
    private long lastUpdate = 0;
	

	/**
	 * Initializes Uart0 module on the Raspberry Pi<br>	
	 * <br>
	 * Used Pins: <br>	
	 * - MOSI _ GPIO12 _ Pin 19 <br>	
	 * - MISO _ GPIO13 _ Pin 21 <br>	
	 * - SCK __ GPIO14 _ Pin 23 <br>	
	 * - GND __ ______ _ Pin 25 <br>	
	 * - GND __ ______ _ Pin 6 <br>
	 * - TxD __ GPIO15 _ Pin 8 <br>
	 * - RxD __ GPIO16 _ Pin 10 <br>
	 *  <br>	
	 * for pinout see: <a href="http://pi4j.com/images/j8header.png">Raspberry Pi Pinout</a>
	 * 
	 * DON'T FORGET TO ENABLE UART<br>
	 * (http://www.irrational.net/2012/04/19/using-the-raspberry-pis-serial-port/)<br>
	 * (https://openenergymonitor.org/forum-archive/node/12311.html)
	 * @param sensorDataQueue the sensorDataQueue, where the {@link SensorData} is posted.
	 * @param updateInterval the minimum time (in ms) that has to pass between processed data, for incoming data to be processed.
	 */
	public STM32UartInterface(LinkedBlockingQueue<IData> sensorDataQueue, int updateInterval) {
		
		super(sensorDataQueue);
		initialize(updateInterval);
	}
	
	
	/**
	 * Sets up the PIs serial interface to listen for incoming data, which is interpreted as {@link SensorData} and put on a Queue for further processing.
	 * @param interval the minimum time (in ms) that has to pass between processed data, for incoming data to be processed. Lowest allowed value is 10ms. If it is lower, a default value of 50ms will be used.
	 */
	private void initialize(int interval){
		
		if(updateInterval >= 10)
			this.updateInterval = interval;
		
		// register the serial data listener
        serial.addListener(new SerialDataEventListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {

                // NOTE! - It is extremely important to read the data received from the
                // serial port.  If it does not get read from the receive buffer, the
                // buffer will continue to grow and consume memory.
                try {
                	byte[] SensorData = event.getBytes();
                	if(SensorData.length < 4 || System.currentTimeMillis() - lastUpdate < updateInterval)
                		return;
                	
                	int distanceFront = (Byte.toUnsignedInt(SensorData[0])<<8) + Byte.toUnsignedInt(SensorData[1]);
                	int distanceLeft = Byte.toUnsignedInt(SensorData[2]);
                	int distanceRight = Byte.toUnsignedInt(SensorData[3]);
                	postSensorData(new DistanceSensorData(distanceFront, distanceLeft, distanceRight));
                	lastUpdate = System.currentTimeMillis();
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        try {
                        
        	// open the default serial port provided on the GPIO header
            serial.open(Serial.DEFAULT_COM_PORT, Baud._115200, DataBits._8, 
                        Parity.NONE, StopBits._1, FlowControl.HARDWARE);
            
            System.out.println("Serial interface initialization completed!");
            System.out.println("");
          
        } catch(IOException ex) {
        	System.out.println("****************UART interface initialization returned with errors****************");
            System.out.println(ex.getMessage());
            return;
        }
	}
	
}




