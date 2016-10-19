<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Session extends CI_Controller {
	public function verify() {
		$this->load->library('JWT_wrapper');
		$username = $this->input->post('username');
		$password = $this->input->post('password');
		$payload = $this->get_login_request($username, $password);
		$jwt = \Firebase\JWT\JWT::encode(array('payload' => $payload), 'secret');

		$data = array();
		$data['title'] = "JSON Blob";
		$data['content'] = $this->parser->parse('display_block', array('title' => 'JSON Blob', 'content' => $jwt), true);
		$this->parser->parse('layout', $data);
	}

	public function login()
	{
		$data = array();
		$data['title'] = "Login";
		$data['content'] = $this->parser->parse('login_form', array(), true);
		$this->parser->parse('layout', $data);
	}

	private function get_csrf() {

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
}
