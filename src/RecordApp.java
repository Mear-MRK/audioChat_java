import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.LineUnavailableException;

public class RecordApp {
	public static void main(String[] args) throws SocketException, LineUnavailableException, UnknownHostException {
		
		
		int duration_s = 60;
		if (args.length > 0) {
			duration_s = Integer.valueOf(args[0]);
		}
		
		InetAddress dest = InetAddress.getLoopbackAddress();
		if (args.length > 1) {
			dest = InetAddress.getByName(args[1]);
		}
		
		Queue<DatagramPacket> packetQueue = new ConcurrentLinkedQueue<DatagramPacket>();
		
		SendDatagramService sendService = new SendDatagramService(packetQueue);
		RecordToDatagram recorder = new RecordToDatagram(packetQueue, dest, SharedConfig.port);
		
		sendService.start();
		recorder.start();

		
		try {
			Thread.sleep(duration_s * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
		recorder.stop();
		sendService.stop();
		try {
			recorder.join();
			sendService.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
}
