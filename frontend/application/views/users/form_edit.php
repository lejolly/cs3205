<?php defined('BASEPATH') OR exit('No direct script access allowed'); ?>
<script type="text/javascript" src="<?php echo base_url(); ?>js/sha256.js"></script>
<script type="text/javascript" src="<?php echo base_url(); ?>js/bCrypt.js"></script>
<script type="text/javascript" src="<?php echo base_url(); ?>js/form.js"></script>
<script>

function checkForm() {
    var error = false;
    var inputs = {full_name: $('#full_name'), number: $('#number')};
    clearFormValidation(inputs);

    if(inputs.full_name.val() == '') {
        error = true;
        setErrorState(inputs.full_name);
        addDangerAlert('Please provide the user\'s Full Name');
    }

    var number = parseInt($('#number').val());

    if(isNaN(number) || number < 80000000 || number > 90000000) {
        error = true;
        setErrorState(inputs.number)
        addDangerAlert('Please enter a valid phone number');
    }

    if(!error) {
        $('#form').submit();
    }
}
</script>
<div class="row">
    <div class="col-md-6 col-md-offset-3">
        <form id="form" method="post">
            <div class="form-group">
                <label for="full_name">Full Name</label>
                <input type="text" class="form-control" name="full_name" id="full_name" placeholder="Full Name of User" value="<?php echo $full_name; ?>">
            </div>
            <div class="form-group">
                <label for="number">Phone Number</label>
                <input type="phone" class="form-control" name="number" id="number" placeholder="Phone Number" value="<?php echo $number; ?>">
            </div>
            <div class="form-group">
                <label>User Role</label>
	            <div class="radio">
	                <label>
	                    <input type="radio" name="role" id="role_user_radio" value="user" <?php if(strcmp($role, 'user') == 0) {echo 'checked';} ?>>
	                    Normal User
	                </label>
	            </div>
	            <div class="radio">
	                <label>
	                    <input type="radio" name="role" id="role_user_radio" value="admin" <?php if(strcmp($role, 'admin') == 0) {echo 'checked';} ?>>
	                    Admin User
	                </label>
	            </div>
	        </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary btn-block" onClick="javascript:checkForm();">Update User</button>
                <input type="hidden" name="username" value="<?php echo $username; ?>">
            </div>
        </form>
    </div>
</div>
