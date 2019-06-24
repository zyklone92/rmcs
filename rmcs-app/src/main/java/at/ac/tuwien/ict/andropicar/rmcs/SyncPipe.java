package at.ac.tuwien.ict.andropicar.rmcs;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * The SyncPipe takes an {@link InputStream} and pipes it to the specified {@link OutputStream}.
 * @author Boeck
 *
 */
class SyncPipe implements Runnable {
	
	private final OutputStream ostrm_;
	private final InputStream istrm_;
	  
	/**
	 * Instantiates a new Object of the SyncPipe with the specified Streams.
	 * @param istrm	the {@link InputStream} that is being piped to the given {@link OutputStream}
	 * @param ostrm the {@link OutputStream} that is the target for the given {@link InputStream}
	 */
	public SyncPipe(InputStream istrm, OutputStream ostrm) {
		istrm_ = istrm;
		ostrm_ = ostrm;
	}
	
	
	public void run() {
		try{
			final byte[] buffer = new byte[1024];
			for (int length = 0; (length = istrm_.read(buffer)) != -1; ) {
				ostrm_.write(buffer, 0, length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}