<?php defined('BASEPATH') OR exit('No direct script access allowed');

include 'phpseclib/Crypt/RSA.php';
include 'phpseclib/Math/BigInteger.php';

class Jwt {
	const C1_PRIVATE_KEYFILE = '../backend/keys/c1_id_rsa';
	const C2_PUBLIC_KEYFILE = '../backend/keys/c2_id_rsa.pub';

	public static function get_jws($packet) {
		$headers = ['alg' => 'RS512'];
		$headers_enc = base64_encode(json_encode($headers));

		$payload['message'] = json_encode($packet);
		$payload_enc = base64_encode(json_encode($payload));

		$priv_key = Jwt::get_priv_key();
		$signature = '';
		if(!openssl_sign("$headers_enc.$payload_enc", $signature, $priv_key, OPENSSL_ALGO_SHA512)) {
			throw new Exception('Error when creating signed JWT');
		}
		$signature_enc = base64_encode($signature);

		$jws = "$headers_enc.$payload_enc.$signature_enc";
		return $jws;
	}

	public static function verify_signature($packet) {
		log_message('debug', '[RESPONSE] raw: ' . $packet);
		$parts = explode('.', $packet);
		$pub_key = Jwt::get_pub_key();
		
		if(count($parts) != 3) {
			goto fail;
		} else {
			log_message('debug', '[HEADERS] ' . base64_decode($parts[0]));
			log_message('debug', '[PAYLOAD] ' . base64_decode($parts[1]));
		}

		if(!openssl_verify($parts[0].'.'.$parts[1], base64_decode($parts[2]), $pub_key, OPENSSL_ALGO_SHA512)) goto fail;

		return json_decode(base64_decode($parts[1]), true);

		fail: {
			log_message('debug', '[OPENSSL_ERROR] ' . openssl_error_string());
			throw new Exception('Signed JWT failed to validate');
		}
	}

	private static function get_priv_key() {
		if(!is_readable(Jwt::C1_PRIVATE_KEYFILE)) {
			throw new Exception('Invalid or missing C1 Private KEYFILE: ' . getcwd());
		}

		$contents = file_get_contents(Jwt::C1_PRIVATE_KEYFILE);
		$key = openssl_get_privatekey($contents, '');

		if(!$key) {
			throw new Exception('Unable to read C1 private key: ' . openssl_error_string());
		} else {
			$details = openssl_pkey_get_details($key);
			if(!isset($details['key']) || $details['type'] !== OPENSSL_KEYTYPE_RSA) {
				throw new Exception('C1 Private key is not compatible with RSA signatures');
			}
		}

		return $key;
	}

	private static function get_pub_key() {
		if(!is_readable(Jwt::C2_PUBLIC_KEYFILE)) {
			throw new Exception('Invalid or missing C2 Public KEYFILE: ' . getcwd());
		}

		$contents = file_get_contents(Jwt::C2_PUBLIC_KEYFILE);
		// $pubKey = openssl_pkey_get_public($contents);
		// $key = openssl_pkey_get_details($pubKey)['key'];
		$key = openssl_get_publickey($contents);
		log_message('debug', '[PUBLIC KEY] ' . openssl_pkey_get_details($key)['key']);
		// log_message('debug', '[PUBLIC KEY] ' . $key);
		// log_message('debug', '[PUBLIC KEY] ' . var_export($key, true));

		return $key;
	}
}
