<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>比較</title>
</head>
<body>
	<h4>比較内容</h4>
	＜原本＞ コード：${genpon_schedule.code}
	<p>[${genpon_schedule.orthodoxShubetsu}] ${genpon_schedule.ikisaki} 行</p>
	＜今回の編集＞ コード：${old_schedule.code}
	<p>[${old_schedule.orthodoxShubetsu}] ${old_schedule.ikisaki} 行</p>
	<div style="display: inline-flex">
		<table border="1">
			<tr>
				<th></th>
				<th>着</th>
				<th>発</th>
				<th>所要時間</th>
				<th>待機時間</th>
			</tr>
			<c:forEach items="${genpon_schedule.scheduleTags}" var="record">
				<tr>
					<td>${record.name}</td>
					<td>${record.arrive}</td>
					<td>${record.departure}</td>
					<td>${record.spending}</td>
					<td>${record.waitMoment}</td>
				</tr>
			</c:forEach>
		</table>
		<table border="1">
			<tr>
				<th></th>
				<th>着</th>
				<th>発</th>
				<th>所要時間</th>
				<th>待機時間</th>
				<th></th>
				<th></th>
				<th>差分（着）</th>
				<th>差分（発）</th>
			</tr>
			<c:forEach items="${new_schedule.scheduleTags}" var="record">
				<tr>
					<td>${record.name}</td>
					<td>${record.arrive}</td>
					<td>${record.departure}</td>
					<td>${record.spending}</td>
					<td>${record.waitMoment}</td>
					<td></td>
					<td></td>
					<td>${record.comparingArrive}</td>
					<td>${record.comparingDeparture}</td>

				</tr>
			</c:forEach>
		</table>
	</div>
</body>
</html>