<?php
defined('BASEPATH') OR exit('No direct script access allowed');
session_start();

class Authorized extends CI_Controller {
	public function index() {
		if(!isset($_SESSION['auth_token'])) {
			redirect('session/login');
		}
		$data['title'] = "Super Secret Page";
		$data['content'] = $this->parser->parse('display_block', array('title' => 'Super Secret Page', 'content' => 'auth_token: '.$_SESSION['auth_token'].'<br>id: '.$_SESSION['data']['id'].'<br>first_name: '.$_SESSION['data']['first_name'].'<br>last_name: '.$_SESSION['data']['last_name'].'<br>last_update: '.$_SESSION['data']['last_update']), true);
		$this->parser->parse('layout', $data);
	}
}
