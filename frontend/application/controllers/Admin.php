<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Admin extends CI_Controller {
	const TABLE_ID = 'users';

	public function user_index() {
		$this->auth->check_admin();

		$action = 'retrieve_request';
		$data = array();
		$data['auth_token'] = $this->auth->get_auth_token();
		$data['csrf_token'] = md5(rand()); // TODO: implement with csrf
		$data['table_id'] = self::TABLE_ID;
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
			$page['title'] = 'Error';
			$page['contents'] = $this->load->view('error_block', array('error' => 'none'), true);
			$this->load->view('layout', $page);
		}
	}

	public function user_add() {
		$this->auth->check_admin();

		$full_name = $this->input->post('full_name');
		$username = $this->input->post('username');
		$salt = $this->input->post('salt');
		$hash = $this->input->post('hash');
		$number = $this->input->post('number');
		$role = $this->input->post('role');
		if($full_name != null && $username != null && $salt != null && $hash != null && $number != null) {
			log_message('debug', '[PARAMS] full_name='.$full_name);
			log_message('debug', '[PARAMS] username='.$username);
			log_message('debug', '[PARAMS] salt='.$salt);
			log_message('debug', '[PARAMS] hash='.$hash);
			log_message('debug', '[PARAMS] number='.$number);
			log_message('debug', '[PARAMS] role='.$role);
			$action = 'create_request';
			$data['auth_token'] = $this->auth->get_auth_token();
			$data['csrf_token'] = $this->auth->get_csrf_token();
			$data['table_id'] = 'users';
			$data = array_merge($data, compact('full_name', 'username', 'salt', 'hash', 'number', 'role'));
			$id = get_class($this);

			try {
				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'retrieve_response', array());
				$_SESSION['flash'] = $this->utils->success_alert_html('Created new user');
				redirect('admin/users');
			} catch(Exception $e) {
				log_message('error', 'Exception when trying to create user: ' . $e->getMessage());
				$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to create new user');
			}
		}

		$page['title'] = 'Add New User';
		$page['contents'] = $this->load->view('users/form_add', null, true);
		$this->load->view('layout', $page);
	}

	public function user_edit($user_id) {
		$this->auth->check_admin();

		$full_name = $this->input->post('full_name');
		$number = $this->input->post('number');
		$role = $this->input->post('role');
		$username = $this->input->post('username');
		if($full_name != null && $number != null && $role != null) {
			log_message('debug', '[PARAMS] full_name = ' . $full_name);
			log_message('debug', '[PARAMS] number = ' . $number);
			log_message('debug', '[PARAMS] role = ' . $role);
			log_message('debug', '[PARAMS] username = ' . $username);
			$action = 'update_request';
			$data['auth_token'] = $this->auth->get_auth_token();
			$data['csrf_token'] = $this->auth->get_csrf_token();
			$data['table_id'] = self::TABLE_ID;
			$data = array_merge($data, compact('full_name', 'number', 'role', 'username'));
			$id = get_class($this);

			try {
				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'update_response', array());
				$_SESSION['flash'] = $this->utils->success_alert_html('User info updated');
				redirect('admin/users');
			} catch (Exception $e) {
				log_message('error', 'Exception when trying to update user: ' . $e->getMessage());
				$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to update user info');
				$page['title'] = 'Update User';
				$page['contents'] = $this->load->view('users/form_edit', compact('full_name', 'number', 'role'), true);
				$this->load->view('layout', $page);
			}
		} else {
			$action = 'retrieve_request';
			$data['auth_token'] = $this->auth->get_auth_token();
			$data['cstf_token'] = $this->auth->get_csrf_token();
			$data['table_id'] = self::TABLE_ID;
			$data['record_id'] = $user_id;
			$id = get_class($this);

			try {
				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'retrieve_response', array(), array('rows'));
				$page['title'] = 'Update User';
				$page['contents'] = $this->load->view('users/form_edit', $payload['rows'][0], true);
				$this->load->view('layout', $page);
			} catch(Exception $e) {
				log_message('error', 'Exception when retrieving user: ' . $e->getMessage());
				$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to retrieve user details');
				redirect('admin/users');
			}
		}
	}
}
