<?php defined('BASEPATH') OR exit('No direct script access allowed'); ?>
<script type="text/javascript" src="<?php echo base_url(); ?>js/form.js"></script>
<script>
function checkForm() {
    var error = false;
    var inputs = {name: $('#name'), quantity: $('#quantity')};
    clearFormValidation(inputs);

    if(inputs.name.val() == '') {
        error = true;
        setErrorState(inputs.name);
        addDangerAlert('Please enter the item name');
    }

    var quantity = parseInt($('#quantity').val());

    if(isNaN(quantity) || quantity < 0) {
        error = true;
        setErrorState(inputs.quantity);
        addDangerAlert('Please provide a valid non-negative quantity');
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
                <label for="name">Name</label>
                <input type="text" class="form-control" name="name" id="name" placeholder="Name of Item" value="<?php echo $name; ?>">
            </div>
            <div class="form-group">
                <label for="quantity">Quantity</label>
                <input type="number" class="form-control" name="quantity" id="quantity" placeholder="Quantity of Item" value="<?php echo $quantity; ?>">
            </div>
            <div class="form-group">
                <label for="comment">Comment</label>
                <textarea class="form-control" rows="4" placeholder="Comments for Item" name="comment"><?php echo $comment; ?></textarea>
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary btn-block" onClick="javascript:checkForm();">Update Item</button>
            </div>
        </form>
    </div>
</div>
