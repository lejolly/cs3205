<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Authorized extends CI_Controller {
	public function secret() {
		$this->auth->check_auth();

		$data['title'] = "Super Secret Page";
		$data['content'] = $this->parser->parse('display_block', array('title' => 'Super Secret Page', 'content' => 'auth_token: '.$_SESSION['auth_token']), true);
		$this->parser->parse('layout', $data);
	}

	public function index() {
		$this->load->library('request');
		// $this->auth->check_admin();

		$page['title'] = "List of Users";
		$page['content'] = $this->parser->parse('display_block', array('title' => 'List of Users', 'content' => 'test'), true);

		$action = 'retrieve_request';
		$data = array();
		$data['auth_token'] = ''; //$this->auth->get_auth_token();
		$data['csrf_token'] = md5(rand()); // TODO: implement with csrf
		$data['table_id'] = 'users';
		$data['record_id'] = '';
		$id = get_class($this);
		try {
			$packet = $this->request->get_packet($action, $data, $id);
			log_message('debug', '[REQUEST] retrieve_request: ' . $packet);
			$response = $this->request->send_request($packet);
			log_message('debug', '[RESPONSE] retrieve_response: ' . var_export($response, true));
			$data = $this->request->verify_payload($response, 'retrieve_response', array('headers', 'rows'));
			
			$this->parser->parse('layout', $data);
		} catch (Exception $e) {
			log_message('error', 'Exception when trying to retrieve index: ' . $e->getMessage());
			$error = $this->request->get_error($response);
			log_message('error', 'Error message from C2: '.$error);
		}
	}
}
