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

<script type="text/javascript" src="<?php echo base_url(); ?>js/sha256.js"></script>
<script type="text/javascript" src="<?php echo base_url(); ?>js/bCrypt.js"></script>
<script>
function showDangerAlert(message) {
	$('#alerts').append('<div class="alert alert-danger alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>Oh Snap!</strong> ' + message + '</div>');
}

function getSalt() {
	var username = $('#username').val() == undefined ? '' : $('#username').val();
	$.getJSON('/index.php/session/get_salt/' + username)
	.done(function(response) {
		if(response.hasOwnProperty('error')) {
			showDangerAlert(response.error);
		} else {
			if(response.hasOwnProperty('salt') && response.hasOwnProperty('challenge')) {
				submitForm(username, response.salt, response.challenge);
			}
		}
	}).fail(function(jxhr, status, error) {
		showDangerAlert('Unable to send request to the server, perhaps it is down?');
	});
}

function submitForm(_username, salt, _challenge) {
	var _csrf_token = "abc";
	var _otp = $('#otp').val();
	var password = $('#password').val() == undefined ? '' : $('#password').val();
	hashpw(password, salt, function(hs, err) {
		hs = btoa(hs);
		console.log('[SECRET] hs = base_64(Hs(password, salt)) = ' + hs);
		var hash = Sha256.hash(hs);
		console.log('hash = H(Hs(password, salt)) = ' + hash);
		var _response = stringXOR(Sha256.hash(hash + _challenge), hs);
		console.log('response = H(H(Hs(password, salt)), challenge) xor Hs(password, salt) = ' + _response);

		$.post('/index.php/session/verify/', {username: _username, challenge: _challenge, response: _response, csrf_token: _csrf_token, otp: _otp})
		.done(function(response) {
			if(response.hasOwnProperty('error')) {
				showDangerAlert(response.error);
			}
		})
	});
}

function stringXOR(s1, s2) {
	s1 = atob(s1);
	s2 = atob(s2);
	result = '';
	for(var i = 0; i < s2.length; i++) {
		result += String.fromCharCode(s1.charCodeAt(i) ^ s2.charCodeAt(i));
	}
	return btoa(result);
}
</script>

<form class="form-signin">
	<h2 class="form-signin-heading">Please sign in</h2>
	<label for="username" class="sr-only">Username</label>
	<input type="text" id="username" name="username" class="form-control" placeholder="Username" required autofocus>
	<label for="password" class="sr-only">Password</label>
	<input type="password" id="password" name="password" class="form-control" placeholder="Password" required>
	<label for="otp" class="sr-only">OTP</label>
	<input type="text" id="otp" name="otp" class="form-control" placeholder="OTP" required>
	<button class="btn btn-lg btn-primary btn-block" onClick="javascript:getSalt();" type="button">Sign in</button>
</form>
