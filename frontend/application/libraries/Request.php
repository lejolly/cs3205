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

		$result = socket_connect($socket, $address, $port);
		if ($result === false) {
		    throw new Exception("Unable to connect to server: " . socket_strerror(socket_last_error()));
		}

		if(!socket_write($socket, $request, strlen($request))) {
			throw new Exception("Socket write error: " . socket_strerror(socket_last_error()));
		} else {
			// TODO: Make socket reading asynchronous and with timeout
			$response = $this->socket_getline($socket);
			return json_decode($response, true);
		}
	}

	public function get_packet($action, $data, $id) {
		$packet = array();
		$packet['action'] = $action;
		$packet['data'] = $data;
		$packet['error'] = '';
		$packet['id'] = $id;
		$packet['input'] = '';
		return json_encode($packet) . "\r\n";
	}

	public function verify_payload($json_payload, $action, $data_fields) {
		$payload = json_decode($json_payload, true);
		if(!isset($payload['acton'])) {
			throw new Exception('Missing field <action> in response payload');
		} else if(strcmp($payload['action'], $action) != 0) {
			throw new Exception('Expected <action=' . $action . '> in response payload');
		} else {
			if(!isset($payload['data'])) {
				throw new Exception('Missing field <data> in response payload');
			} else {
				$data = array();
				foreach($data_fields as $data_field) {
					if(!isset($payload['data'][$data_field])) {
						throw new Exception('Expected <data[' . $data_field . ']> in response payload');
					} else {
						$data[$data_field] = $payload['data'][$data_field];
					}
				}
				return $data;
			}
		}
	}

	public static function error_output_json($message) {
		$data = array();
		$data['error'] = $message;
		return json_encode($data);
	}

	private function socket_getline($socket) {
	    $response = '';
	    while ($out = socket_read($socket, 1024)) {
	        $response .= $out;
	        if (strpos($response, "\r\n") !== false) break;
	    }
	    return $response;
	}
}
