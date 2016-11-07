<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Admin extends CI_Controller {

	public function user_index() {
		$this->load->library('request');
		// $this->auth->check_admin();

		$action = 'retrieve_request';
		$data = array();
		$data['auth_token'] = $this->auth->get_auth_token();
		$data['csrf_token'] = md5(rand()); // TODO: implement with csrf
		$data['table_id'] = 'users';
		$data['record_id'] = '';
		$id = get_class($this);

		try {
			$packet = $this->request->get_packet($action, $data, $id);
			log_message('debug', '[REQUEST] retrieve_request: ' . $packet);
			$response = $this->request->send_request($packet);
			log_message('debug', '[RESPONSE] retrieve_response: ' . var_export($response, true));
			$payload = $this->request->verify_payload($response, 'retrieve_response', array(), array('rows'));
			
			$page['title'] = "List of Users";
			$page['contents'] = $this->load->view('users/table', $payload, true);
			$this->parser->parse('layout', $page);
		} catch (Exception $e) {
			log_message('error', 'Exception when trying to retrieve index: ' . $e->getMessage());
			$error = $this->request->get_error($response);
			log_message('error', 'Error message from C2: '.$error);
			$page['title'] = 'Error';
			$page['contents'] = $this->load->view('error_block', compact($error), true);
			$this->load->view('layout', $page);
		}
	}

	public function user_add() {
		$this->load->view('layout');
	}
}
