<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="jakarta.servlet.ServletContext"%>
<%@page import="org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.Aerodrom"%>
<%@page import="org.foi.nwtis.podaci.Udaljenost"%>
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
List<Udaljenost> udaljenosti = (List<Udaljenost>) request.getAttribute("udaljenosti");
%>
<title>5.5.4 - Udaljenosti dva aerodroma po državama</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.5.4 - Udaljenosti dva aerodroma po državama
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
		action="${pageContext.servletContext.contextPath}/mvc/aerodromi/udaljenostiDrzaveAerodromi"
		enctype="multipart/form-data">
		<label for="icaoOd">Od aerodroma:</label> <input required type="text"
			name="icaoOd"></br> <label for="icaoDo">Do aerodroma:</label> <input
			required type="text" name="icaoDo"></br> <input type="submit">
	</form>
	<br>
	<br>
	<%if(udaljenosti!=null && !udaljenosti.isEmpty()) {%>
	<table>
		<tbody>
			<tr>
				<th>Država</th>
				<th>Udaljenost (km)</th>
			</tr>
			<% 
			float ukupnaUdaljenost = 0; 
			for(Udaljenost udaljenost : udaljenosti){
			  ukupnaUdaljenost+=udaljenost.km();
			  %>
			  <tr>
					<td><%=udaljenost.drzava()%></td>
					<td><%=udaljenost.km() %></td>
				</tr>
			  <%}%>
		</tbody>
	</table>
	<table><tbody><tr><td><b>Ukupna udaljenost</b></td><td><%=ukupnaUdaljenost %></td></tr></tbody></table>
	<%} if(udaljenosti!=null && udaljenosti.isEmpty()){ %>
	<h2 style="text-align: center;">Greška u dohvaćanju udaljenosti!<h1>
	<%} %>
</body>
</html>