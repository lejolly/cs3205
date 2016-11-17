<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Items extends CI_Controller {
	const TABLE_ID = 'items';

	public function index() {
		$this->auth->check_auth();

		$action = 'retrieve_request';
		$data = array();
		$data['auth_token'] = $this->auth->get_auth_token();
		$data['table_id'] = self::TABLE_ID;
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
			$page['title'] = "Error";
			$page['contents'] = $this->load->view('error_block', null, true);
			$this->load->view('layout', $page);
		}
	}

	public function add() {
		$this->auth->check_auth();

		$name = $this->input->post('name');
		$quantity = $this->input->post('quantity');
		$comment = $this->input->post('comment');
		if($name != null && $quantity != null) {
			log_message('debug', '[PARAMS] name = ' . $name);
			log_message('debug', '[PARAMS] quantity = ' . $quantity);
			log_message('debug', '[PARAMS] comment = ' . $comment);

			$action = 'create_request';
			$data['auth_token'] = $this->auth->get_auth_token();
			$data['table_id'] = self::TABLE_ID;
			$data = array_merge($data, compact('name', 'quantity', 'comment'));
			$id = get_class($this);

			try {
				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'create_response', array());
				$_SESSION['flash'] = $this->utils->success_alert_html('Created new item');
				redirect('items');
			} catch(Exception $e) {
				log_message('error', 'Exception when trying to create item: ' . $e->getMessage());
				$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to create new item');
			}
		}

		$page['title'] = 'Add Item';
		$page['contents'] = $this->load->view('items/form_add', null, true);
		$this->load->view('layout', $page);
	}

	public function edit($item_id) {
		$this->auth->check_auth();

		$name = $this->input->post('name');
		$quantity = $this->input->post('quantity');
		$comment = $this->input->post('comment');
		if($name != null && $quantity != null) {
			log_message('debug', '[PARAMS] name = ' . $name);
			log_message('debug', '[PARAMS] quantity = ' . $quantity);
			log_message('debug', '[PARAMS] comment = ' . $comment);

			$action = 'update_request';
			$data['auth_token'] = $this->auth->get_auth_token();
			$data['table_id'] = self::TABLE_ID;
			$data['id'] = $item_id;
			$data = array_merge($data, compact('name', 'quantity', 'comment'));
			$id = get_class($this);

			try {
				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'update_response', array());
				$_SESSION['flash'] = $this->utils->success_alert_html('Item info updated');
				redirect('items');
			} catch(Exception $e) {
				log_message('error', 'Exception when trying to update item: ' . $e->getMessage());
				$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to update item info');
				$page['title'] = 'Update Item';
				$page['contents'] = $this->load->view('items/form_edit', compact('name', 'quantity', 'comment'), true);
				$this->load->view('layout', $page);
			}
		} else {
			$action = 'retrieve_request';
			$data['auth_token'] = $this->auth->get_auth_token();
			$data['table_id'] = self::TABLE_ID;
			$data['record_id'] = $item_id;
			$id = get_class($this);

			try {
				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'retrieve_response', array(), array('rows'));
				$page['title'] = 'Update Item';
				$page['contents'] = $this->load->view('items/form_edit', $payload['rows'][0], true);
				$this->load->view('layout', $page);
			} catch(Exception $e) {
				log_message('error', 'Exception when retrieving item');
				$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to retrieve item details');
				redirect('items');
			}
		}
	}

	public function delete($item_id) {
		$this->auth->check_admin();
		
		$name = $this->input->post('name');
		$confirm = $this->input->post('confirm');

		if($name != null && $confirm != null && strcmp($confirm, 'true') == 0) {
			try {
				$action = 'delete_request';
				$data['auth_token'] = $this->auth->get_auth_token();
				$data['table_id'] = self::TABLE_ID;
				$data['name'] = $name;
				$id = get_class($this);

				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'delete_response', array());
				$_SESSION['flash'] = $this->utils->success_alert_html('Item deleted');
				redirect('items');
			} catch (Exception $e) {
				log_message('error', 'Exception when deleting item: ' . $e->getMessage);
				$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to delete item');
			}
		} else {
			try {
				$action = 'retrieve_request';
				$data['auth_token'] = $this->auth->get_auth_token();
				$data['table_id'] = self::TABLE_ID;
				$data['record_id'] = $item_id;
				$id = get_class($this);

				$packet = $this->request->get_packet($action, $data, $id);
				$response = $this->request->send_request($packet);
				$payload = $this->request->verify_payload($response, 'retrieve_response', array(), array('rows'));

				$page['title'] = 'Confirm Delete Item';
				$page['contents'] = $this->load->view('items/form_delete', $payload['rows'][0], true);
				$this->load->view('layout', $page);
			} catch(Exception $e) {
				log_message('error', 'Exception when retrieving item');
				$_SESSION['flash'] = $this->utils->danger_alert_html('Unable to retrieve item details');
				redirect('items');
			}
		}
	}
}
