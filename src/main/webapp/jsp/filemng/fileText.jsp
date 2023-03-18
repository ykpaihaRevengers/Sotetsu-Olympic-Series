<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ファイルの中身</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>

	<c:if test="${!empty file_list}">
		<h5>対象ディレクトリ：${file_list.dirPath}</h5>
		<div class="display_flex">
			<c:forEach items="${file_list.bookList}" var="TextBook">
				<div class="fileBackGround">

					<h4>${TextBook.fileName}</h4>
					<br>
					<c:forEach items="${TextBook.fileText}" var="line">${line}<br>

					</c:forEach>
				</div>
			</c:forEach>
		</div>
	</c:if>

</body>
</html>