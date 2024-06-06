import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ReceiveDatagramService implements Runnable {

	private Thread thrd;
	private DatagramSocket socket;
	final int port;
	final int buffSize = SharedConfig.buffSize;
	private AtomicBoolean isActive = new AtomicBoolean(false);
	private Queue<DatagramPacket> packets;
	private AtomicLong numReceivedPackets = new AtomicLong();
	
	public ReceiveDatagramService(Queue<DatagramPacket> packetQueue) throws SocketException {
		packets = packetQueue;
		port = SharedConfig.port;
		socket = new DatagramSocket(port);
		thrd = new Thread(this, "udp_receiver");
	}

	@Override
	public void run() {

		isActive.set(true);
		byte[] buff = new byte[buffSize];
		DatagramPacket dp = new DatagramPacket(buff, buffSize);
		while (isActive.get()) {
			try {
				socket.receive(dp);
				numReceivedPackets.incrementAndGet();
				packets.add(new DatagramPacket(Arrays.copyOf(buff, dp.getLength()), dp.getLength(), dp.getAddress(),
						dp.getPort()));
				synchronized (packets) {
				packets.notifyAll();
				}
			} catch (SocketException e) {
				
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	public boolean isActive() {
		return isActive.get();
	}


	public long getNumReceivedPackets() {
		return numReceivedPackets.get();
	}

	public int getBuffSize() {
		return buffSize;
	}

	public void start() {
		isActive.set(true);
		thrd.start();
		System.out.println("Receiving started. buffSize: " + buffSize + ", port: " + port);
	}
	
	public void stop() {
		isActive.set(false);
		socket.close();
		System.out.println("Num received packets: " + numReceivedPackets.toString());
	}
	
	public void join() throws InterruptedException {
		thrd.join();
	}

}
