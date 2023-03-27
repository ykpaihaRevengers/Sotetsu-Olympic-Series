<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>テーブル一覧</title>
</head>
<body>
	<table border="1">
		<tr>
			<th>コード</th>
			<th>種別</th>
			<th>行先</th>
			<th>デプロイ日時</th>
			<th></th>
			<th>詳細表</th>
			<th></th>
			<th>削除する</th>
		</tr>
		<c:forEach items="${deployed_schedule_list}" var="record">
			<tr>
				<td>${record.deployed_code}</td>
				<td>${record.shubetsu}</td>
				<td>${record.ikisaki}</td>
				<td>${record.deployed_timestamp}</td>
				<td></td>
				<td>
					<form action="/base/FittingServlet?action=selectCode" method="post">
						<input type="hidden" name="deployed_code" value="${record.deployed_code}"> <input type="submit" value="詳細"><input type="hidden" name="page" value="individual/checkTableList.jsp">
					</form>
				</td>
				<td></td>
				<td>
					<form action="/base/FittingServlet?action=deleteCode" method="post">
						<input type="hidden" name="deployed_code" value="${record.deployed_code}"> <input type="submit" value="削除"><input type="hidden" name="page" value="individual/checkTableList.jsp">
					</form>
				</td>
			</tr>
		</c:forEach>
	</table>


</body>
</html>