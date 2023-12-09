<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="jakarta.servlet.ServletContext"%>
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
Integer broj = Integer.parseInt(kontekst.getAttribute("stranica.brojRedova").toString());
if(request.getAttribute("neuspjesnaPrijava")!=null){
  %>
 	<script>
 	window.alert("Netočni korisnički podaci!");
	</script>
  <%
}
%>
<title>5.1 - Početni</title>
</head>
<header>
	<h1>
		<i class="fa fa-home" id="home"></i> 
		5.1 - Početni
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
				<td><a href="${pageContext.servletContext.contextPath}/mvc/korisnici"><button>5.2 Korisnici</button></a></td>
			</tr>
			<tr>
				<td><a href="${pageContext.servletContext.contextPath}/mvc/posluzitelj"><button>5.3 Poslužitelj</button></a></td>
			</tr>
			<tr>
				<td><a href="${pageContext.servletContext.contextPath}/mvc/jms"><button>5.4 JMS Poruke</button></a></td>
			</tr>
			<tr>
				<td>
				<% if(kontekst.getAttribute("korisnik")!=null){
				  %>
				  <a href="${pageContext.servletContext.contextPath}/mvc/aerodromi"><button>5.5 Aerodromi</button></a>
				  <%} else{%>
				 	<button>5.5 Aerodromi</button>
				  <%}%>	
				</td>
			</tr>
			<tr>
				<td>
				<% if(kontekst.getAttribute("korisnik")!=null){
				  %>
				  <a href="${pageContext.servletContext.contextPath}/mvc/letovi"><button>5.6 Letovi</button></a>
				  <%} else{%>
				 	<button>5.6 Letovi</button>
				  <%}%>	
				</td>
			</tr>
			<tr>
				<td>
				<% if(kontekst.getAttribute("korisnik")!=null){
				  %>
				  <a href="${pageContext.servletContext.contextPath}/mvc/dnevnik?odBroja=0&broj=<%=broj%>"><button>5.7 Dnevnik</button></a>
				  <%} else{%>
				 	<button>5.7 Dnevnik</button>
				  <%}%>	
					</td>
			</tr>
		</tbody>
	</table>
</body>
</html>