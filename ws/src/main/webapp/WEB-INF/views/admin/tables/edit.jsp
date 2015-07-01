<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>New Table</title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css"/>

<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/style.css"/>

<script src="https://code.jquery.com/jquery-2.1.3.min.js"></script>

<script type="text/javascript">
	/* When column clicked, highlight it. */
	$(function() {
		$("th").click(function() {
			var columnName = $(this).data("column-id")
			var id = $("body").data("table-id");
			$("[data-column-id='" + $(this).data("column-id") + "']").toggleClass("join");

			$.ajax({
				url : $("body").data("page-context") + "/table/" + id + "/joincolumn/" + columnName,
				method : $(this).hasClass("join") ? "PUT" : "DELETE",
				error : function(error) {
					console.log(error);
					$("[data-column-id='" + $(this).data("column-id") + "']").toggleClass("join");
				}
			})
		});
	});
</script>
</head>
<body data-table-id="${model.id}" data-page-context="${pageContext.request.contextPath}">
	<jsp:include page="/WEB-INF/header.jsp"/>
	<div class="container">
		<c:choose>
			<c:when test="${not empty model.id}">
				<h1>Edit ${model.tableName}</h1>
			</c:when>
			<c:otherwise>
				<h1>Create a New Table</h1>
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test="${empty model.id}">
				<form method="post" action="${pageContext.request.contextPath}/table/new" enctype="multipart/form-data">
			</c:when>
			<c:otherwise>
				<form method="post" action="${pageContext.request.contextPath}/table/${model.id}/edit" enctype="multipart/form-data">
			</c:otherwise>
		</c:choose>
			<section>
				<h1>Data</h1>
				<dl>
					<dt>Table Name:</dt>
					<dd><input name="tableName" value="${model.tableName}"/></dd>
					<dt>Data (.csv):</dt>
					<dd><input type="file" name="data"/><br/></dd>
					<c:if test="${not empty model.id}">
						<dt>Joinable columns:</dt>
						<dd>
							<span class="note aside">(indicate by clicking column name)</span>
							<table>
								<thead>
									<tr>
										<c:forEach varStatus="i" var="column" items="${preview.headers}">
										<th data-column-id="${i.index}" data-name="${column.name}" class="<c:if test="${column.joinable}">join</c:if>">${column.name}</th>
										</c:forEach>
									</tr>
								</thead>
								<tbody>
									<tr>
										<c:forEach varStatus="i" var="cell" items="${preview.rowOne}">
										<td data-column-id="${i.index}" class="<c:if test="${cell.column.joinable}">join</c:if>">${cell.data}</td>
										</c:forEach>
									</tr>
									<tr>
										<c:forEach varStatus="i" var="cell" items="${preview.rowTwo}">
										<td data-column-id="${i.index}" class="<c:if test="${cell.column.joinable}">join</c:if>">${cell.data}</td>
										</c:forEach>
									</tr>
									<tr>
										<c:forEach varStatus="i" var="column" items="${preview.headers}">
										<th data-column-id="${i.index}" class="<c:if test="${column.joinable}">join</c:if>">...</th>
										</c:forEach>
									</tr>
								</tbody>
							</table>
							
						</dd>
					</c:if>
				</dl>
			</section>
			<section>
				<h1>Access</h1>
				<dl>
					<dt>Username:</dt>
					<dd><input name="username" value="${model.username}"/></dd>
					<dt>Password:</dt>
					<dd><input type="password" name="password" value="${model.password}"/></dd>
					<dt>Confirm Password:</dt>
					<dd><input type="password" name="confirm" value="${model.password}"/></dd>
				</dl>
			</section>
			
			<c:choose>
				<c:when test="${not empty model.id}">
					<input type="submit" class="btn btn-primary" value="Update"/>
				</c:when>
				<c:otherwise>
					<input type="submit" class="btn btn-primary" value="Create"/>
				</c:otherwise>
			</c:choose>
			
		</form>
	</div>
</body>
</html>