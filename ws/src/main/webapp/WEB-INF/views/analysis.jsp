<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Perform Analysis</title>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/style.css">

<script src="https://code.jquery.com/jquery-2.1.3.min.js"></script>

<script type="text/javascript">
$(function() {
	var tableActions = function() {
		if ( $(this).hasClass("dependent") ) {
			$(this).removeClass("dependent").addClass("predictor");
		} else if ( $(this).hasClass("predictor") ) {
			$(this).removeClass("predictor");
		} else {
			$(this).addClass("dependent");
		}
		
		$(".table").each(function() {
			$(this).removeClass("locked");
			if ( $(this).has(".dependent").length || $(this).has(".predictor").length ) {
				$(this).addClass("locked");
			}
		});
	};
	
	$(".column-names li").click(tableActions);
	
	$("#add-remote-server").click(function() {
		var host = $("[name='host']").val();
		var port = $("[name='port']").val()
		$.ajax({
			url : "http://" + host + ":" + port + "/" + $("body").data("page-context") + "/tables",
			data : 'username=' + $("[name='username']").val() + "&password=" + $("[name='password']").val(),
			success : function(response) {
				
				for ( var i = 0; i < response.length; i++ ) {
					var table = response[i];
					var li = "<li class='table remote'>"
						+ "<p>From " + host + "</p>"
						+ "<div>"
						+ "<span class='table-name'>" + table.tableName + "</span>"
						+ "<ul class='column-names'>";
					for ( var j = 0; j < table.columns.length; j++ ) {
						li += "<li data-column-id='" + j + "' data-table-='" + table.id + "' data-host='" + host + "' data-port='" + port + "' data-token='tbi'>" + table.columns[j].name + "</li>\n";
					}
					li += "</ul>" + "</div>" + "</li>";
					$(".tables").append(li);
				}
				
				$(".remote .column-names li").click(tableActions);
			}
		});
	});
	
	$("#begin-regression").click(beginRegression);
	
	var beginRegression = function(auths) {
		// show wait image during ajax call
		var data = {};
		data.auths = auths || {};
		
		var predictorColumns = [];
		$(".predictor").each(function(e) {
			var column = { table : e.data("table-id"), column : e.data("column-id") }
			if ( e.data("host") ) {
				column.host = e.data("host");
				column.port = e.data("port");
			}
			predictorColumns.push(column);
		});
		var dependentColumns = [];
		$(".dependent").each(function(e) {
			var column = { table : e.data("table-id"), column : e.data("column-id") }
			if ( e.data("host") ) {
				column.host = e.data("host");
				column.port = e.data("port");
			}
			dependentColumns.push(column);
		});
		data.predictorColumns = predictorColumns;
		data.dependentColumns = dependentColumns;
		
		// ask for u/p for each table, submit with regression request
		
		$.ajax({
			url: $("body").data("page-context") + "/regression",
			method : "POST",
			type : "application/json",
			data : data,
			success : function(response) {
				location.href = $("body").data("page-context") + "/regression/" + response.id;
			},
			error : function(error, status, response) {
				// turn off wait image
				if ( status == 401 ) {
					var content = "<h1>Further authentication required</h1>";
					content += "<p>In order to protect the data you are about to analyze, please enter the appropriate table-specific username and password for the following tables:</p>";
					content += "<ul>"
					for ( var i = 0; i < response.auths.length; i++ ) {
						var table = response.auths[i].table;
						content += "<li><h2>For Access to " + table.tableName + " from " + table.host + ":" + table.port + ":</h2>";
						content += "<dl class='auth' data-table-id='" + table.id + "' data-host='" + table.host + "' data-port='" + table.port + "'><dt>Username:</dt><dd><input class='username'/></dd>";
						content += "<dt>Password:</dt><dd><input type='password' class='password'/></dd></dl></li>";
					}
					content += "</ul>"
					content += "<input id='auths' type='submit' class='btn btn-primary' value='Authenticate'/>";
					$.dialog(content);
					$("#auths").click(function() {
						$(".auth").each(function() { 
							data.auths.push({ id : $(this).data("table-id"), host : $(this).data("host"), port : $(this).data("port")});
						});
						beginRegression(data.auths);
					});
				} else { // show the errors
					var content = "<h1>There was an error when running the regression:</h1>";
					content += "<p>" + response.message + "</p>";
					$.dialog(content);
				}
			}
		});
	};
	
});
</script>
</head>
<body data-page-context="${pageContext.request.contextPath}">
	<jsp:include page="/WEB-INF/header.jsp"/>
	<div class="container">
		<h1>Create a Regression</h1>
		<section>
			<ul class="tables">
				<c:forEach var="table" items="${model}">
				<li data-password-protected="${table.passwordProtected}" class="table">
					<div>
						<span class="table-name">${table.tableName}</span>
						<ul class="column-names">
							<c:forEach varStatus="i" var="column" items="${table.columns}">
								<c:if test="${column.joinable}">
									<li data-column-id="${i.index}" data-table-id="${table.id}">${column.name}</li>
								</c:if>
							</c:forEach>
						</ul>
					</div>
				</li>
				</c:forEach>	
			</ul>
			
			<p>Add tables from a remote server:</p>
			<dl>
				<dt>Host:</dt>
				<dd><input type="text" name="host" placeholder="127.0.0.1"/></dd>
				<dt>Port:</dt>
				<dd><input type="text" name="port" placeholder="8080"/></dd>
				<a href="#" id="add-remote-server" class="btn btn-primary">Add</a>			
			</dl>
			
			<a href="#" id="begin-regression" class="btn btn-primary btn-block">Start Regression</a>
		</section>
	</div>
</body>
</html>