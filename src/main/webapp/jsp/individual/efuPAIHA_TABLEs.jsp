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

		<c:forEach items="${table_names}" var="table">
			<tr>
				<td>${table}</td>
				<td>
					<form action="/base/DataBaseServlet?action=showColumns" method="post">
						<input type="hidden" name="table_name" value="${table}"> <input type="submit" value="カラム詳細"><input type="hidden" name="page" value="select.jsp">
					</form>
				</td>
				<td></td>
				<td>
					<form action="/base/DataBaseServlet?action=select" method="post">
						<input type="hidden" name="table_name" value="${table}"> <input type="submit" value="データ一覧"><input type="hidden" name="page" value="individual/direction.jsp">
					<input type="hidden" name="selecting" value="selecting">
					</form>
				</td>
			</tr>
		</c:forEach>
	</table>
	
	
</body>
</html>