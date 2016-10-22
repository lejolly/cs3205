<style>
.form-signin {
	max-width: 330px;
	padding: 15px;
	margin: 0 auto;
}
.form-signin .form-signin-heading {
  	margin-bottom: 10px;
}

.form-signin .form-control {
	position: relative;
	height: auto;
	-webkit-box-sizing: border-box;
	-moz-box-sizing: border-box;
	box-sizing: border-box;
	padding: 10px;
	font-size: 16px;
}
.form-signin .form-control:focus {
  	z-index: 2;
}
.form-signin input[id="username"] {
	margin-bottom: -1px;
	border-bottom-right-radius: 0;
	border-bottom-left-radius: 0;
}
.form-signin input[id="password"] {
	margin-top: -1px;
	margin-bottom: -1px;
	border-radius: 0;
}
.form-signin input[id="otp"] {
	margin-bottom: 10px;
	border-top-left-radius: 0;
	border-top-right-radius: 0;
}
</style>

<script>
function submitForm() {
	var username = $('username').val();
	$.ajax({
		url: '/cs3205/index.php/session/get_salt/'
	}).done(function(data) {
		console.log('success');
		console.log(data);
		if(data.hasOwnProperty('error')) {
			$('#alerts').append('<div class="alert alert-danger alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>Oh snap!</strong> ' + data.error + '</div>');
		}
	}).fail(function(jxhr, status, error) {
		console.log('failed:' + error);
		$('#alerts').append('<div class="alert alert-error alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>Oh snap!</strong> Unable to send request to the server, perhaps it is down?</div>');
	});
}
</script>

<form class="form-signin" action="<?php echo site_url('session/verify'); ?>" method="post">
	<h2 class="form-signin-heading">Please sign in</h2>
	<label for="username" class="sr-only">Username</label>
	<input type="text" id="username" name="username" class="form-control" placeholder="Username" required autofocus>
	<label for="password" class="sr-only">Password</label>
	<input type="password" id="password" name="password" class="form-control" placeholder="Password" required>
	<label for="otp" class="sr-only">OTP</label>
	<input type="text" id="otp" name="otp" class="form-control" placeholder="OTP" required>
	<button class="btn btn-lg btn-primary btn-block" onClick="javascript:submitForm();" type="button">Sign in</button>
</form>
