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
Integer broj = Integer.parseInt(kontekst.getAttribute("stranica.brojRedova").toString());
%>
<title>5.6 - Letovi</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.6 - Letovi
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
				<td><a href="${pageContext.servletContext.contextPath}/mvc/letovi/polasciInterval?odBroja=0&broj=<%=broj%>"><button>Letovi s aerodroma u intervalu</button></a></td>
			</tr>
			<tr>
				<td><a href="${pageContext.servletContext.contextPath}/mvc/letovi/polasciDatum?odBroja=0&broj=<%=broj%>"><button>Letovi s aerodroma na datum</button></a></td>
			</tr>
			<tr>
				<td><a href="${pageContext.servletContext.contextPath}/mvc/letovi/polasciDatumOS"><button>Letovi s aerodroma na datum (OS)</button></a></td>
			</tr>
		</tbody>
	</table>
</body>
</html>