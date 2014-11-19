<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Workshops of today</title>
</head>
<h1>Persons</h1>
 
<table style="border: 1px solid; width: 500px; text-align:center">
 <thead style="background:#fcf">
  <tr>
   <th>Title</th>
   <th>Teacher</th>
  </tr>
 </thead>
 <tbody>
 <c:forEach items="${workshops}" var="workshop">
  <tr>
   <td><c:out value="${workshop.title}" /></td>
   <td><c:out value="${workshop.teacher.username}" /></td>
  </tr>
 </c:forEach>
 </tbody>
</table>
 
<c:if test="${empty workshops}">
 There are currently no workshops today for that user .
</c:if>
<body>