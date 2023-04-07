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
	コード：${deployedSchedule.code}
	<p>[${deployedSchedule.orthodoxShubetsu}] ${deployedSchedule.ikisaki} 行</p>

	<table border="1">
		<tr>
			<th></th>
			<th>着</th>
			<th>発</th>
			<th>所要時間</th>
			<th>待機時間</th>
			<th>トータル</th>

		</tr>
		<c:forEach items="${deployedSchedule.scheduleTags}" var="record">
			<tr>
				<td>${record.name}</td>
				<td>${record.arrive}</td>
				<td>${record.departure}</td>
				<td>${record.spending}</td>
				<td>${record.waitMoment}</td>
				<td>${record.totalMinute}</td>

			</tr>
		</c:forEach>

	</table>


</body>
</html>