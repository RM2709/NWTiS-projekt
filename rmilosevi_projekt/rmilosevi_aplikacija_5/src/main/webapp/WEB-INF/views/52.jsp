<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="jakarta.servlet.ServletContext"%>
<!DOCTYPE html>
<html>
<head>
<style>
<%@include file="/WEB-INF/css/style.css"%>
</style>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<%
ServletContext kontekst = (ServletContext) request.getAttribute("kontekst");
%>
<title>5.2 - Korisnici</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.2 - Korisnici
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
	<table>
		<tbody>
			<tr>
				<th>Pogledi</th>
			</tr>
			<tr>
				<td><a href="${pageContext.servletContext.contextPath}/mvc/korisnici/registracija"><button>5.2.1 Registracija</button></a></td>
			</tr>
			<tr>
				<td><% if(kontekst.getAttribute("korisnik")==null){
				  %>
				  <a href="${pageContext.servletContext.contextPath}/mvc/korisnici/prijava"><button>5.2.2 Prijava</button></a>
				  <%} else{%>
				  <button>5.2.2 Prijava</button>
				  <%}%>	</td>
			</tr>
			<tr>
				<td><% if(kontekst.getAttribute("korisnik")!=null){
				  %>
				  <a href="${pageContext.servletContext.contextPath}/mvc/korisnici/pregled"><button>5.2.3 Pregled</button></a>
				  <%} else{%>
				  <a href="${pageContext.servletContext.contextPath}/mvc/korisnici/prijava"><button>5.2.3 Pregled</button></a>
				  <%}%>	</td>
			</tr>
		</tbody>
	</table>
</body>
</html>