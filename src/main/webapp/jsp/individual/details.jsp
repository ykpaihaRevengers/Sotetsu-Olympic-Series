<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>詳細</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/matrixShuffle.css">
</head>
<body>
	<h4>詳細内容</h4>
	コード：${deployed_code}
	<p>[${shubetsu}] ${ikisaki} 行</p>

	<table border="1">
		<tr>
			<th></th>
			<th>発</th>
			<th>着</th>
		</tr>
		<c:forEach items="${selectedData}" var="record">
			<tr>
				<td>${record.name}</td>
				<td>${record.arr}</td>
				<td>${record.dep}</td>
			</tr>
		</c:forEach>

	</table>


</body>
</html>