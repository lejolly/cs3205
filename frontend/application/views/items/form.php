<?php defined('BASEPATH') OR exit('No direct script access allowed'); ?>
<script type="text/javascript" src="<?php echo base_url(); ?>js/form.js"></script>
<script>

function checkForm() {
    var error = false;
    var inputs = {name: $('#name')};
    clearFormValidation(inputs);
}
</script>
<div class="row">
    <div class="col-md-6 col-md-offset-3">
        <form id="form" method="post">
            <div class="form-group">
                <label for="name">Name</label>
                <input type="text" class="form-control" name="name" id="name" placeholder="Name of Item">
            </div>
            <div class="form-group">
                <label for="quantity">Quantity</label>
                <select class="form-control">
                <?php for($i = 1; $i <= 100; $i++): ?>
                	<option value="<?php echo $i; ?>"><?php echo $i; ?></option>
                <?php endfor; ?>
                </select>
            </div>
            <div class="form-group">
                <label for="comment">Comment</label>
                <textarea class="form-control" rows="4" placeholder="Comments for Item"></textarea>
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary btn-block" onClick="javascript:checkForm();">Add New Item</button>
            </div>
        </form>
    </div>
</div>
