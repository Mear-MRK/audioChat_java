import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class SendDatagramService implements Runnable {

	private Queue<DatagramPacket> packets;
	private AtomicBoolean isActive = new AtomicBoolean(false);
	final private DatagramSocket socket;
	private Thread thrd;
	private AtomicLong numSentPackets = new AtomicLong();

	public SendDatagramService(Queue<DatagramPacket> packetQueue) throws SocketException {
		socket = new DatagramSocket();
		packets = packetQueue;
		thrd = new Thread(this);
	}

	@Override
	public void run() {

		DatagramPacket dp;

		while (isActive.get()) {
			synchronized (packets) {
				try {
					packets.wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			while ((dp = packets.poll()) != null && isActive.get()) {
				try {
					socket.send(dp);
					numSentPackets.incrementAndGet();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void start() {
		isActive.set(true);
		thrd.start();
		System.out.println("Sending started.");
	}

	public void stop() {
		isActive.set(false);
		socket.close();
		synchronized (packets) {
			packets.notifyAll();
		}
		System.out.println("Sent: " + numSentPackets.toString());
	}

	public long getNumSentPackets() {
		return numSentPackets.get();
	}

	public void join() throws InterruptedException {
		thrd.join();
	}

}
