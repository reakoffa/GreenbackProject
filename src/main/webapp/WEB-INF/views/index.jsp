<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<h1>Welcome to my HTML converter app</h1>
	<br>
	<br>
	<h3>Please select an HTML file to upload</h3>
	${error }
	<form method="POST" enctype="multipart/form-data" action="/">
		<table>
			<tr>
				<td>Select file to upload:<input type="file" name="fileupload"
					required /></td>
			</tr>
			<br>
			<tr>

				<td>Convert to JSON file:<input type="submit" value="Convert" /></td>
			</tr>
		</table>
	</form>
	Download coverted file to local computer:
	<a href="${file }">${file }</a>

</body>
</html>