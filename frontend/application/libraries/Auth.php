<?php
defined('BASEPATH') OR exit('No direct script access allowed');

@session_start();
class Auth {
	public static function check_auth() {
		if(!isset($_SESSION['auth_token'])) {
			redirect('session/login');
		}
	}
}
