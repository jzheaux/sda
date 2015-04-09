<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>New Table</title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

<link rel="stylesheet" href="../../style.css">

<script src="https://code.jquery.com/jquery-2.1.3.min.js"></script>

<script type="text/javascript">
	/* When column clicked, highlight it. */

</script>
</head>
<body>
	<div class="container">
		<form method="post">
			<section>
				<h1>Data</h1>
				<dl>
					<dt>Name:</dt>
					<dd><input name="name"/></dd>
					<dt>Data:</dt>
					<dd><input type="file" name="data"/></dd>
					<dd>
						<table>
							<thead>
								<tr>
									<th>Column A</th>
									<th>Column B</th>
									<th>Column C</th>
									<th>Column D</th>
									<th>Column E</th>
									<th>Column F</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>0.3423</td>
									<td>0.43534</td>
									<td>0.81231</td>
									<td>0.21312</td>
									<td>0.34353</td>
									<td>0.56466</td>
								</tr>
								<tr>
									<td>0.3423</td>
									<td>0.43534</td>
									<td>0.81231</td>
									<td>0.21312</td>
									<td>0.34353</td>
									<td>0.56466</td>
								</tr>
								<tr>
									<td>...</td>
									<td>...</td>
									<td>...</td>
									<td>...</td>
									<td>...</td>
									<td>...</td>
								</tr>
							</tbody>
						</table>
					</dd>
				</dl>
			</section>
			<section>
				<h1>Access</h1>
				<dl>
					<dt>Username:</dt>
					<dd><input name="username"/></dd>
					<dt>Password:</dt>
					<dd><input type="password" name="password"/></dd>
					<dt>Confirm Password:</dt>
					<dd><input type="password" name="confirm"/></dd>
				</dl>
			</section>
			<input type="submit" class="btn btn-primary" value="Submit"/>
		</form>
	</div>
</body>
</html>