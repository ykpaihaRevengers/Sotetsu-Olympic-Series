<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>DB更新前の確認</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
	<p>以下のSQLを実行し、データベースを更新します。</p>

	<c:forEach items="${sqls}" var="sql" begin="0" varStatus="status">
${status.count}.		${sql}<br>
	</c:forEach>

	<form action="/base/DataBaseServlet?action=executeUpdatefromList" method="post">
		<input type="submit" style="color: #ffffff; background-color: #ff1500;" value="SQLを実行する（元には戻せません）"> <input type="hidden" name="sqls" value="${sqls}">
	</form>
	<form action="/base/DataBaseServlet?action=quit" method="post">
		<input type="submit" style="color: #ffffff; background-color: #1129ee;" value="DB更新を取りやめる">
	</form>




</body>
</html>