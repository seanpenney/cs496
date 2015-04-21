$(document).ready(function() {
	$("#university_form").submit(function(e) {
		var mydata = {
			name: $("#university_name_input").val(),
			url: $("#university_photo_input").val()
		};
		
		$.ajax({
			method: "post",
			url: "/add",
			data: mydata,
			dataType: "json",
			success: addedUniversity,
			error: errFunction
		});
		e.preventDefault();
		return false;
	});
	
	function addedUniversity(data) {
		
		alert("Added " + data.name + ", " + data.photo);
	}
	
	function errFunction(a, b) {
		alert("error:" + a + "/" + b);
	}
});