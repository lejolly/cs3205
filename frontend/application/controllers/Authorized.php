<?php
defined('BASEPATH') OR exit('No direct script access allowed');
session_start();

class Authorized extends CI_Controller {
	public function index() {
		if(!isset($_SESSION['auth_token'])) {
			redirect('session/login');
		}

		$data['title'] = "Super Secret Page";
		$data['content'] = $this->parser->parse('display_block', array('title' => 'Super Secret Page', 'content' => 'All your bases are belong to us'), true);
		$this->parser->parse('layout', $data);
	}
}
