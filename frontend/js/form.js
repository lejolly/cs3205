function addDangerAlert(message) {
    $('#alerts').append('<div class="alert alert-danger alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>Validation Error!</strong> ' + message + '</div>');
}

function setErrorState(input) {
	input.parent('div').addClass('has-error');
}

function clearErrorState(input) {
	input.parent('div').removeClass('has-error');
}

function clearFormValidation(inputs) {
	$('#alerts').empty();
	$.each(inputs, function(index, value) {
		clearErrorState(value);
	})
}
