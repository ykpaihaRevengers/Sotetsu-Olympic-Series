<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	ファイル処理一覧
	<table>
		<tr>
			<td>条件パターン</td>
			<td>条件値</td>
			<td>処理方法</td>
			<td>処理値</td>
		</tr>
		<c:forEach items="${file_list.fileEditions}" var="edition">
			<tr>
				<td>${edition.pattern}</td>
				<td>${edition.filterValue}</td>
				<td>${edition.function}</td>
				<td><c:if test="${!empty edition.replacedValue}"> :${edition.replacedValue}</c:if></td>
			</tr>
		</c:forEach>


	</table>


	<form action="/base/DataBaseServlet?action=individualProcessing" method="post">
		<input type="submit" style="color: #ffffff; background-color: #f76e05;" value="この条件処理を登録する"> <input type="hidden" name="process" value="uplord_file_edition"> <input type="hidden"
			name="table_name" value="file_edition"> <input type="hidden" name="process" value="uplord_file_edition"> <input type="hidden" name="page" value="filemng/editFile.jsp">
	</form>
	<c:if test="${empty disable}">
		<form action="/base/FileEditServlet?action=edit" method="post">
			<input type="submit" style="color: #ffffff; background-color: #f76e05;" value="登録された条件で処理実行"> <input type="hidden" name="edition" value="execute_file">
		</form>
	</c:if>

	<c:if test="${!empty db_execute_msg}">${db_execute_msg}</c:if>
	<c:if test="${!empty file_execute_msg}">${file_execute_msg}</c:if>
	<br>
	<form action="/base/FittingServlet?action=manuscriptInsert" method="post">
		<input type="submit" style="color: #ffffff; background-color: #99a677;" value="整形したファイルを登録"> <input type="hidden" name="page" value="filemng/editFile.jsp">
	</form>
	<c:if test="${!empty execute_msg}">${execute_msg}<br>
		<form action="/base/DataBaseServlet" method="post">
			<input type="submit" value="トップに戻る">
		</form>
		<form action="/base/DataBaseServlet?action=select" method="post">
			<input type="hidden" name="table_name" value="schedule_manuscript"> <input type="submit" value="挿入された値を閲覧">
		</form>
	</c:if>

</body>
</html>