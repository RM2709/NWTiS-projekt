<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="jakarta.servlet.ServletContext"%>
<!DOCTYPE html>
<html>
<head>
<style>
<%@
include file="/WEB-INF/css/style.css"%>
</style>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<%
ServletContext kontekst = (ServletContext) request.getAttribute("kontekst");
%>
<title>5.2.2 - Prijava</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.2.2 - Prijava
		<% if(kontekst.getAttribute("korisnik")!=null){%>
		<a href="${pageContext.servletContext.contextPath}/mvc/korisnici/odjava">
		  <i class="fa fa-sign-out" id="odjava"></i> 
		</a>
		 <%}%>
	</h1>
	<hr>
	<br>
</header>
<body>
	<form method="POST"
		action="${pageContext.servletContext.contextPath}/mvc/korisnici/prijavi"
		enctype="multipart/form-data">
		<label for="korime">Korisničko ime: </label> <input required type="text" name="korime"><br>
		<label for="lozinka">Lozinka: </label> <input required type="password" name="lozinka"><br>
		<input type="submit">
	</form>
	<br>
	<br>
</body>
</html>