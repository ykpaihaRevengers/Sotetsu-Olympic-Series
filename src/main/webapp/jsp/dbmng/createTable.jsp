<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>テーブルの作成</title>
</head>
<style>
input {
	vertical-align: text-top;
}
</style>
<body>
	<a href="/base/DataBaseServlet" style="text-align: right;">データベース一覧に戻る</a>

	<h2>テーブルの作成</h2>
	<div style="display: inline-flex">

		<form action="/base/DataBaseServlet?action=createTableA" method="post">
			<h3>テキストからの読み込み</h3>
			<p>テーブル名入力</p>
			<input type="text" style="width: 200px;" name="table_name">
			<p>PRIMARY KEY入力</p>
			<input type="text" style="width: 250px;" name="primary_key">
			<p>カラムの定義</p>
			<textarea name="columns_list" cols="64" rows="10"></textarea>
			<p>カラムの区切り</p>
			<input type="radio" name="split_char" value="\t" checked> タブで区切る <input type="radio" name="split_char" value=","> カンマで区切る <input type="radio" name="split_char" value=" ">
			スペースで区切る
			<p>
				<input type="submit" value="DDL文の作成">
			</p>
		</form>

		<c:if test="${!empty db_execute_msg}">
			<p style="color: #666666;">${db_execute_msg}</p>
		</c:if>
		<form action="/base/DataBaseServlet?action=createTableB" method="post">
			<h3>エディタで作成</h3>
			<p>テーブル名入力</p>
			<input type="text" style="width: 200px;" name="table_name">
			<p>カラムの定義</p>
			<table>
				<tr>
					<td>カラム名</td>
					<td>DBタイプ</td>
					<td>文字数の指定</td>
					<td>NOT NULL</td>
					<td>デフォルト値</td>
				</tr>
				<c:forEach var="i" begin="1" end="10" step="1">
					<tr>
						<td><input type="text" style="width: 120px;" name="column_name${i}"></td>
						<td><select name="data_type${i}">
								<option value=""></option>
								<option value="SERIAL">SERIAL</option>
								<option value="INTEGER">INTEGER</option>
								<option value="TEXT">TEXT</option>
								<option value="VARCHAR">VARCHAR</option>
								<option value="CHAR">CHAR</option>
								<option value="DATE">DATE</option>
								<option value="TIME">TIME</option>
								<option value="TIMESTAMP">TIMESTAMP</option>

						</select></td>
						<td><input type="text" pattern="^[1-9][0-9]*$" style="width: 20px;" name="max_length${i}" /></td>
						<td><input type="checkbox" name="not_null${i}" value=" NOT NULL"></td>
						<td><input type="text" style="width: 100px;" name="default_value${i}"></td>
					</tr>
				</c:forEach>
			</table>
			PRIMARY KEY: <input type="text" style="width: 200px;" name="primary_key">
			<p>
				<input type="submit" value="DDL文の作成">
			</p>
		</form>
	</div>
</body>
</html>