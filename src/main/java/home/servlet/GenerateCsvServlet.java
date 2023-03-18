package home.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dbmng.bean.SelectedData;
import filemng.bean.FileList;
import filemng.bean.TextBook;
import home.tool.FileUtils;
import home.tool.ScenarioUtil;

/**
 * Servlet implementation class GenerateCsv
 */
@WebServlet("/GenerateCsvServlet")
public class GenerateCsvServlet extends MainServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GenerateCsvServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		String tableName = getRequestParameter(request, "table_name");
		String csvFileName = tableName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
		if (ScenarioUtil.checkStringValue(tableName)) {
			csvFileName = "DB_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";

		}
		response.setHeader("Content-Type", "text/csv; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + csvFileName + "\"");
		HttpSession session = request.getSession();
		PrintWriter out;
		try {
			System.out.println("ABC");
			out = response.getWriter();
			//文字化け防止のため、BOM付きのファイルとして設定
			out.write(0xFEFF);
			if (super.checkSessionAttribute(session, "selected_data")) {
				System.out.println("SED");

				SelectedData selectedData = (SelectedData) session.getAttribute("selected_data");
				System.out.println("dddSED");

				//ヘッダーのカラムを出力
				out.append(selectedData.getColumns().stream().map(c -> c.getColumnName()).collect(Collectors.joining(",")) + FileUtils.CRLF);
				//SELECTされたデータを出力
				for (Map<String, Object> map : selectedData.getDbDataList()) {
					out.append(map.entrySet().stream().map(e -> e.getValue().toString()).collect(Collectors.joining(",")) + FileUtils.CRLF);
				}
			}

			if (super.checkSessionAttribute(session, "file_list")) {
				FileList fileList = (FileList) session.getAttribute("file_list");

				for (TextBook textBook : fileList.getBookList()) {
					csvFileName = textBook.getFileName() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
					textBook.getFileText().stream().filter(a -> ScenarioUtil.checkStringValue(a)).map(b -> b + FileUtils.CRLF).forEachOrdered(out::append);

				}

			}
			System.out.println(actionEquals(action, "outputToCsv"));
			System.out.println(getRequestParameter(request, "action"));

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
