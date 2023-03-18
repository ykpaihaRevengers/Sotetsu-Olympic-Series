<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/matrixShuffle.css">
</head>
<body>
	<h4>マトリクス化</h4>

	<form action="/base/FittingServlet?action=showTables" method="post">
		<input type="submit" style="color: #ffffff; background-color: #99a677;" value="登録データの閲覧"> <input type="hidden" name="page" value="select.jsp">
	</form>
	<form action="/base/FittingServlet?action=createTableByManuscriptInsert" method="post">
		<input type="submit" style="color: #ffffff; background-color: #99a677;" value="登録データによるテーブルの作成"> <input type="hidden" name="page" value="select.jsp">
	</form>

	<form action="/base/DataBaseServlet?action=truncate_table" method="post">
		<input type="submit" style="color: #ffffff; background-color: #8a1a04;" value="テーブルのリセット"> <input type="hidden" name="table_name" value="schedule_manuscript">
	</form>
	<c:if test="${!empty execute_message}">
		<p>${execute_message}</p>
	</c:if>
	<form action="/base/FittingServlet?action=deploy" method="post">
		<input type="submit" style="color: #ffffff; background-color: #44f702;" value="マトリクスにデプロイ"> <input type="hidden" name="table_name" value="schedule_manuscript"> <input type="hidden"
			name="page" value="select.jsp">
	</form>
	<br />
	<c:if test="${!empty showTables}">
		<jsp:include page="/jsp/individual/efuPAIHA_TABLEs.jsp"></jsp:include>
	</c:if>
	<br />
</body>
</html>