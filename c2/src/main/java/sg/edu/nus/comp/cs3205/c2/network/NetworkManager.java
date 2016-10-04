package sg.edu.nus.comp.cs3205.c2.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkManager {
	public static int PORT = 13205;
	public static int BUFFER_SIZE = 4096;

	private static Logger logger = LoggerFactory.getLogger(NetworkManager.class);

	private DatagramSocket socket;

	public NetworkManager() throws SocketException {
		socket = new DatagramSocket(PORT);

		while (true) {
			try {
				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				new PacketHandlerThread(packet).run();
			} catch (IOException e) {
				logger.error("IOException encountered when receiving packet");
				e.printStackTrace();
				break;
			}
		}
	}
}
