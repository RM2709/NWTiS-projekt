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
<title>5.3 - Poslužitelj</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.3 - Poslužitelj
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
	<%
	if(request.getAttribute("poruka")!=null){
	  %>
	  	<div class="alert"">
		<span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span>
		<%= (String) request.getAttribute("poruka") %>
		</div>
		<br>
	  <%
	}
	%>
	<table>
		<tbody>
			<tr>
				<th>Upravljanje poslužiteljem</th>
			</tr>
			<tr>
				<td><a href="${pageContext.servletContext.contextPath}/mvc/posluzitelj/status"><button>STATUS</button></a></td>
			</tr>
			<tr>
				<td><a href="${pageContext.servletContext.contextPath}/mvc/posluzitelj/kraj"><button>KRAJ</button></a></td>
			</tr>
			<tr>
				<td><a href="${pageContext.servletContext.contextPath}/mvc/posluzitelj/init"><button>INIT</button></a></td>
			</tr>
			<tr>
				<td><a href="${pageContext.servletContext.contextPath}/mvc/posluzitelj/pauza"><button>PAUZA</button></a></td>
			</tr>
			<tr>
				<td><a href="${pageContext.servletContext.contextPath}/mvc/posluzitelj/infoDa"><button>INFO DA</button></a></td>
			</tr>
			<tr>
				<td><a href="${pageContext.servletContext.contextPath}/mvc/posluzitelj/infoNe"><button>INFO NE</button></a></td>
			</tr>
		</tbody>
	</table>
</body>
</html>