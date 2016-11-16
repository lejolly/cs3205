<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Admin extends CI_Controller {
	const TABLE_ID = 'users';

	public function user_index() {
		$this->auth->check_admin();

		try {
			$action = 'retrieve_request';
			$data = array();
			$data['auth_token'] = $this->auth->get_auth_token();
			$data['csrf_token'] = $this->auth->get_csrf_token();
			$data['table_id'] = self::TABLE_ID;
			$id = get_class($this);

			$packet = $this->request->get_packet($action, $data, $id);
			$response = $this->request->send_request($packet);
			$payload = $this->request->verify_payload($response, 'retrieve_response', array(), array('rows'));
			
			$page['title'] = Strings::ADMIN_USER_INDEX_TITLE;
			$page['contents'] = $this->load->view('users/table', $payload, true);
			$this->load->view('layout', $page);
		} catch (Exception $e) {
			log_message('error', 'Exception when trying to retrieve index: ' . $e->getMessage());
			$page['title'] = Strings::ADMIN_USER_INDEX_TITLE;
			$page['contents'] = $this->load->view('error_block', array(), true);
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
		$challenge = $this->input->post('challenge');

		if($full_name != null && $username != null && $salt != null && $hash != null && $number != null) {
			try {
				$action = 'create_request';
				$data['auth_token'] = $this->auth->get_auth_token();
				$data['csrf_token'] = $this->auth->get_csrf_token();
				$data['table_id'] = 'users';
				$data = array_merge($data, compact('full_name', 'username', 'salt', 'hash', 'number', 'role'));
				$id = get_class($this);

				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'create_response', array());
				
				$page['title'] = Strings::ADMIN_USER_ADD_CONFIRM_TITLE;
				$page['contents'] = $this->load->view('users/form_add_confirm', compact('full_name', 'username', 'salt', 'hash', 'number', 'role'), true);
				$this->load->view('layout', $page);
			} catch(Exception $e) {
				log_message('error', 'Exception when trying to create user: ' . $e->getMessage());
				$_SESSION['flash'] = $this->utils->danger_alert_html(Strings::ADMIN_USER_ADD_ERROR);
				redirect('admin/users');
			}
		} else if($challenge != null && $username != null) {
			try {
				$action = 'sms_challenge';
				$data['auth_token'] = $this->auth->get_auth_token();
				$data['csrf_token'] = $this->auth->get_csrf_token();
				$data['username'] = $username;
				$data['challenge'] = $challenge;
				$data['action'] = 'create';
				$id = get_class($this);

				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'sms_result', array('result'));
				if(strcmp($payload['result'], 'true') == 0) {
					$_SESSION['flash'] = $this->utils->success_alert_html(Strings::ADMIN_USER_ADD_SUCCESS);
				} else {
					$_SESSION['flash'] = $this->utils->danger_alert_html(Strings::ADMIN_USER_ADD_ERROR_SMS);
				}
			} catch (Exception $e) {
				log_message('error', 'Exception when sending SMS challenge: ' . $e->getMessage());
				$_SESSION['flash'] = $this->utils->danger_alert_html(Strings::ADMIN_USER_ADD_ERROR);
				redirect('admin/users');
			}
		} else {
			$page['title'] = Strings::ADMIN_USER_ADD_TITLE;
			$page['contents'] = $this->load->view('users/form_add', null, true);
			$this->load->view('layout', $page);
		}
	}

	public function user_edit($user_id) {
		$this->auth->check_admin();

		$full_name = $this->input->post('full_name');
		$number = $this->input->post('number');
		$username = $this->input->post('username');
		if($full_name != null && $number != null) {
			log_message('debug', '[PARAMS] full_name = ' . $full_name);
			log_message('debug', '[PARAMS] number = ' . $number);
			log_message('debug', '[PARAMS] username = ' . $username);
			$action = 'update_request';
			$data['auth_token'] = $this->auth->get_auth_token();
			$data['csrf_token'] = $this->auth->get_csrf_token();
			$data['table_id'] = self::TABLE_ID;
			$data = array_merge($data, compact('full_name', 'number', 'username'));
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

	public function user_delete($user_id) {
		$challenge = $this->input->post('challenge');
		$username = $this->input->post('username');

		if($challenge != null && $username != null) {
			try {
				$action = 'sms_challenge';
				$data['auth_token'] = $this->auth->get_auth_token();
				$data['csrf_token'] = $this->auth->get_csrf_token();
				$data['username'] = $username;
				$data['challenge'] = $challenge;
				$data['action'] = 'delete';
				$id = get_class($this);

				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'sms_result', array());
				if(strcmp($payload, 'true') == 0) {
					$_SESSION['flash'] = $this->utils->success_alert_html('User deleted');
				} else {
					$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to delete user, incorrect SMS Code');
				}
				redirect('admin/users');
			} catch(Exception $e) {
				log_message('error', 'Exception when sending SMS challenge');
				$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to delete user');
				redirect('admin/users');
			}
		} else {
			try {
				$action = 'retrieve_request';
				$data['auth_token'] = $this->auth->get_auth_token();
				$data['csrf_token'] = $this->auth->get_csrf_token();
				$data['table_id'] = self::TABLE_ID;
				$data['record_id'] = $user_id;
				$id = get_class($this);

				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'retrieve_response', array(), array('rows'));

				$action = 'delete_request';
				$data['username'] = $payload['rows'][0]['username'];
				$packet = $this->request->get_packet($action, $data, $id);
				$this->request->send_request($packet);

				$page['title'] = 'Confirm Delete User';
				$page['contents'] = $this->load->view('users/form_delete', $payload['rows'][0], true);
				$this->load->view('layout', $page);
			} catch(Exception $e) {
				log_message('error', 'Exception when retrieving user');
				$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to retrieve user details');
				redirect('admin/users');
			}
		}
	}
}
