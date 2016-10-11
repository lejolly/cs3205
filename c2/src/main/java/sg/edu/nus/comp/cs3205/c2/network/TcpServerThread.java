package sg.edu.nus.comp.cs3205.c2.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerThread extends Thread {
	private Logger logger = LoggerFactory.getLogger(TcpServerThread.class);
	private ServerSocket server = null;
	
	public TcpServerThread(int port) throws IOException, IllegalArgumentException {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			logger.error("IOException when creating ServerSocket");
			throw e;
		} catch (IllegalArgumentException e) {
			logger.error("Invalid port number [0, 65535]");
			throw e;
		}
	}
	
	public int getPort() {
		return server.getLocalPort();
	}
	
	public void stopServer() {
		try {
			server.close();
			logger.info("TcpServerThread stopping");
		} catch (IOException e) {
			logger.error("IOException when closing ServerSocket");
		}
	}
	
	@Override
	public void run() {
		logger.info("(" + this.getId() + ") TcpServerThread started");
		while(true) {
			try {
				Socket socket = server.accept();
				try {
					
				} finally {
					socket.close();
				}
			} catch (IOException e) {
				logger.error("IOException when accepting connection");
				break;
			} finally {
				try {
					server.close();
				} catch (IOException e) {
					logger.error("IOException when closing ServerSocket");
					break;
				}
			}
		}
		logger.info("(" + this.getId() + ") TcpServerThread stopped");
	}
}
