<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="jakarta.servlet.ServletContext"%>
<%@page import="org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.Aerodrom"%>
<%@page import="org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsMeteo.endpoint.MeteoPodaci"%>
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
Aerodrom aerodrom = (Aerodrom) request.getAttribute("aerodrom");
MeteoPodaci meteoPodaci = (MeteoPodaci) request.getAttribute("meteoPodaci");
%>
<title>5.5.2 - Podaci o aerodromu</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.5.2 - Podaci o aerodromu
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
		<tr><th colspan="5">Aerodrom</th></tr>
			<tr>
				<th>ICAO</th>
				<th>Naziv</th>
				<th>Država</th>
				<th>Zemljopisna širina</th>
				<th>Zemljopisna duljina</th>
			</tr>
			<tr>
				<td><%=aerodrom.getIcao() %></td>
				<td><%=aerodrom.getNaziv() %></td>
				<td><%=aerodrom.getDrzava() %></td>
				<td><%=aerodrom.getLokacija().getLatitude() %></td>
				<td><%=aerodrom.getLokacija().getLongitude() %></td>
			</tr>
		</tbody>
	</table>
	<%if(meteoPodaci!=null){ %>
	<table>
		<tbody>
		<tr><th colspan="8">Meteorološki podaci</th></tr>
			<tr>
				<th>Vrijeme</th>
				<th>Vlažnost</th>
				<th>Tlak</th>
				<th>Minimalna temperatura</th>
				<th>Maksimalna temperatura</th>
				<th>Trenutna temperatura</th>
				<th>Izlazak sunca</th>
				<th>Zalazak sunca</th>
			</tr>
			<tr>
				<td><%=meteoPodaci.getCloudsName() %></td>
				<td><%=meteoPodaci.getHumidityValue()%> <%=meteoPodaci.getHumidityUnit()%></td>
				<td><%=meteoPodaci.getPressureValue()%> <%=meteoPodaci.getPressureUnit()%></td>
				<td><%=meteoPodaci.getTemperatureMin()%> <%=meteoPodaci.getTemperatureUnit()%></td>
				<td><%=meteoPodaci.getTemperatureMax()%> <%=meteoPodaci.getTemperatureUnit()%></td>
				<td><%=meteoPodaci.getTemperatureValue()%> <%=meteoPodaci.getTemperatureUnit()%></td>
				<td><%=meteoPodaci.getSunRise()%></td>
				<td><%=meteoPodaci.getSunSet()%></td>
			</tr>
		</tbody>
	</table>
	<%} else { %>
	<h2 style="text-align: center;">Greška u dohvaćanju meteoroloških podataka!<h1>
	<%} %>
</body>
</html>