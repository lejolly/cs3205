<?php
defined('BASEPATH') OR exit('No direct script access allowed');

@session_start();
class Auth {
	private $CI;

	public function __construct() {
		$this->CI = &get_instance();
	}

	public function is_logged_in() {
		return isset($_SESSION['auth_token']) && !empty($_SESSION['auth_token']);
	}

	public function is_admin() {
		return isset($_SESSION['role']) && $_SESSION['role'] === 'admin';
	}

	public function check_auth() {
		if(!$this->is_logged_in()) {
			$_SESSION['flash'] = $this->CI->utils->danger_alert_html('You must be logged in to view this page');
			redirect('session/login');
		}
	}

	public function check_admin() {
		$this->check_auth();
		if(!$this->is_admin()) {
			$_SESSION['flash'] = $this->CI->utils->danger_alert_html('You must be an administrator to view this page');
			redirect('items');
		}
	}

	public function get_auth_token() {
		return $_SESSION['auth_token'];
	}

	public function get_csrf_token() {
		// if(!isset($_SESSION['csrf_token'])) {
		// 	$CI = &get_instance();

		// 	try {
		// 		$action = 'csrf_request';
		// 		$data = array();
		// 		$id = get_class($this);

		// 		$packet = $CI->request->get_packet($action, $data, $id);
		// 		$response = $CI->request->send_request($packet);
		// 		$data = $CI->request->verify_payload($response, 'csrf_response', array('csrf_token'));
		// 		$_SESSION['csrf_token'] = $data['csrf_token'];
		// 	} catch (Exception $e) {
		// 		log_message('error', 'Unable to retrieve CSRF token');
		// 		return null;
		// 	}
		// }
		return $_SESSION['csrf_token'];
	}
}
