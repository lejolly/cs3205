<?php defined('BASEPATH') OR exit('No direct script access allowed'); ?>
<div class="row">
	<div class="col-md-6 col-md-offset-3">
		<h1>Item to Delete</h1>
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
					<th scope="row">Name</th>
					<td><?php echo $name; ?></td>
				</tr>
				<tr>
					<th scope="row">Quantity</th>
					<td><?php echo $quantity; ?></td>
				</tr>
				<tr>
					<th scope="row">Comment</th>
					<td><pre><code><?php echo $comment; ?></code></pre></td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<div class="row">
    <div class="col-md-6 col-md-offset-3">
        <form id="form" method="post">
            <div class="form-group">
                <button type="submit" class="btn btn-primary btn-block" name="confirm" value="true">Confirm Delete Item</button>
                <input type="hidden" name="name" value="<?php echo $name; ?>">
            </div>
        </form>
    </div>
</div>
