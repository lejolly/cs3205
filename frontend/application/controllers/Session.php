<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Session extends CI_Controller {
	public function verify() {
		$this->load->library('JWT_wrapper');
		$username = $this->input->post('username');
		$password = $this->input->post('password');
		$payload = $this->get_login_request($username, $password);
		$jwt = \Firebase\JWT\JWT::encode(array('payload' => $payload), 'secret');

		if($auth_token = $this->get_auth_token($username, $password)) {
			redirect('authorized/index');
		} else {
			$data = array();
			$data['title'] = "JSON Blob";
			$data['content'] = $this->parser->parse('display_block', array('title' => 'JSON Blob', 'content' => $jwt), true);
			$this->parser->parse('layout', $data);
		}
	}

	public function login()
	{
		$data = array();
		$data['title'] = "Login";
		$data['content'] = $this->parser->parse('login_form', array(), true);
		$this->parser->parse('layout', $data);
	}

	private function get_auth_token($username, $password) {
		$request = $this->get_login_request($username, $password);

		$service_port = '8081';
		$address = '127.0.0.1';
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		if ($socket === false) {
		    return null;
		}

		$result = socket_connect($socket, $address, $service_port);
		if ($result === false) {
		    return null;
		}
		socket_write($socket, $request, strlen($request));

		$response = socket_getline($socket);
		$packet = json_decode($response, true);
		return $packet['data']['auth_token'];
	}

	private function get_login_request($username, $password, $csrf_token = 'test') {
		$packet = array();
		$packet['action'] = 'login_request';
		$packet['data'] = array(
			'username' => $username,
			'password' => $password,
			'csrf_token' => $csrf_token
		);
		$packet['error'] = null;
		$packet['id'] = get_class($this);
		$packet['input'] = null;
		return json_encode($packet);
	}

	function socket_getline($socket) {
	    $response = '';
	    while ($out = socket_read($socket, 1024)) {
	        $response .= $out;
	        if (strpos($response, "\r\n") !== false) break;
	    }
	    return $response;
	}
}
