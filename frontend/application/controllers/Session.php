<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Session extends CI_Controller {
	public function login() {
		$data = array();
		$data['title'] = "Login";
		$data['content'] = $this->parser->parse('login_form', array(), true);
		$this->parser->parse('layout', $data);
	}

	public function logout() {
		delete_cookie('auth_token');
		redirect('session/login');
	}

	public function verify() {
		$this->load->library('jwt');
		$this->load->library('request');
		$username = $this->input->post('username');
		$challenge = $this->input->post('challenge');
		$response = $this->input->post('response');
		$csrf_token = $this->input->post('csrf_token');
		$otp = $this->input->post('otp');
		log_message('debug', '[PARAMS] username=' . $username);
		log_message('debug', '[PARAMS] challenge=' . $challenge);
		log_message('debug', '[PARAMS] response=' . $response);
		log_message('debug', '[PARAMS] csrf_token=' . $csrf_token);
		log_message('debug', '[PARAMS] otp=' . $otp);

		if($username == null || $challenge == null || $response == null || $csrf_token == null || $otp == null) {
			log_message('debug', '[PARAMS] One or more credentials missing');
			$output = Request::error_output_json('Invalid credentials provided. Please check that your username, password and OTP have been correctly entered.');
			$this->output->set_content_type('application/json');
        	$this->output->set_output($output);
		} else {
			$action = 'login_request';
			$data = array();
			$data['username'] = $username;
			$data['challenge'] = $challenge;
			$data['response'] = $response;
			$data['csrf_token'] = $csrf_token;
			$data['otp'] = $otp;
			$id = get_class($this);

			try {
				$packet = $this->request->get_packet($action, $data, $id);
				log_message('debug', '[REQUEST] login_request: ' . $packet);
				$response = $this->request->send_request($packet);
				log_message('debug', '[RESPONSE] login_response: ' . var_export($response, true));
// TODO: signature verification?
				$data = $this->request->verify_payload($response, 'login_response', array('auth_token', 'csrf_token'));
				set_cookie('auth_token', $data['auth_token']);
				redirect('authorized/index');
			} catch (Exception $e) {
				log_message('error', 'Exception when trying to login: ' . $e->getMessage());
				$output = Request::error_output_json('Unable to login, perhaps the server is down?');
				log_message('debug', '[OUTPUT] ' . $output);
				$this->output->set_content_type('application/json');
        		$this->output->set_output($output);
			}
			
		}
	}

	public function get_salt($username = null) {
		//error_reporting(0); // All errors handled via Exceptions
		log_message('debug', '[PARAMS] username: ' . $username);
		$this->load->library('request');

		$action = 'salt_request';
		$data = array();
		$data['username'] = $username == null ? '' : $username;
		$id = get_class($this);

		try {
			$packet = $this->request->get_packet($action, $data, $id);
			log_message('debug', '[REQUEST] salt_request: ' . $packet);
			$response = $this->request->send_request($packet);
			log_message('debug', '[RESPONSE] salt_response: ' . var_export($response, true));
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
}
