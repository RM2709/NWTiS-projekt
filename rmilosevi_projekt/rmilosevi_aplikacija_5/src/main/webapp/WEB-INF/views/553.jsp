<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="jakarta.servlet.ServletContext"%>
<%@page import="org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.Aerodrom"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.Map"%>
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
TreeMap<String, Boolean> aerodromi = (TreeMap<String, Boolean>) request.getAttribute("aerodromi");
HashMap<String, Aerodrom> aerodromiString = (HashMap<String, Aerodrom>) request.getAttribute("aerodromiString");
%>
<title>5.5.3 - Aerodromi za preuzimanje letova</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.5.3 - Aerodromi za preuzimanje letova
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
		<tr><th colspan="6">Aerodrom</th></tr>
			<tr>
				<th>ICAO</th>
				<th>Naziv</th>
				<th>Država</th>
				<th>Zemljopisna širina</th>
				<th>Zemljopisna duljina</th>
				<th>Status</th>
			</tr>
			<% for(Map.Entry<String, Boolean> unos : aerodromi.entrySet()){
			  String icao = unos.getKey();
			  Boolean status = unos.getValue();
			  Aerodrom aerodrom = aerodromiString.get(icao);
			  %>
			  <tr>
					<td><%=aerodrom.getIcao()%></td>
					<td><%=aerodrom.getNaziv() %></td>
					<td><%=aerodrom.getDrzava() %></td>
					<td><%=aerodrom.getLokacija().getLatitude() %></td>
					<td><%=aerodrom.getLokacija().getLongitude() %></td>
					<%if(status==true){ %>
					<td><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/pauzirajAerodrom?icao=<%=aerodrom.getIcao()%>"><button>Da</button></a></td>
					<%} else { %>
					<td><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/aktivirajAerodrom?icao=<%=aerodrom.getIcao()%>"><button>Pauza</button></a></td>
					<%}%>
				</tr>
			  <%}%>
		</tbody>
	</table>
</body>
</html>