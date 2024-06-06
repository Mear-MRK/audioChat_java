import java.net.DatagramPacket;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class PlayingDatagramPacket implements Runnable {

	private Thread thrd;
	final private SourceDataLine line;
	final AudioFormat format;
	AtomicBoolean playing = new AtomicBoolean(false);
	private Queue<DatagramPacket> packets;

	public PlayingDatagramPacket(Queue<DatagramPacket> packetQueue) throws LineUnavailableException {
		packets = packetQueue;
		format = SharedConfig.format;
		line = AudioSystem.getSourceDataLine(format);
		line.open();
		thrd = new Thread(this, "Playing thread");
	}

	public void start() {
		line.start();
		playing.set(true);
		thrd.start();
		System.out.println("Playing started.");
	}

	public void stop() {
		playing.set(false);
		line.close();
		synchronized (packets) {
			packets.notifyAll();
		}
		
	}

	@Override
	public void run() {

		while (playing.get()) {
			synchronized (packets) {
				try {
					packets.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			DatagramPacket dp;
			while ((dp = packets.poll()) != null && playing.get()) {
				line.write(dp.getData(), 0, dp.getLength());
			}

		}

	}

	public void join() throws InterruptedException {
		thrd.join();
	}
}
