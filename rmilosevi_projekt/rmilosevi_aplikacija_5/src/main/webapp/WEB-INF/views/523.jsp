<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="jakarta.servlet.ServletContext"%>
<%@page import="org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsKorisnici.endpoint.Korisnik"%>
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
List<Korisnik> korisnici = (List<Korisnik>) request.getAttribute("korisnici");
String traziImeKorisnika = (String) request.getAttribute("traziImeKorisnika");
String traziPrezimeKorisnika = (String) request.getAttribute("traziPrezimeKorisnika");
%>
<title>5.2.3 - Pregled</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.2.3 - Pregled
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
		action="${pageContext.servletContext.contextPath}/mvc/korisnici/pregled"
		enctype="multipart/form-data">
		<label for="traziImeKorisnika">Ime: </label> <input type="text" name="traziImeKorisnika" value=<%= (traziImeKorisnika==null) ? "" : traziImeKorisnika%>><br>
		<label for="traziPrezimeKorisnika">Prezime: </label> <input type="text" name="traziPrezimeKorisnika" value=<%= (traziPrezimeKorisnika==null) ? "" : traziPrezimeKorisnika%>><br>
		<input type="submit">
	</form>
	<br>
	<table>
		<tbody>
			<tr>
				<th>Ime</th>
				<th>Prezime</th>
				<th>Korisničko ime</th>
				<th>Lozinka</th>
			</tr>
			<% for(Korisnik korisnik : korisnici){
			  %>
			  <tr>
					<td><%=korisnik.getIme() %></td>
					<td><%=korisnik.getPrezime() %></td>
					<td><%=korisnik.getKorime() %></td>
					<td><%=korisnik.getLozinka() %></td>
				</tr>
			  <%}%>
		</tbody>
	</table>
</body>
</html>