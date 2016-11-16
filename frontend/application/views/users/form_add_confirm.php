<?php defined('BASEPATH') OR exit('No direct script access allowed'); ?>
<script type="text/javascript" src="<?php echo base_url(); ?>js/form.js"></script>
<script>
function checkForm() {
    var error = false;
    var inputs = {challenge: $('#challenge')};
    clearFormValidation(inputs);

    if(inputs.challenge.val() == '') {
        error = true;
        setErrorState(inputs.challenge);
        addDangerAlert('Please enter the sms code recieved');
    }

    if(!error) {
        $('#form').submit();
    }
}
</script>
<div class="row">
    <div class="col-md-6 col-md-offset-3">
        <h1>User to Add</h1>
        <table class="table">
            <thead>
                <tr>
                    <th>Field</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <th scope="row">Full Name</th>
                    <td><?php echo $full_name; ?></td>
                </tr>
                <tr>
                    <th scope="row">Username</th>
                    <td><?php echo $username; ?></td>
                </tr>
                <tr>
                    <th scope="row">Number</th>
                    <td><?php echo $number; ?></td>
                </tr>
                <tr>
                    <th scope="row">Role</th>
                    <td><?php echo $role; ?></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<div class="row">
    <div class="col-md-6 col-md-offset-3">
        <form id="form" method="post">
            <div class="form-group">
                <label for="name">SMS Code</label>
                <input type="text" class="form-control" name="challenge" id="challenge" placeholder="SMS Code Recieved">
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary btn-block" onClick="javascript:checkForm();">Confirm Add User</button>
                <input type="hidden" name="username" value="<?php echo $username; ?>">
            </div>
        </form>
    </div>
</div>
