import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class RecordToDatagram implements Runnable {
	private Thread thrd;
	final private TargetDataLine line;
	InetAddress dest;
	final int port;
	final int buffSize = SharedConfig.buffSize;
	final AudioFormat format = SharedConfig.format;
	final private byte[] buff = new byte[buffSize];
	AtomicBoolean recording = new AtomicBoolean(false);
	Queue<DatagramPacket> recPackets;
	private long numRecordedPackets = 0;

	public RecordToDatagram(Queue<DatagramPacket> packetQueue, InetAddress dest_addr, int dest_port) throws LineUnavailableException {
		recPackets = packetQueue;
		line = AudioSystem.getTargetDataLine(format);
		this.dest = dest_addr;
		this.port = dest_port;
		thrd = new Thread(this);
	}

	public void start() throws LineUnavailableException {
		line.open();
		recording.set(true);
		thrd.start();
		System.out.println("Format: " + format);
		System.out.println("buffSize: " + buffSize);
	}

	public void stop() {
		recording.set(false);
		line.close();
		synchronized (recPackets) {
			recPackets.notifyAll();
		}
	}

	@Override
	public void run() {
		
		line.start();
		System.out.println("Recording started.");

		while (recording.get()) {
			int numByteRead = line.read(buff, 0, buffSize);
			byte[] copyBuff = Arrays.copyOf(buff, numByteRead);
			DatagramPacket dp = new DatagramPacket(copyBuff, numByteRead, dest, port);
			numRecordedPackets++;
			recPackets.add(dp);
			synchronized (recPackets) {
				recPackets.notifyAll();
			}
		}
		

		System.out.println("Num recorded packets: " + numRecordedPackets);
	}
	
	public synchronized long  getNumRecordedPackets() {
		return numRecordedPackets;
	}

	public void join() throws InterruptedException {
		thrd.join();
	}
}
