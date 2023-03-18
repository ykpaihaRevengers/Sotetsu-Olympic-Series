<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${table_name}のデータ挿入</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
	<h3>${table_name}のデータ挿入</h3>
	<form>
		<input type="button" value="戻る" onClick="history.back()" dir="rtl">
	</form>
	<form action="/base/DataBaseServlet?action=insert" method="post">

		<table border="1">
			<tr>
				<c:forEach items="${insert_columns}" var="insert_column">
					<th>${insert_column.columnName}</th>
				</c:forEach>
			</tr>
			<tr>
				<c:forEach items="${insert_columns}" var="insert_column">
					<td>${insert_column.dataType}</td>
				</c:forEach>
			</tr>
			<tr>
				<c:forEach items="${insert_columns}" var="insert_column">
					<td></td>
				</c:forEach>
			</tr>
			<tr>
				<c:forEach items="${insert_columns}" var="column" begin="0" varStatus="status">
					<td><input type="text" style="width: 120px;" name="insert_value${status.count}"></td>
				</c:forEach>
			</tr>
		</table>
		<textarea name="insert_list" cols="80" rows="30"></textarea>
		<p>区切り文字</p>
		<input type="radio" name="split_char" value="\t" checked> タブで区切る <input type="radio" name="split_char" value=","> カンマで区切る <input type="radio" name="split_char" value=" ">
		スペースで区切る<input type="hidden" name="page" value="dbmng/checkSQL.jsp"> <input type="submit" value="データを挿入する">
	</form>
	<br>
	<table border="1">
		<tr>
			<c:forEach items="${columns}" var="referenceColumn">
				<th>${referenceColumn.columnName}</th>
			</c:forEach>
		</tr>
		<tr>
			<c:forEach items="${columns}" var="referenceColumn">
				<td></td>
			</c:forEach>
		</tr>
		<c:forEach items="${selected_data.dbDataList}" var="record">
			<tr>
				<c:forEach items="${record}" var="element">
					<td>${element.value}</td>
				</c:forEach>
			</tr>
		</c:forEach>
	</table>
</body>
</html>