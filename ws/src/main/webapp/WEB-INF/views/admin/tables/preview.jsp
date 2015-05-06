<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table>
	<thead>
		<tr>
			<c:forEach var="column" items="${model.columns}">
			<th>${column}</th>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<tr>
			<c:forEach var="column" items="${model.rowOne}">
			<td>${column}</td>
			</c:forEach>
		</tr>
		<tr>
			<c:forEach var="column" items="${model.rowTwo}">
			<td>${column}</td>
			</c:forEach>
		</tr>
		<tr>
			<c:forEach var="column" items="${model.columns}">
			<td>...</td>
			</c:forEach>
		</tr>
	</tbody>
</table>