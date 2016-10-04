package sg.edu.nus.comp.cs3205.c2.network;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

public class PacketHandlerThread extends Thread {
	final private static Logger logger = LoggerFactory.getLogger(PacketHandlerThread.class);

	final private DatagramPacket packet;

	private Claims body;
	private Header header;

	public PacketHandlerThread(DatagramPacket packet) {
		this.packet = packet;
	}

	@Override
	public void run() {
		byte[] data = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getLength());
		String jwt = new String(data);
		Jws<Claims> token = null;
		try {
			token = Jwts.parser().parseClaimsJws(jwt);
		} catch (ExpiredJwtException e) {

		} catch (MalformedJwtException e) {

		} catch (SignatureException e) {

		} catch (IllegalArgumentException e) {

		}
		if (token != null) {
			body = token.getBody();
			header = token.getHeader();
		}
	}
}
