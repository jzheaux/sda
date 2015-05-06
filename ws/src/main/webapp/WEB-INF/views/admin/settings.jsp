<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Data Provider</title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">

<!-- Latest compiled and minified JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

<link rel="stylesheet" href="style.css">

<script src="https://code.jquery.com/jquery-2.1.3.min.js"></script>
</head>
<body>
	<div class="container">
		<section id="tables">
		<h1>Data Tables</h1>
		<table class="data">
			<thead>
				<tr>
					<th>Name</th>
					<th>Size</th>
					<th>Last Modified</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="table" items="${tables}">
					<tr>
						<td>${table.tableName}</td>
						<td>${table.sizeInBytes} bytes</td>
						<td>${table.lastModifiedInDays} day(s) ago</td>
						<td><a class="btn btn-primary" href="tables/${table.id}/edit">Edit</a>
							<form method="post" action="tables/${table.id}/delete">
								<input type="submit" class="btn" value="Delete" />
							</form></td>
					</tr>
				</c:forEach>
				<!-- <tr>
							<td>Table #2</td>
							<td>65467 bytes</td>
							<td>3 days ago</td>
							<td><a class="btn btn-primary" href="tables/edit.jsp">Edit</a><a class="btn" href="#">Delete</a></td>
						</tr>
						<tr>
							<td>Table #3</td>
							<td>12342 bytes</td>
							<td>3 days ago</td>
							<td><a class="btn btn-primary" href="tables/edit.jsp">Edit</a><a class="btn" href="#">Delete</a></td>
						</tr>		 -->
			</tbody>
		</table>
		<a class="btn btn-primary" href="table/new">Add Data Table</a> </section>

		<section id="settings">
		<h1>Server Settings</h1>
		<form method="post">
			<dl>
				<dt>Number of parallel processors:</dt>
				<dd>
					<input type="number" name="numberOfProcessors"
						value="${model.numberOfProcessors}" />
				</dd>
				<dt>Number of GPUs:</dt>
				<dd>
					<input type="number" name="numberOfGpus"
						value="${model.numberOfGpus}" />
				</dd>
				<dt>Data Directory</dt>
				<dd>
					<input name="dataDirectory" value="${model.dataDirectory}" />
				</dd>
				<dt>Access Logs Directory</dt>
				<dd>
					<input name="accessLogDirectory"
						value="${model.accessLogDirectory}" />
				</dd>
			</dl>
			<input class="btn btn-primary" type="submit" value="Submit" />
		</form>
		</section>
	</div>
</body>
</html>