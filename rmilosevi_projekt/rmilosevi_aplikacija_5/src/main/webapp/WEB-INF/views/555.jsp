<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="jakarta.servlet.ServletContext"%>
<%@page import="org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.Aerodrom"%>
<!DOCTYPE html>
<html>
<head>
<style>
<%@include file="/WEB-INF/css/style.css"%>
</style>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<%
ServletContext kontekst = (ServletContext) request.getAttribute("kontekst");
String udaljenost = (String) request.getAttribute("udaljenost");
%>
<title>5.5.5 - Udaljenost dva aerodroma</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.5.5 - Udaljenost dva aerodroma
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
	<form method="GET"
		action="${pageContext.servletContext.contextPath}/mvc/aerodromi/udaljenostAerodromi"
		enctype="multipart/form-data">
		<label for="icaoOd">Od aerodroma:</label> <input required type="text"
			name="icaoOd"></br> <label for="icaoDo">Do aerodroma:</label> <input
			required type="text" name="icaoDo"></br> <input type="submit">
	</form>
	<br>
	<br>
	<%if(udaljenost!=null) {
		if(udaljenost!="index"){
	%>
	<table>
		<tbody>
			<tr>
				<th>Udaljenost (km)</th>
			</tr>
			  <tr>
					<td><%=udaljenost%></td>
				</tr>
		</tbody>
	</table>
	<%}
	} else{ %>
	<h2 style="text-align: center;">Greška u dohvaćanju udaljenosti!<h1>
	<%} %>
</body>
</html>