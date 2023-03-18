<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ファイルの編集</title>
</head>
<body>
	<h3>編集方法を選択</h3>
	<form>
		<input type="button" value="戻る" onClick="history.back()" dir="rtl">
	</form>
	<form action="/base/FileEditServlet?action=edit" method="post">

		<h4>フィルター</h4>

		キーワードで行を絞る
		<textarea name="filter" cols="26" rows="8"></textarea>
		特定のキーワードを除外する
		<textarea name="filter_remove" cols="14" rows="8"></textarea>

		<h4>結合及び置換</h4>
		<ul>
			<c:forEach var="f" begin="1" end="10">
				<li>条件：<select name="pattern${f}">
						<option value=""></option>
						<option value="equals">equals(完全一致)</option>
						<option value="startswith">startswith(前方一致)</option>
						<option value="endswith">endswith(後方一致)</option>
						<option value="contains">contains部分一致</option>
				</select><input type="text" name="filterValue${f}"> 処理：<select name="function${f}">
						<option value=""></option>
						<option value="!">!(除外)</option>
						<option value="match">match(指定)</option>
						<option value="ahead">head(前の文字と結合)</option>
						<option value="behind">tail(後の文字と結合)</option>
						<option value="replaceall">replaceall(置換)</option>
						<option value="remove">remove(除去)</option>
				</select><input type="text" name="functionChar${f}">
				</li>
			</c:forEach>
		</ul>
		<h4>文字の置換</h4>
		<c:forEach var="i" begin="1" end="10">
				置換前：<input type="text" name="before${i}"> 置換後：<input type="text" name="after${i}">
			<br>
		</c:forEach>
		<input type="submit" style="color: #ffffff; background-color: #2287ff;" value="処理実行"> <input type="hidden" name="execute" value="execute">
	</form>

	<form action="/base/DataBaseServlet?action=individualProcessing" method="post">
		<input type="submit" style="color: #ffffff; background-color: #e56df7;" value="登録した編集方法を呼び出す"> <input type="hidden" name="process" value="download_file_edition"> <input type="hidden"
			name="table_name" value="file_edition"> <input type="hidden" name="page" value="filemng/editFile.jsp"> 登録番号：<input type="text" pattern="^[1-9][0-9]*$" style="width: 20px;"
			name="file_edition_code" />
	</form>
	<input type="button" value="csvダウンロード" onclick="location.href='GenerateCsvServlet'">
	<br />
	<form action="/base/FileEditServlet?action=file" method="post">
		<input type="submit" style="color: #ffffff; background-color: #a9a9a9;" value="編集を取り消す"> <input type="hidden" name="page" value="file.jsp">
	</form>

	<c:if test="${!empty file_list.fileEditions}"><jsp:include page="/jsp/filemng/fileEditions.jsp"></jsp:include></c:if>
	<h4>参考：ファイルの中身</h4>
	<jsp:include page="/jsp/filemng/fileText.jsp"></jsp:include>

</body>
</html>