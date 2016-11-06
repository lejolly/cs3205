<?php
defined('BASEPATH') OR exit('No direct script access allowed');

@session_start();
class Auth {
	public function check_auth() {
		if(!isset($_SESSION['auth_token'])) {
			redirect('session/login');
		}
	}

	public function check_admin() {
		$this->check_auth();
		
	}

	public function get_auth_token() {
		return $_SESSION['auth_token'];
	}
}
