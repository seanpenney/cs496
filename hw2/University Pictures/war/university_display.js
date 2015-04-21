$(document).ready(function() {
	$.ajax({
		method : "get",
		url : "/query",
		dataType : "json",
		success : displayUniversities,
		error : errFunction
	});

	function displayUniversities(data) {
		var tbody = $("#university_table tbody");
		tbody.empty();
		for (var i = 0; i < data.length; i++) {
			var obj = data[i];
			var tr = $("<tr></tr>");
			tr.append($("<td></td>").text(obj.name));
			tr.append($("<td></td>").html('<img src=' + obj.url + ' height=200px width=200px> '));
			tbody.append(tr);
		}
	}

	function errFunction(a, b) {
		alert("error:" + a + "/" + b);
	}
});