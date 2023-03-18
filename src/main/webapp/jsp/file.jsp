<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${file_fullpath}の内容確認</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
	<a href="/base/DataBaseServlet" style="text-align: right;">データベース一覧に戻る</a>
	<h3>${text_book.fileName}</h3>
	<div style="display: inline-flex">
		<form action="/base/FileEditServlet?action=edit" method="post">
			<input type="submit" style="color: #ffffff; background-color: #987654;" value="ファイルの編集"> <input type="hidden" name="init" value="init">
		</form>
		<form action="/base/DataBaseServlet?action=readSQL" method="post">
			<input type="submit" style="color: #ffffff; background-color: #987654;" value="SQLに反映する"> <input type="hidden" name="page" value="dbmng/checkSQLfromText.jsp">
		</form>
	</div>

	<p>
	<jsp:include page="/jsp/filemng/fileText.jsp"></jsp:include>

</body>
</html>