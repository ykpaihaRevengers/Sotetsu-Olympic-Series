package usr.УкраїнаRevengers.servlet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dbmng.bean.ExecuteQuery;
import dbmng.servlet.DataBaseServlet;
import home.dao.MainDAO;
import home.dao.err.DAOException;
import home.servlet.MainServlet;
import home.tool.ScenarioUtil;
import usr.УкраїнаRevengers.utility.DirectionsList;
import usr.УкраїнаRevengers.utility.NameJudging;
import usr.УкраїнаRevengers.utility.NameJudging.CodeHeader;

/**
 * Servlet implementation class CreateExcelServlet
 */
@WebServlet("/CreateExcelServlet")
public class CreateExcelServlet extends MainServlet implements DirectionsList {

	XSSFWorkbook workbook;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateExcelServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 初期処理
		super.doGet(request, response);

		try (MainDAO dao = new MainDAO((String) session.getAttribute(DataBaseServlet.DATABASE_NAME))) {

			workbook = new XSSFWorkbook();
			CellStyle cellStyle = workbook.createCellStyle();

			if (actionEquals(action, "createExcel")) {

				XSSFFont font = this.workbook.createFont();
				font.setFontName("Yu Gothic UI");
				cellStyle.setFont(font);
				cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

				for (CodeHeader codeHeader : CodeHeader.values()) {
					System.out.println(codeHeader);

					createSheetFromDirectionTable((HEADER + ＿ + codeHeader.toString() + ＿ + DIRECTIONA).toLowerCase(), dao, codeHeader, cellStyle);

					createSheetFromDirectionTable((HEADER + ＿ + codeHeader.toString() + ＿ + DIRECTIONB).toLowerCase(), dao, codeHeader, cellStyle);

				}

				FileOutputStream output = null;
				try {
					output = new FileOutputStream("allDirections.xlsx");
					workbook.write(output);
					System.out.println("完了。。");
				} catch (IOException e) {
					System.out.println(e.toString());
				} finally {
					try {
						if (output != null) {
							output.close();
						}
						if (workbook != null) {
							workbook.close();
						}
					} catch (IOException e) {
						System.out.println(e.toString());
					}
				}
			}

		} catch (DAOException e) {
			e.printStackTrace();
		}
		//ページへの転送
		gotoPage(request, response, page);
	}

	private void createSheetFromDirectionTable(String tableName, MainDAO dao, CodeHeader codeHeader, CellStyle cellStyle) throws DAOException {

		ExecuteQuery directionselect = new ExecuteQuery(tableName, dao);
		directionselect.setOrderByColumn(NameJudging.judgeStandardSortName(codeHeader, tableName));
		List<Map<String, Object>> directionData = dao.select(directionselect);

		Sheet sheet = workbook.createSheet(tableName);

		// keySetを使用してカラム列を作成する
		if (ScenarioUtil.checkList(directionData)) {
			System.out.println(directionData);
			int rowNumber = 0;
			for (String columnKey : directionData.get(0).keySet()) {
				Cell cell = sheet.createRow(rowNumber++).createCell(0);
				cell.setCellValue(columnKey.replaceAll("（.*?）", ""));
				cell.setCellStyle(cellStyle);

				//カラム列の幅の自動調整
				CellReference cellReference = new CellReference(rowNumber, 0);
				sheet.autoSizeColumn(cellReference.getCol());

			}

			int lineNumber = 1;
			for (Map<String, Object> data : directionData) {
				rowNumber = 0;
				boolean extent = false;
				String previousKey = "";
				String previousValue = "";
				String ikisaki = null;
				for (String columnKey : data.keySet()) {

					//カラム列の幅の自動調整
					CellReference cellReference = new CellReference(rowNumber, lineNumber);
					sheet.autoSizeColumn(cellReference.getCol());

					String value = (String) data.get(columnKey);

					if (ScenarioUtil.checkStringValue(value)) {
						extent = true;
					} else {
						if (!extent) {
							value = EXCEL_NULL;
						} else {
							value = NameJudging.judgeValueToNull(codeHeader, tableName, ikisaki, columnKey, previousKey, previousValue);
						}
						if (value.equals(EXCEL_NULL)) {
							extent = false;
						}
					}
					if (columnKey.equals("ikisaki")) {
						ikisaki = value;
						extent = false;
					}
					value = value.replaceAll("（.*?）", "");

					sheet.getRow(rowNumber++).createCell(lineNumber).setCellValue(value);
					previousKey = columnKey;
					previousValue = value;
				}
				lineNumber++;
			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
