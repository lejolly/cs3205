<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Items extends CI_Controller {
	const TABLE_ID = 'items';

	public function index() {
		$this->auth->check_auth();

		$action = 'retrieve_request';
		$data = array();
		$data['auth_token'] = $this->auth->get_auth_token();
		$data['csrf_token'] = $this->auth->get_csrf_token();
		$data['table_id'] = self::TABLE_ID;
		$data['record_id'] = '';
		$id = get_class($this);

		try {
			$packet = $this->request->get_packet($action, $data, $id);
			$response = $this->request->send_request($packet);
			$payload = $this->request->verify_payload($response, 'retrieve_response', array(), array('rows'));
			$page['title'] = "List of Items";
			$page['contents'] = $this->load->view('items/table', $payload, true);
			$this->parser->parse('layout', $page);
		} catch(Exception $e) {
			log_message('error', 'Exception when retrieving items list: '.$e->getMessage());
		}
	}

	public function add() {
		$this->auth->check_auth();

		$name = $this->input->post('name');
		$quantity = $this->input->post('quantity');
		$comment = $this->input->post('comment');
		if($name != null && $quantity != null && $comment != null) {
			log_message('debug', '[PARAMS] name='.$name);
			log_message('debug', '[PARAMS] quantity='.$quantity);
			log_message('debug', '[PARAMS] comment='.$comment);

			$action = 'create_request';
			$data['auth_token'] = $this->auth->get_auth_token();
			$data['csrf_token'] = $this->auth->get_csrf_token();
			$data['table_id'] = self::TABLE_ID;
			$data = array_merge($data, compact('name', 'quantity', 'comment'));
			$id = get_class($this);

			try {
				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'retrieve_response', array());
				$_SESSION['flash'] = $this->utils->success_alert_html('Created new item');
				redirect('items');
			} catch(Exception $e) {
				log_message('error', 'Exception when trying to create item: ' . $e->getMessage());
				$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to create new item');
			}
		}

		$page['title'] = 'Add New Item';
		$page['contents'] = $this->load->view('items/form', null, true);
		$this->load->view('layout', $page);
	}
}
