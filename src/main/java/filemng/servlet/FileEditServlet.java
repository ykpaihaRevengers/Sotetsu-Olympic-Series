package filemng.servlet;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import filemng.bean.FileList;
import home.dao.err.DAOException;
import home.servlet.MainServlet;
import home.tool.ScenarioUtil;

/**
 * Servlet implementation class FileEditServlet
 */
@WebServlet("/FileEditServlet")
public class FileEditServlet extends MainServlet {
	private static final long serialVersionUID = 1L;

	private static final String FILE_EXECUTE_MSG = "file_execute_msg";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileEditServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 初期処理
		super.doGet(request, response);
		FileList fileList = (FileList) session.getAttribute("file_list");

		if (action != null) {
			switch (action) {
			case "file":
				Path fullPath = Paths.get(Optional.ofNullable(getRequestParameter(request, "file_fullpath")).orElse(""));
				if (!ScenarioUtil.checkObjectValue(fullPath) || fullPath.toString().isBlank()) {
					fullPath = (Path) session.getAttribute("file_fullpath");
				}
				fileList = new FileList(fullPath);
				session.setAttribute("file_list", fileList);
				session.setAttribute("file_fullpath", fullPath);
				//ページへの転送
				gotoPage(request, response, page);
				break;
			case "edit":

				if (getRequestParameter(request, "execute").equals("execute")) {
					//入力した方法でファイルを編集し、その方法を登録
					fileList.setFileEditions(ScenarioUtil.mappingArrayList(fileList.getBookList(), book -> book.editTextbook(request)).get(0));
					request.setAttribute(FILE_EXECUTE_MSG, "入力した編集方法で処理を実行しました。");
				}

				if (super.getRequestParameter(request, "edition").equals("execute_file")) {
					//登録した編集方法で編集されたファイルを登録
					try {
						fileList.editFile(fileList.getFileEditions());
						request.setAttribute(FILE_EXECUTE_MSG, "登録した編集方法で処理を実行しました。");
					} catch (DAOException e) {
						request.setAttribute(FILE_EXECUTE_MSG, e.getMessage());
						e.printStackTrace();
					}
					request.setAttribute("disable", "disable");
				}

				gotoPage(request, response, "filemng/editFile.jsp");
				break;

			default:
				break;
			}

		} else {
			//ページへの転送
			gotoPage(request, response, page);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
