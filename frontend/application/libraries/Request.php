<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Request {

	public function send_request($request) {
		log_message('debug', '[REQUEST] '.$request);

		$port = '8081';
		$address = '127.0.0.1';
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);

		if ($socket === false) {
		    throw new Exception("Unable to create socket" . socket_strerror(socket_last_error()));
		}

		$result = socket_connect($socket, $address, $port);
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
				log_message('debug', '[RESPONSE] '.$response);
				return $response;
			}
		}
	}

	public function get_packet($action, $data, $id = '', $rows = array()) {
		$packet = array();
		$packet['action'] = $action;
		$packet['data'] = $data;
		$packet['error'] = '';
		$packet['id'] = $id;
		$packet['input'] = '';
		$packet['rows'] = $rows;
		return json_encode($packet) . "\r\n";
	}

	public function verify_payload($json_payload, $action, $data_fields, $toplevel_fields = array()) {
		$payload = json_decode($json_payload, true);
		if(!isset($payload['action'])) {
			throw new Exception('Missing field <action> in response payload');
		}

		if(strcmp($payload['action'], $action) != 0) {
			throw new Exception('Expected <action=' . $action . '> in response payload');
		}
		
		if(!isset($payload['data'])) {
			throw new Exception('Missing field <data> in response payload');
		}

		$data = array();

		if(isset($payload['error']) && !empty($payload['error'])) {
			throw new Exception('Error field is populated: ' . $payload['error']);
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
		return $data;
	}

	public function get_error($json_payload) {
		$payload = json_decode($json_payload, true);
		if(!isset($payload['error']) || empty($payload['error'])) {
			return null;
		} else {
			return $payload['error'];
		}
	}

	public static function error_output_json($message) {
		$data = array();
		$data['error'] = $message;
		return json_encode($data);
	}

	private function socket_getline($socket) {
		socket_set_nonblock($socket);
		$start = time();
		$timeout = 1;
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
