<?php if($this->auth->is_logged_in()): ?>
<ul class="nav navbar-nav navbar-left">
	<li class="dropdown">
		<?php echo anchor('#', 'Items <span class="caret"></span>', array('class' => 'dropdown-toggle', 'data-toggle' => 'dropdown', 'role' => 'button', 'aria-haspopup' => 'true', 'aria-expanded' => 'false')); ?>
		<ul class="dropdown-menu">
			<li><?php echo anchor('items', 'List Items'); ?></li>
			<li><?php echo anchor('items/add', 'Add Item'); ?></li>
		</ul>
	</li>
</ul>
<?php endif; ?>
<?php if($this->auth->is_admin()): ?>
<ul class="nav navbar-nav navbar-left">
	<li class="dropdown">
		<?php echo anchor('#', 'Users <span class="caret"></span>', array('class' => 'dropdown-toggle', 'data-toggle' => 'dropdown', 'role' => 'button', 'aria-haspopup' => 'true', 'aria-expanded' => 'false')); ?>
		<ul class="dropdown-menu">
			<li><?php echo anchor('admin/users', 'List Users'); ?></li>
			<li><?php echo anchor('admin/users/add', 'Add User'); ?></li>
		</ul>
	</li>
</ul>
<?php endif; ?>
<ul class="nav navbar-nav navbar-right">
	<?php if($this->auth->is_logged_in()): ?>
	<li><?php echo anchor('session/logout', 'Logout'); ?></li>
	<?php else: ?>
	<li><?php echo anchor('session/login', 'Login'); ?></li>
	<?php endif; ?>
</ul>
