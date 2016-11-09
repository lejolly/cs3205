<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Authorized extends CI_Controller {
	public function index() {
		$this->auth->check_auth();

		$data['title'] = "Super Secret Page";
		$data['content'] = $this->parser->parse('display_block', array('title' => 'Super Secret Page', 'contents' => 'auth_token: '.$_SESSION['role']), true);
		$this->parser->parse('layout', $data);
	}
}
