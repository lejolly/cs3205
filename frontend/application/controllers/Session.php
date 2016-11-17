<?php
defined('BASEPATH') OR exit('No direct script access allowed');

@session_start();
class Session extends CI_Controller {
	public function login() {
		$page = array();
		$page['title'] = "Login";
		$page['contents'] = $this->load->view('login/form', array(), true);
		$this->load->view('layout', $page);
	}

	public function logout() {
		session_destroy();
		redirect('session/login');
	}

	public function verify() {
		$username = $this->input->post('username');
		$challenge = $this->input->post('challenge');
		$response = $this->input->post('response');
		$csrf_token = $this->input->post('csrf_token');
		$otp = $this->input->post('otp');
		log_message('debug', '[PARAMS] username = ' . $username);
		log_message('debug', '[PARAMS] challenge = ' . $challenge);
		log_message('debug', '[PARAMS] response = ' . $response);
		log_message('debug', '[PARAMS] csrf_token = ' . $csrf_token);
		log_message('debug', '[PARAMS] otp = ' . $otp);

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
				$response = $this->request->send_request($packet);
				$data = $this->request->verify_payload($response, 'login_response', array('auth_token', 'username', 'role', 'csrf_token'));
				$_SESSION['auth_token'] = $data['auth_token'];
				$_SESSION['role'] = $data['role'];
				$_SESSION['username'] = $data['username'];
				$_SESSION['csrf_token'] = $data['csrf_token'];
				$output = json_encode($data);
			} catch (Exception $e) {
				log_message('error', 'Exception when trying to login: ' . $e->getMessage());
				$output = Request::error_output_json('Unable to login, ' . $e->getMessage());
			}

			log_message('debug', '[OUTPUT] ' . $output);
			$this->output->set_content_type('application/json');
        	$this->output->set_output($output);
		}
	}

	public function get_salt($username = '') {
		try {
			$action = 'salt_request';
			$data['username'] = $username;
			$id = get_class($this);
			$packet = $this->request->get_packet($action, $data, $id);
			$response = $this->request->send_request($packet);
			$data = $this->request->verify_payload($response, 'salt_response', array('username', 'salt', 'challenge', 'csrf_token'));
			$output = json_encode($data);
		} catch(Exception $e) {
			log_message('error', 'Exception when getting user salt: ' . $e->getMessage());
			$output = Request::error_output_json('Unable to retrieve user salt, perhaps the server is down?');
			
		}

		log_message('debug', '[OUTPUT] ' . $output);
		$this->output->set_content_type('application/json');
        $this->output->set_output($output);
	}
}
