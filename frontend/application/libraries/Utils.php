<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Utils {
	public function danger_alert_html($message, $title = 'Oh Snap!') {
		return '<div class="alert alert-danger alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>'.$title.'</strong> '.$message.'</div>';
	}

	public function success_alert_html($message, $title = 'Success!') {
		return '<div class="alert alert-success alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>'.$title.'</strong> '.$message.'</div>';
	}
}
