<div class="row">
    <table class="table">
        <thead>
            <tr>
                <th class="col-md-1">ID</th>
                <th class="col-md-3">Username</th>
                <th class="col-md-4">Full Name</th>
                <th class="col-md-4">OTP Seed</th>
                <th class="col-md-2">Role</th>
                <th class="col-md-2">Phone Number</th>
                <th class="col-md-2">Actions</th>
            </tr>
        </thead>
        <tbody>
        <?php foreach($rows as $row): ?>
            <tr>
                <td><?php echo isset($row['id']) ? $row['id'] : '<unknown>'; ?></td>
                <td><?php echo isset($row['username']) ? $row['username'] : '<unknown>'; ?></td>
                <td><?php echo isset($row['full_name']) ? $row['full_name'] : '<unknown>'; ?></td>
                <td><?php echo isset($row['otp_seed']) ? $row['otp_seed'] : '<unknown>'; ?></td>
                <td><?php echo isset($row['role']) ? $row['role'] : '<unknown>'; ?></td>
                <td><?php echo isset($row['number']) ? $row['number'] : '<unknown>'; ?></td>
                <td><?php echo anchor('', 'Edit'); ?> | <?php echo anchor('', 'Remove') ?></td>
            </tr>
        <?php endforeach; ?>
        </tbody>
    </table>
</div>

<div class="row">
    <div class="col-md-12">
        <button class="btn btn-primary center-block">Add User</button>
    </div>
</div>
