<?php defined('BASEPATH') OR exit('No direct script access allowed'); ?>
<script type="text/javascript">
function delete_item(id) {
    if(confirm('Are you sure?')) {
        window.location = '/index.php/items/delete/' + id;
    }
    return false;
}
</script>
<div class="row">
    <table class="table">
        <thead>
            <tr>
                <th class="col-md-1">ID</th>
                <th class="col-md-2">Name</th>
                <th class="col-md-2">Quantity</th>
                <th class="col-md-5">Comment</th>
                <th class="col-md-2">Actions</th>
            </tr>
        </thead>
        <tbody>
        <?php foreach($rows as $row): ?>
            <tr>
                <td><?php echo $row['id']; ?></td>
                <td><?php echo $row['name']; ?></td>
                <td><?php echo $row['quantity']; ?></td>
                <td><pre><code><?php echo empty($row['comment']) ? '&lt;none&gt;' : $row['comment']; ?></code></pre></td>
                <td><?php echo anchor('items/edit/'.$row['id'], 'Edit'); ?> <?php if($this->auth->is_admin()): ?>| <?php echo anchor('#', 'Delete', array('onClick' => 'javascript:delete_item(' . $row['id'] . ')')); endif; ?></td>
            </tr>
        <?php endforeach; ?>
        </tbody>
    </table>
</div>

<div class="row">
    <div class="col-md-12">
        <center>
            <?php echo anchor('items/add', 'Add Item', 'class="btn btn-primary" role="button"'); ?>
        </center>
    </div>
</div>
