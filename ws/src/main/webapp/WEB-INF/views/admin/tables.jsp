<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
						<c:forEach var="table" items="${model}">
						<tr>
							<td>${table.name}</td>
							<td>${table.sizeInBytes} bytes</td>
							<td>${table.lastModifiedInDays} day(s) ago</td>
							<td><a class="btn btn-primary" href="tables/${table.id}">Edit</a><form method="post" action="tables/${table.id}/delete"><input type="submit" class="btn" value="Delete"/></form></td>
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
				<a class="btn btn-primary" href="tables/new">Add Data Table</a>
	</section>