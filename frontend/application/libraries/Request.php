<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Request {
	public function send_request($request) {
		$port = '8081';
		$address = '127.0.0.1';
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);

		if ($socket === false) {
		    throw new Exception("Unable to create socket" . socket_strerror(socket_last_error()));
		}

		@$result = socket_connect($socket, $address, $port);
		if ($result === false) {
		    throw new Exception("Unable to connect to server: " . socket_strerror(socket_last_error()));
		}

		if(!socket_write($socket, $request, strlen($request))) {
			throw new Exception("Socket write error: " . socket_strerror(socket_last_error()));
		} else {
			$response = $this->socket_getline($socket);
			if($response == null) {
				throw new Exception('NULL response');
			} else {
				return $response;
			}
		}
	}

	public function get_packet($action, $data, $id = '', $rows = array()) {
		$packet = array();
		$packet['action'] = $action;
		$packet['data'] = $data;
		if(isset($_SESSION['csrf_token'])) $packet['data']['csrf_token'] = $_SESSION['csrf_token'];
		$packet['error'] = '';
		$packet['id'] = $id;
		$packet['input'] = '';
		$packet['rows'] = $rows;
		$jws = get_instance()->jwt->get_jws($packet);
		log_message('debug', '[REQUEST] raw: ' . json_encode($packet));
		log_message('debug', '[REQUEST] jws: ' . $jws);
		return $jws . "\r\n";
	}

	public function verify_payload($jws, $action, $data_fields, $toplevel_fields = array()) {
		$payload = json_decode(Jwt::verify_signature($jws)['message'], true);

		if(!isset($payload['action'])) {
			throw new Exception('Missing field <action> in response payload');
		} else if(strcmp($payload['action'], 'not_logged_in_response') == 0) {
			redirect('logout');
		}

		if(strcmp($payload['action'], $action) != 0) {
			throw new Exception('Expected <action=' . $action . '> in response payload');
		}
		
		if(!isset($payload['data'])) {
			throw new Exception('Missing field <data> in response payload');
		}

		$data = array();

		if(isset($payload['error']) && !empty($payload['error'])) {
			throw new Exception($payload['error']);
		}

		foreach($data_fields as $data_field) {
			if(!isset($payload['data'][$data_field])) {
				throw new Exception('Expected <data[' . $data_field . ']> in response payload');
			}
			$data[$data_field] = $payload['data'][$data_field];
		}

		foreach($toplevel_fields as $toplevel_field) {
			if(!isset($payload[$toplevel_field])) {
				throw new Exception('Expected <' . $toplevel_field . '> in response payload');
			}
			$data[$toplevel_field] = $payload[$toplevel_field];
		}

		if(isset($payload['data']['csrf_token'])) {
			$_SESSION['csrf_token'] = $payload['data']['csrf_token'];
		}

		return $data;
	}

	public static function error_output_json($message) {
		$data = array();
		$data['error'] = $message;
		return json_encode($data);
	}

	private function socket_getline($socket) {
		socket_set_nonblock($socket);
		$start = time();
		$timeout = 3;
	    $response = '';
	    log_message('debug', 'Socket read start: ' . $start);

	    while (true) {
	    	$data = socket_read($socket, 1024);

	    	if($data) {
	    		$response .= $data;
	    	}

	        if (strpos($data, "\r\n") !== false) {
	        	return $response;
	        } else if(time() > $start + $timeout) {
	        	log_message('error', 'Server response timeout');
	        	break;
	        }
	    }
	    return false;
	}
}
