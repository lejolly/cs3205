<?php defined('BASEPATH') OR exit('No direct script access allowed'); ?>
<script type="text/javascript" src="<?php echo base_url(); ?>js/sha256.js"></script>
<script type="text/javascript" src="<?php echo base_url(); ?>js/bCrypt.js"></script>
<script type="text/javascript" src="<?php echo base_url(); ?>js/form.js"></script>
<script>

function checkForm() {
    var error = false;
    var inputs = {full_name: $('#full_name'), password: $('#password'), password_repeat: $('#password_repeat'), number: $('#number')};
    clearFormValidation(inputs);

    if(inputs.full_name.val() == '') {
        error = true;
        setErrorState(inputs.full_name);
        addDangerAlert('Please provide the user\'s Full Name');
    }

    if($('#password').val().length < 10) {
        error = true;
        setErrorState(inputs.password);
        addDangerAlert('Password must be at least 10 characters long');
    }

    if($('#password').val() !== $('#password_repeat').val()) {
        error = true;
        setErrorState(inputs.password);
        setErrorState(inputs.password_repeat);
        addDangerAlert('Passwords do not match');
    }

    var number = parseInt($('#number').val());

    if(isNaN(number) || number < 80000000 || number > 99999999) {
        error = true;
        setErrorState(inputs.number)
        addDangerAlert('Please enter a valid phone number');
    }

    if(!error) {
        $('#salt').val('$2a$10$NE00BxQCjuL3KThJ92HxQe');
        hashpw($('#password').val(), $('#salt').val(), function(hs, err) {
            $('#hash').val(Sha256.hash(btoa(hs)));
            $('#password').val('');
            $('#password_repeat').val('');
            $('#form').submit();
        });
    }
}
</script>
<div class="row">
    <div class="col-md-6 col-md-offset-3">
        <form id="form" method="post">
            <div class="form-group">
                <label for="full_name">Full Name</label>
                <input type="text" class="form-control" name="full_name" id="full_name" placeholder="Full Name of User">
            </div>
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" class="form-control" name="username" id="username" placeholder="Username">
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" class="form-control" id="password" placeholder="Password">
            </div>
            <div class="form-group">
           	    <label for="password_repeat">Repeat Password</label>
                <input type="password" class="form-control" id="password_repeat" placeholder="Repeat Password">
            </div>
            <div class="form-group">
                <label for="number">Phone Number</label>
                <input type="phone" class="form-control" name="number" id="number" placeholder="Phone Number">
            </div>
            <div class="form-group">
                <label>User Role</label>
	            <div class="radio">
	                <label>
	                    <input type="radio" name="role" id="role_user_radio" value="user" checked>
	                    Normal User
	                </label>
	            </div>
	            <div class="radio">
	                <label>
	                    <input type="radio" name="role" id="role_user_radio" value="admin">
	                    Admin User
	                </label>
	            </div>
	        </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary btn-block" onClick="javascript:checkForm();">Add User</button>
                <input type="hidden" name="hash" id="hash" value="">
                <input type="hidden" name="salt" id="salt" value="">
            </div>
        </form>
    </div>
</div>
