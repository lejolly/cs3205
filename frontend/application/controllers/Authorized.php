<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Authorized extends CI_Controller {
	public function index() {
		$data['title'] = "Super Secret Page";
		$data['content'] = $this->parser->parse('display_block', array('title' => 'Super Secret Page', 'content' => 'All your bases are belong to us'), true);
		$this->parser->parse('layout', $data);
	}
}
