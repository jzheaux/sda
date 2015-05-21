<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Perform Analysis</title>

<script src="${pageContext.request.contextPath}/resources/jquery-1.10.2.js"></script>
<script src="${pageContext.request.contextPath}/resources/jquery-ui.js"></script>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/jquery-ui.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/style.css">

<script src="${pageContext.request.contextPath}/resources/base64.js"></script>

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
		
		$("#begin-regression").unbind("click");
		if ( $(".dependent").length && $(".predictor").length ) {
			$("#begin-regression").click(beginRegression);
		}
	};
	
	$(".column-names li").click(tableActions);
	
	$("#add-remote-server").click(function() {
		var host = $("[name='host']").val();
		var port = $("[name='port']").val();
		
		$.ajax({
			url : $("body").data("page-context") + "/proxy/" + host + "/" + port + "/tables",
			beforeSend: function (xhr) {
				var encoded = Base64.encode($("[name='username']").val() + ":" + $("[name='password']").val());
			    xhr.setRequestHeader ("Authorization", "Basic " + encoded);
			},
			success : function(response) {
				var tables = response;
				for ( var i = 0; i < tables.length; i++ ) {
					var table = tables[i];
					var li = "<li data-table-id='" + table.id + "' data-table-name='" + table.tableName + "' data-host='" + host + "' data-port='" + port + "' class='table remote'>"
						+ "<p>From " + host + "</p>"
						+ "<div>"
						+ "<span class='table-name'>" + table.tableName + "</span>"
						+ "<ul class='column-names'>";
					for ( var j = 0; j < table.columns.length; j++ ) {
						if ( table.columns[j].joinable ) {
							li += "<li data-column-id='" + j + "' data-token='tbi'>" + table.columns[j].name + "</li>\n";
						}
					}
					li += "</ul>" + "</div>" + "</li>";
					$(".tables").append(li);
				}
				
				$(".remote .column-names li").click(tableActions);
			}
		});
	});
	
	var beginRegression = function(e) {
		authorizeRegression();
		e.preventDefault();
	}
	
	var authorizeRegression = function(auths) {
		// show wait image during ajax call
		var data = {};
		data.tables = [];
		
		var hasAtLeastOnePredictor = false;
		var hasAtLeastOneDependent = false;
		
		$(".table").each(function() {
			var tableId = $(this).data("table-id");
			var table = { id : tableId, tableName : $(this).data("table-name") };
			table.predictors = [];
			table.dependents = [];
			
			if ( $(this).data("host") ) {
				table.host = $(this).data("host");
				table.port = $(this).data("port");
			}
			
			$(this).find(".predictor").each(function() {
				var column = { id : $(this).data("column-id") };
				table.predictors.push(column);
				hasAtLeastOnePredictor = true;
			});
			
			$(this).find(".dependent").each(function() {
				var column = { id : $(this).data("column-id") };
				table.dependents.push(column);
				hasAtLeastOneDependent = true;
			});
			 
			if ( auths && auths[tableId] ) {
				table.username = auths[tableId].username;
				table.password = auths[tableId].password;
			}
			
			if ( table.predictors.length > 0 || table.dependents.length > 0 ) {
				data.tables.push(table);
			}
		})
		
		if ( hasAtLeastOnePredictor && hasAtLeastOneDependent ) {
			// ask for u/p for each table, submit with regression request
			$.ajax({
				url: $("body").data("page-context") + "/regression",
				method : "POST",
				datatype : 'json',
				contentType : 'application/json',
				mimeType : 'application/json',
				data : JSON.stringify(data),
				success : function(response) {
					if ( response.id ) {
						//location.href = $("body").data("page-context") + "/regression/" + response.id;
						var content = "<div id='dialog-result' title='Regression Results'>";
						content += response.log;
						content += "</div>";
						$(content).dialog({
							modal: true,
							buttons : {
								"Ok" : function() {
									$(this).dialog("close");
								}
							}
						});
					} else {
						// turn off wait image
						var content = "<div id='dialog-confirm' title='Further Authentication Required'>";
						content += "<p><span class='ui-icon ui-icon-alert' style='float:left; margin:0 7px 20px 0;''></span>In order to protect the data you are about to analyze, please enter the appropriate table-specific username and password for the following tables:</p>";
						content += "<ul>"
						for ( var i = 0; i < response.authRequests.length; i++ ) {
							var table = response.authRequests[i];
							content += "<li><h6>For Access to <strong>" + table.tableName + "</strong>" + ( table.host ? (" from " + table.host + ":" + table.port) : "" ) + ":</h6>";
							content += "<dl class='auth' data-table-id='" + table.id + "' data-host='" + table.host + "' data-port='" + table.port + "'><dt>Username:</dt><dd><input class='username'/></dd>";
							content += "<dt>Password:</dt><dd><input type='password' class='password'/></dd></dl></li>";
						}
						content += "</ul>";
						$(content).dialog({resizable: false,
							      modal: true,
							      buttons : {
							    	  "Authenticate" : function() {
							    		var auths = {};
							    		$(".auth").each(function() { 
											auths[$(this).data("table-id")] = { username : $(this).find(".username").val(), password : $(this).find(".password").val()};
										});
										$(this).dialog("close");
										authorizeRegression(auths);
							    	  },
							    	  "Cancel" : function() {
										$(this).dialog( "close" );
							    	  }
							      }
						});
	 				}
				}
			});
		} else {
			$("<div title='Insufficient Parameters'>You must select at least one predictor column and one dependent column to proceed.</div>").dialog({ modal : true, buttons : { "Ok" : function() { $(this).dialog("close"); }}});
		}

		
	};
	
});
</script>
</head>
<body data-page-context="${pageContext.request.contextPath}">
	<jsp:include page="/WEB-INF/header.jsp"/>
	<div class="container">
		<h1>Create a Regression</h1>
		<section>
			<p>Hover over each table to see the keyable columns; click the column once to mark it as a dependent variable for your regression, click twice to mark it as a predictor.</p>
			<ul class="tables">
				<c:forEach var="table" items="${model}">
				<li data-password-protected="${table.passwordProtected}" data-table-id="${table.id}" data-table-name="${table.tableName}" class="table">
					<div>
						<span class="table-name">${table.tableName}</span>
						<ul class="column-names">
							<c:forEach varStatus="i" var="column" items="${table.columns}">
								<c:if test="${column.joinable}">
									<li data-column-id="${i.index}">${column.name}</li>
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
				<dt>Username:</dt>
				<dd><input type="text" name="username" placeholder="Remote Username"/></dd>
				<dt>Password:</dt>
				<dd><input type="text" name="password" placeholder="Remote Password"/></dd>
				<a href="#" id="add-remote-server" class="btn btn-primary">Add</a>			
			</dl>
			
			<a href="#" id="begin-regression" class="btn btn-primary btn-block">Start Regression</a>
		</section>
	</div>
</body>
</html>