import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.LineUnavailableException;

public class PlayApp {

	public static void main(String[] args) throws SocketException, LineUnavailableException {

		Queue<DatagramPacket> packetQueue = new ConcurrentLinkedQueue<DatagramPacket>();
		ReceiveDatagramService rcvSrv = new ReceiveDatagramService(packetQueue);
		PlayingDatagramPacket player = new PlayingDatagramPacket(packetQueue);

		rcvSrv.start();
		player.start();
		
		System.out.println(SharedConfig.format.toString());
		
		int duration_s = 60;
		if (args.length > 0) {
			duration_s = Integer.valueOf(args[0]);
		}

		try {
			Thread.sleep(duration_s*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		player.stop();
		rcvSrv.stop();

		try {
			player.join();
			rcvSrv.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
