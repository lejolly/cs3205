<?php
defined('BASEPATH') OR exit('No direct script access allowed');
session_start();

class Session extends CI_Controller {
	public function verify() {
		$this->load->library('jwt');
		$this->load->library('request');
		$username = $this->input->post('username');
		$password = $this->input->post('password');
		$otp = $this->input->post('otp');

		$packet = $this->get_salt($username);

		$data = array();
		$data['title'] = "JSON Blob";
		$data['content'] = $this->parser->parse('display_block', array('title' => 'JSON Blob', 'content' => $packet), true);
		$this->parser->parse('layout', $data);
		// $payload = $this->get_login_request($username, $password);
		// $jwt = \Firebase\JWT\JWT::encode(array('payload' => $payload), 'secret');

		// if($auth_token = $this->get_auth_token($username, $password)) {
		// 	$_SESSION['auth_token'] = $auth_token;
		// 	redirect('authorized/index');
		// } else {
		// 	$data = array();
		// 	$data['title'] = "JSON Blob";
		// 	$data['content'] = $this->parser->parse('display_block', array('title' => 'JSON Blob', 'content' => $jwt), true);
		// 	$this->parser->parse('layout', $data);
		// }
	}

	public function login() {
		$data = array();
		$data['title'] = "Login";
		$data['content'] = $this->parser->parse('login_form', array(), true);
		$this->parser->parse('layout', $data);
	}

	public function logout() {
		unset($_SESSION['auth_token']);
		redirect('session/login');
	}

	public function get_salt($username = null) {
		error_reporting(0); // All errors handled via Exceptions
		log_message('debug', '[PARAMS] username: ' . $username);
		$this->load->library('request');

		$action = 'salt_request';
		$data = array();
		$data['username'] = $username == null ? '' : $username;
		$data['password'] = 'pass'; // TODO: remove this
		$id = get_class($this);

		try {
			$packet = $this->request->get_packet($action, $data, $id);
			log_message('debug', '[REQUEST] salt_request: ' . $packet);
			$response = $this->request->send_request($packet);
// TODO: signature verification?
			$data = $this->request->verify_payload($response, 'salt_response', array('username', 'salt', 'challenge'));
			$output = json_encode($data);
			log_message('debug', '[OUTPUT] ' . $output);
		} catch(Exception $e) {
			log_message('error', 'Exception when getting user salt: ' . $e->getMessage());
			$output = Request::error_output_json('Unable to retrieve user salt, perhaps the server is down?');
			log_message('debug', '[OUTPUT] ' . $output);
		}

		$this->output->set_content_type('application/json');
        $this->output->set_output($output);
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

		$response = $this->socket_getline($socket);
		$packet = json_decode($response, true);
		$_SESSION['data'] = json_decode($this->socket_getline($socket), true);
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
		$packet['error'] = '';
		$packet['id'] = get_class($this);
		$packet['input'] = '';
		return json_encode($packet)."\r\n";
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
