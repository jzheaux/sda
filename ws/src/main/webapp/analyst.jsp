<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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

<link rel="stylesheet" href="style.css">

<script src="https://code.jquery.com/jquery-2.1.3.min.js"></script>

<script type="text/javascript">
$(function() {
	$(".column-names li").click(function() {
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
	});
});
</script>
</head>
<body>
	<div class="container">
		<h1>Create a Regression</h1>
		<section>
			<ul class="tables">
				<li class="table">
					<div>
						<span class="table-name">Table #1</span>
						<ul class="column-names">
							<li>Column A</li>
							<li>Column B</li>
							<li>Column C</li>
							<li>Column D</li>
							<li>Column E</li>
							<li>Column F</li>
							<li>Column G</li>
							<li>Column H</li>
						</ul>
					</div>
				</li>
				<li class="table">
					<div>
						<span class="table-name">Table #2</span>
						<ul class="column-names">
							<li>Column A</li>
							<li>Column B</li>
							<li>Column C</li>
							<li>Column D</li>
							<li>Column E</li>
							<li>Column F</li>
						</ul>
					</div>
				</li>
				<li class="table">
					<div>
						<span class="table-name">Table #3</span>
						<ul class="column-names">
							<li>Column A</li>
							<li>Column B</li>
							<li>Column C</li>
							<li>Column D</li>
							<li>Column E</li>
							<li>Column F</li>
							<li>Column G</li>
							<li>Column H</li>
						</ul>
					</div>
				</li>
				<li class="table">
					<div>
						<span class="table-name">Table #4</span>
						<ul class="column-names">
							<li>Column A</li>
							<li>Column B</li>
							<li>Column C</li>
							<li>Column D</li>
							<li>Column E</li>
							<li>Column F</li>
							<li>Column G</li>
							<li>Column H</li>
							<li>Column I</li>
						</ul>
					</div>
				</li>	
				<li class="table remote">
					<p>From 165.76.52.11</p>
					<div>
						<span class="table-name">Table #1</span>
						<ul class="column-names">
							<li>Column A</li>
							<li>Column B</li>
							<li>Column C</li>
							<li>Column D</li>
							<li>Column E</li>
						</ul>
					</div>
					
				</li>
				<li class="table remote">
					<p>From 165.76.52.11</p>
					<div>
						<span class="table-name">Table #2</span>
						<ul class="column-names">
							<li>Column A</li>
							<li>Column B</li>
							<li>Column C</li>
							<li>Column D</li>
							<li>Column E</li>
							<li>Column F</li>
							<li>Column G</li>
						</ul>
					</div>
				</li>
			</ul>
			
			<p>Add tables from a remote server:</p>
			<dl>
				<dt>Host:</dt>
				<dd><input type="text" placeholder="127.0.0.1"/></dd>
				<dt>Port:</dt>
				<dd><input type="text" placeholder="8080"/></dd>
				<a href="#" id="add-remote-server" class="btn btn-primary">Add</a>			
			</dl>
			
			<a href="#" id="begin-regression" class="btn btn-primary btn-block">Start Regression</a>
		</section>
	</div>
</body>
</html>