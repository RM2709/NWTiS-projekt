<%@page import="org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsLetovi.endpoint.LetAviona"%>
<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="jakarta.servlet.ServletContext"%>
<%@page import="org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsLetovi.endpoint.LetAviona"%>
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
List<LetAviona> letovi = (List<LetAviona>) request.getAttribute("letovi");
String icao = (String) request.getAttribute("icao");
String datum = (String) request.getAttribute("datum");
if(letovi!=null && letovi.isEmpty()) {
  icao = null;
  datum = null;
}
%>
<title>5.6.2 - Letovi s aerodroma na datum (OS)</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.6.2 - Letovi s aerodroma na datum (OS)
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
		action="${pageContext.servletContext.contextPath}/mvc/letovi/polasciDatumOS"
		enctype="multipart/form-data">
		<label for="icao">Aerodrom:</label> 
		<input required type="text" name="icao" value=<%= (icao==null) ? "" : icao%>></br> 
		<label for="datum">Datum:</label> 
		<input required type="text" name="datum" placeholder="dd.mm.gggg." value=<%= (datum==null) ? "" : datum%>></br> 
		<input type="submit">
	</form>
	<br>
	<br>
	<%if(letovi!=null && !letovi.isEmpty()) {%>
	<table>
		<tbody>
			<tr>
				<th>ICAO</th>
				<th>Prvi kontakt</th>
				<th>Aerodrom polaska</th>
				<th>Zadnji kontakt</th>
				<th>Aerodrom dolaska</th>
				<th>Oznaka</th>
				<th>Horizontalna udaljenost aerodroma odlaska</th>
				<th>Vertikalna udaljenost aerodroma odlaska</th>
				<th>Horizontalna udaljenost aerodroma dolaska</th>
				<th>Vertikalna udaljenost aerodroma dolaska</th>
				<th>Broj kandidata aerodroma odlaska</th>
				<th>Broj kandidata aerodroma dolaska</th>
			</tr>
			<%
			for (LetAviona l : letovi) {
			%>
			<tr>
				<td><%=l.getIcao24()%></td>
				<td><%=l.getFirstSeen()%></td>
				<td><%=l.getEstDepartureAirport()%></td>
				<td><%=l.getLastSeen()%></td>
				<td><%=l.getEstArrivalAirport()%></td>
				<td><%=l.getCallsign()%></td>
				<td><%=l.getEstDepartureAirportHorizDistance()%></td>
				<td><%=l.getEstDepartureAirportVertDistance()%></td>
				<td><%=l.getEstArrivalAirportHorizDistance()%></td>
				<td><%=l.getEstArrivalAirportVertDistance()%></td>
				<td><%=l.getDepartureAirportCandidatesCount()%></td>
				<td><%=l.getArrivalAirportCandidatesCount()%></td>
			</tr>
			<%} %>
		</tbody>
	</table>
	<%} if(letovi!=null && letovi.isEmpty()){ %>
	<h2 style="text-align: center;">Greška u dohvaćanju letova!<h1>
	<%} %>
</body>
</html>