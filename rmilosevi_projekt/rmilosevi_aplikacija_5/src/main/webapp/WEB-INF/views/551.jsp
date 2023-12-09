<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
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
List<Aerodrom> aerodromi = (List<Aerodrom>) request.getAttribute("aerodromi");
String traziNaziv = (String) request.getAttribute("traziNaziv");
String traziDrzavu = (String) request.getAttribute("traziDrzavu");
Integer odBroja = Integer.parseInt(request.getAttribute("odBroja").toString());
Integer broj = Integer.parseInt(request.getAttribute("broj").toString());
%>
<title>5.5.1 - Pregled aerodroma</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.5.1 - Pregled aerodroma
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
		action="${pageContext.servletContext.contextPath}/mvc/aerodromi/pregledAerodroma"
		enctype="multipart/form-data">
		<label for="traziNaziv">Naziv: </label> <input type="text" name="traziNaziv" value=<%= (traziNaziv==null) ? "" : traziNaziv%>><br>
		<label for="traziDrzavu">Država: </label> <input type="text" name="traziDrzavu" value=<%= (traziDrzavu==null) ? "" : traziDrzavu%>><br>
		<input type="hidden" id="odBroja" name="odBroja" value="0">
		<input type="hidden" id="broj" name="broj" value="<%=broj%>">
		<input type="submit">
	</form>
	<br>
	<table>
	<tr>
	<%if(odBroja>0) {
	%><td><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/pregledAerodroma?traziNaziv=<%= (traziNaziv==null) ? "" : traziNaziv%>&traziDrzavu=<%= (traziDrzavu==null) ? "" : traziDrzavu%>&odBroja=<%=odBroja-broj%>&broj=<%=broj%>"><button>Prethodna</button></a></td>
	<td><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/pregledAerodroma?traziNaziv=<%= (traziNaziv==null) ? "" : traziNaziv%>&traziDrzavu=<%= (traziDrzavu==null) ? "" : traziDrzavu%>&odBroja=0&broj=<%=broj%>"><button>Početak</button></a></td><%
	}else{
	%><td><button>Prethodna</button></td>
 	<td><button>Početak</button></td><%
 	}
 	if(aerodromi.isEmpty() || aerodromi.size()<broj) {
  	%><td><button>Sljedeća</button></td><%
	}else{
 	%><td><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/pregledAerodroma?traziNaziv=<%= (traziNaziv==null) ? "" : traziNaziv%>&traziDrzavu=<%= (traziDrzavu==null) ? "" : traziDrzavu%>&odBroja=<%=odBroja+broj%>&broj=<%=broj%>"><button>Sljedeća</button></a></td><%
 	}%>
 	</tr>
  	</table>
	<table>
		<tbody>
			<tr>
				<th>ICAO</th>
				<th>Naziv</th>
				<th>Država</th>
				<th>Lokacija</th>
				<th>Dodaj za preuzimanje</th>
			</tr>
			<% for(Aerodrom aerodrom : aerodromi){
			  %>
			  <tr>
					<td><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/podaciAerodrom/<%=aerodrom.getIcao()%>"><button><%=aerodrom.getIcao() %></button></a></td>
					<td><%=aerodrom.getNaziv() %></td>
					<td><%=aerodrom.getDrzava() %></td>
					<td><%=aerodrom.getLokacija().getLatitude() %>, <%=aerodrom.getLokacija().getLongitude() %></td>
					<td><a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/dodajAerodrom?icao=<%=aerodrom.getIcao()%>"><button>Dodaj</button></a></td>
				</tr>
			  <%}%>
		</tbody>
	</table>
</body>
</html>