import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.LineUnavailableException;

public class WalkieTalkie {

	private static Queue<DatagramPacket> receivedPackets;
	private static Queue<DatagramPacket> recordedPackets;
	private static long duration_s = 60;
	private static InetAddress dest_addr = InetAddress.getLoopbackAddress();
	private static int dest_port = SharedConfig.port;

	private static SendDatagramService sendService;
	private static ReceiveDatagramService receiveService;

	private static RecordToDatagram recorder;
	private static PlayingDatagramPacket player;

	public static void main(String[] args) {

		try {
			argsParser(args);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return;
		}
		
		receivedPackets = new ConcurrentLinkedQueue<>();
		recordedPackets = new ConcurrentLinkedQueue<>();

		try {
			sendService = new SendDatagramService(recordedPackets);
			receiveService = new ReceiveDatagramService(receivedPackets);
			sendService.start();
			receiveService.start();
		} catch (SocketException e2) {
			e2.printStackTrace();
			return;
		}

		try {
			recorder = new RecordToDatagram(recordedPackets, dest_addr, dest_port);
			player = new PlayingDatagramPacket(receivedPackets);
			recorder.start();
			player.start();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
			return;
		}
		
		System.out.println(SharedConfig.format.toString());
		
		try {
			Thread.sleep(duration_s * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		recorder.stop();
		player.stop();
		
		sendService.stop();
		receiveService.stop();
		
		try {
			sendService.join();
			receiveService.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		

	}

	private static void argsParser(String[] args) throws UnknownHostException {

		if (args.length > 0)
			duration_s = Integer.valueOf(args[0]);
		if (args.length > 1)
			dest_addr = InetAddress.getByName(args[1]);
		
		System.out.println("Dest address: " + dest_addr);

	}

}
