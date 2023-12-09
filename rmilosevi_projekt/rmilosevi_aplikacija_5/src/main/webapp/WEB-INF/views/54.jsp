<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="jakarta.servlet.ServletContext"%>
<%@page import="jakarta.jms.TextMessage"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<style>
<%@include file="/WEB-INF/css/style.css"%>
</style>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<%
ServletContext kontekst = (ServletContext) request.getAttribute("kontekst");
List<TextMessage> jmsPoruke = (List<TextMessage>) request.getAttribute("jmsPoruke");
%>
<title>5.4 - JMS Poruke</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.4 - JMS Poruke
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
<tr><th style="background: #211C1C;"><a href="${pageContext.servletContext.contextPath}/mvc/jms/obrisi"><button>Obriši</button></a></th></tr>
</table>
	<br>
	<table>
		<tr>
			<th colspan="2">Primljene JMS poruke</th>
		</tr>
		<%
		if (jmsPoruke.isEmpty()) {
		%>
		<td colspan="2">Nema poruka</td>
		<%
		} else {
		for (TextMessage poruka : jmsPoruke) {
		%>
		<tr>
			<td><%=poruka.getText() %></td>
		</tr>
		<%
			}
		}
		%>
	</table>
</body>
</html>