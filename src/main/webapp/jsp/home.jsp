<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>テーブル一覧</title>
</head>
<body>
	<h1>データベース 「${database_name}」のデータ検索</h1>

	<jsp:include page="/html/home.html"></jsp:include>

	<c:if test="${!empty db_execute_msg}">
		<p style="color: #666666;">${db_execute_msg}</p>
	</c:if>
	<h3>登録データの閲覧</h3>
	<form action="/base/FittingServlet?action=showTables" method="post">
		<input type="submit" style="color: #ffffff; background-color: #99a677;" value="登録データの閲覧"> <input type="hidden" name="page" value="individual/checkTableList.jsp">
	</form>
	<h3>ファイルの読み込み</h3>
	<form action="/base/FileEditServlet?action=file" method="post">
		<input type="text" name="file_fullpath" style="width: 950px;"> <input type="submit" style="color: #ffffff; background-color: #456789;" value="読み込む"> <input type="hidden" name="page"
			value="file.jsp">
	</form>
	<h3>テーブルの追加</h3>
	<form action="/base/DataBaseServlet?action=create_table" method="post">
		<input type="submit" style="color: #ffffff; background-color: #2287ff;" value="テーブルの追加">
	</form>
	<h3>テーブル一覧</h3>
	<hr>
	<table border="1">
		<tr>
			<th>テーブル名</th>
			<th>リンク</th>
			<th></th>
			<th>全データ確認</th>
			<th></th>
			<th>テーブルの削除</th>
			<th>テーブル名の変更</th>
		</tr>
		<c:forEach items="${table_names}" var="table">
			<tr>
				<td>${table}</td>
				<td>
					<form action="/base/DataBaseServlet?action=showColumns" method="post">
						<input type="hidden" name="table_name" value="${table}"> <input type="submit" value="カラム詳細">
					</form>
				</td>
				<td></td>
				<td>
					<form action="/base/DataBaseServlet?action=select" method="post">
						<input type="hidden" name="table_name" value="${table}"> <input type="submit" value="データ一覧">
					</form>
				</td>
				<td></td>
				<td>
					<form action="/base/DataBaseServlet?action=drop_table" method="post">
						<input type="hidden" name="table_name" value="${table}"> <input type="submit" style="color: #ffffff; background-color: #ff0000;" value="テーブル本体を削除">
					</form>
				</td>
				<td>
					<form action="/base/DataBaseServlet?action=alter_table" method="post">
						<input type="hidden" name="alterAction" value=" RENAME TO "> <input type="hidden" name="table_name" value="${table}"> <input type="submit"
							style="color: #ffffff; background-color: #3cb371;" value="テーブル名を変更">
					</form>
				</td>
			</tr>
		</c:forEach>
	</table>
	<br>



	<h3>HTML5</h3>
	<form action="/base/FileEditServlet?action=file" method="post">
		<input type="text" name="file_fullpath" style="width: 950px;"> <input type="submit" style="color: #ffffff; background-color: #456789;" value="読み込む"> <input type="hidden" name="page"
			value="file.jsp">
	</form>
</body>
</html>