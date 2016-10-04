package sg.edu.nus.comp.cs3205.c2.network;

import java.security.Key;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

public class PacketHandlerThreadTest {
	private static Logger logger = LoggerFactory.getLogger(PacketHandlerThreadTest.class);

	@Test
	public void test() {
		Key key = MacProvider.generateKey();

		String jwt = Jwts.builder()
				.claim("test", 123)
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();
		logger.debug(String.format("jwt: %s", jwt));
	}

}
