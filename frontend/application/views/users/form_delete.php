<?php defined('BASEPATH') OR exit('No direct script access allowed'); ?>
<script type="text/javascript" src="<?php echo base_url(); ?>js/form.js"></script>
<script>
function checkForm() {
    var error = false;
    var inputs = {sms: $('#sms')};
    clearFormValidation(inputs);

    if(inputs.sms.val() == '') {
        error = true;
        setErrorState(inputs.sms);
        addDangerAlert('Please enter the sms code recieved');
    }

    if(!error) {
        $('#form').submit();
    }
}
</script>
<div class="row">
	<div class="col-md-6 col-md-offset-3">
		<h1>User to Delete</h1>
		<table class="table">
			<thead>
				<tr>
					<th class="col-md-4">Field</th>
					<th class="col-md-8">Value</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<th scope="row">ID</th>
					<td><?php echo $id; ?></td>
				</tr>
				<tr>
					<th scope="row">Full Name</th>
					<td><?php echo $full_name; ?></td>
				</tr>
				<tr>
					<th scope="row">Role</th>
					<td><?php echo $role; ?></td>
				</tr>
				<tr>
					<th scope="row">Number</th>
					<td><?php echo $number; ?></td>
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
                <input type="text" class="form-control" name="sms" id="sms" placeholder="SMS Code Recieved">
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary btn-block" onClick="javascript:checkForm();">Confirm Delete User</button>
            </div>
        </form>
    </div>
</div>
