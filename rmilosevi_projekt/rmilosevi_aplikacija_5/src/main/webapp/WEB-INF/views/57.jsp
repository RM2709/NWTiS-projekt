<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="jakarta.servlet.ServletContext"%>
<%@page import="org.foi.nwtis.podaci.Dnevnik"%>
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
List<Dnevnik> podaci = (List<Dnevnik>) request.getAttribute("podaci");
Integer odBroja = Integer.parseInt(request.getAttribute("odBroja").toString());
Integer broj = Integer.parseInt(request.getAttribute("broj").toString());
String vrsta;
if(request.getAttribute("vrsta")!=null){
  vrsta = request.getAttribute("vrsta").toString();
} else{
  vrsta = "";
}

%>
<title>5.7 - Dnevnik</title>
</head>
<header>
	<h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/index">
			<i class="fa fa-home" id="home"></i> 
		</a>
		5.7 - Dnevnik
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
	<br>
	<form method="GET"
		action="${pageContext.servletContext.contextPath}/mvc/dnevnik"
		enctype="multipart/form-data">
  	<input type="radio" id="AP2" name="vrsta" value="AP2"<%if(vrsta!=null && vrsta.compareTo("AP2")==0){%> checked<%}%>>
  	<label for="AP2">AP2</label>&nbsp;&nbsp;&nbsp;
  	<input type="radio" id="AP4" name="vrsta" value="AP4"<%if(vrsta!=null && vrsta.compareTo("AP4")==0){%> checked<%}%>>
  	<label for="AP4">AP4</label>&nbsp;&nbsp;&nbsp;
  	<input type="radio" id="AP5" name="vrsta" value="AP5"<%if(vrsta!=null && vrsta.compareTo("AP5")==0){%> checked<%}%>>
 	<label for="AP5">AP5</label>&nbsp;&nbsp;&nbsp;
  	<input type="radio" id="bezfiltera" name="vrsta" value=""<%if(vrsta==null || vrsta.compareTo("")==0){%> checked<%}%>>
 	<label for="bezfiltera">Bez filtera</label>&nbsp;&nbsp;&nbsp;<br><br>
	<input type="hidden" id="odBroja" name="odBroja" value="0">
	<input type="hidden" id="broj" name="broj" value="<%=broj%>">
	<input type="submit">
	</form>
	<br>
			<table>
			  <tr>
			  <%if(odBroja>0) {
			  %><td><a href="${pageContext.servletContext.contextPath}/mvc/dnevnik?vrsta=<%=vrsta%>&odBroja=<%=odBroja-broj%>&broj=<%=broj%>"><button>Prethodna</button></a></td>
			  <td><a href="${pageContext.servletContext.contextPath}/mvc/dnevnik?vrsta=<%=vrsta%>&odBroja=0&broj=<%=broj%>"><button>Početak</button></a></td><%
			  }else{
			  %><td><button>Prethodna</button></td>
			  <td><button>Početak</button></td><%
			  }
			  if(podaci.isEmpty() || podaci.size()<broj) {
			  %><td><button>Sljedeća</button></td><%
			  }else{
			  %><td><a href="${pageContext.servletContext.contextPath}/mvc/dnevnik?vrsta=<%=vrsta%>&odBroja=<%=odBroja+broj%>&broj=<%=broj%>"><button>Sljedeća</button></a></td><%
			  }%>
			  </tr>
			  </table>
	<table>
		<tbody>
			<tr>
				<th>Zahtjev</th>
				<th>Vrsta</th>
				<th>Metoda</th>
				<th>Parametri</th>
			</tr>
			<% for(Dnevnik zapis : podaci){
			  %>
			  <tr>
					<td><%=zapis.zahtjev()%></td>
					<td><%=zapis.vrsta()%></td>
					<td><%=zapis.metoda()%></td>
					<td><%=zapis.parametri()%></td>
				</tr>
			  <%}%>
		</tbody>
	</table>
</body>
</html>