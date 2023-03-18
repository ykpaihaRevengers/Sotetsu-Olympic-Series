package usr.УкраїнаRevengers.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbmng.bean.CreateTable;
import dbmng.bean.CreateTable.ColumnTypes;
import dbmng.bean.ExecuteQuery;
import dbmng.bean.Insert;
import dbmng.servlet.DataBaseServlet;
import filemng.bean.FileList;
import filemng.bean.TextBook;
import home.dao.MainDAO;
import home.dao.MainDAO.CurrentDateTimeStamp;
import home.dao.MainDAO.Operator;
import home.dao.err.DAOException;
import home.servlet.MainServlet;
import home.tool.ScenarioUtil;
import usr.УкраїнаRevengers.bean.ManuscriptScedule;
import usr.УкраїнаRevengers.bean.ManuscriptScedule.DirectionBaseColumns;
import usr.УкраїнаRevengers.bean.ScheduleTag;
import usr.УкраїнаRevengers.utility.NameJudging;
import usr.УкраїнаRevengers.utility.NameJudging.Athletes;
import usr.УкраїнаRevengers.utility.NameJudging.CodeHeader;
import usr.УкраїнаRevengers.utility.NameJudging.Direction;
import usr.УкраїнаRevengers.utility.NameJudging.FPAIPAColumnname;
import usr.УкраїнаRevengers.utility.NameJudging.Terminus;

/**
 * Servlet implementation class FittingServlet
 */
@WebServlet("/FittingServlet")
public class FittingServlet extends MainServlet {
	private static final long serialVersionUID = 1L;
	public static final String HEADER = "FPAIHA";
	public static final String ＿ = "_";
	public static final String DIRECTION = "direction";
	public static final String NAMELIST = "namelist";
	public static final String ARR = "着";
	public static final String DEP = "発";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FittingServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 初期処理
		super.doGet(request, response);
		String tableName = getRequestParameter(request, "table_name");

		try (MainDAO dao = new MainDAO((String) session.getAttribute(DataBaseServlet.DATABASE_NAME))) {

			if (actionEquals(action, "manuscriptInsert")) {
				tableName = "schedule_manuscript";
				FileList fileList = (FileList) session.getAttribute("file_list");
				List<ManuscriptScedule> schedules = ScenarioUtil.mappingArrayList(fileList.getBookList(), book -> arrangeTextBook(book));

				Insert insert = new Insert(tableName, dao);
				schedules.stream().map(schedule -> schedule.createInsertValues()).flatMap(list -> list.stream()).forEachOrdered(insert::setInsertValue);
				String result = dao.insert(insert);
				request.setAttribute("execute_msg", "DBへの反映が" + result + "件反映されました。");

			}

			if (actionEquals(action, "showTables")) {
				request.setAttribute("table_name", "schedule_manuscript");
				request.setAttribute("table_names", dao.showTableList(HEADER.toLowerCase(), Operator.STARTSWITH));
				request.setAttribute("showTables", "showTables");
			}

			if (actionEquals(action, "createTableByManuscriptInsert")) {
				tableName = "schedule_manuscript";

				List<String> selectColumnList = new ArrayList<>();
				selectColumnList.add("name");

				for (CodeHeader header : CodeHeader.values()) {
					ExecuteQuery select = new ExecuteQuery(tableName, dao, selectColumnList);
					//	String headerValue = ;
					Direction aORb = Direction.lB;
					if (header.equals(CodeHeader.SO)) {
						aORb = Direction.lA;
					}
					select.setFilter("code", Operator.STARTSWITH, header.toString());
					select.setFilter("code", Operator.ENDSWITH, aORb.toString());

					List<Map<String, Object>> selectedList = dao.select(select);
					List<String> nameList = new ArrayList<String>(new LinkedHashSet<>(ScenarioUtil.mappingArrayList(selectedList, record -> (String) record.get("name"))));
					if (header.equals(CodeHeader.MG)) {
						Collections.reverse(nameList);
						Arrays.asList(Athletes.values()).stream().map(athlete -> athlete.toString()).forEachOrdered(nameList::add);
						Collections.reverse(nameList);
					}

					CreateTable table = new CreateTable(HEADER + ＿ + header.toString() + ＿ + DIRECTION + aORb.getDirKey(aORb), "code");
					table = inputArriveAndDeparture(nameList, table, header, aORb);

					String msg = dao.createTable(table);
					aORb = aORb.exchangeAB(aORb);
					Collections.reverse(nameList);

					CreateTable tableReverse = new CreateTable(HEADER + ＿ + header.toString() + ＿ + DIRECTION + aORb.getDirKey(aORb), "code");
					tableReverse = inputArriveAndDeparture(nameList, tableReverse, header, aORb);

					String msgBack = dao.createTable(tableReverse);

					if (header.equals(CodeHeader.SO) || header.equals(CodeHeader.SI) || header.equals(CodeHeader.TJ)) {
						Collections.reverse(nameList);
					}

					String nameListTableName = HEADER + ＿ + header.toString() + ＿ + "nameList";
					CreateTable nameListtable = new CreateTable(nameListTableName, "name");
					nameListtable.setColumnAttr("num", ColumnTypes.SERIAL, 0, true, "");
					nameListtable.setColumnAttr("name", ColumnTypes.VARCHAR, 40, true, "");
					String msgNameListtable = dao.createTable(nameListtable);

					Insert inserttoNameList = new Insert(nameListTableName, dao);
					for (String name : nameList) {
						List<String> insertValue = new ArrayList<>();
						insertValue.add(name);
						inserttoNameList.setInsertValue(insertValue);
					}
					String messageInsertNameList = dao.insert(inserttoNameList);

					request.setAttribute("execute_message", msg + " " + msgBack + " " + msgNameListtable + " " + messageInsertNameList);
				}

			}

			if (actionEquals(action, "deploy")) {
				ExecuteQuery select = new ExecuteQuery(tableName, dao, Arrays.asList("code"));
				select.setGroupByColumn("code");
				List<String> deployCodes = ScenarioUtil.mappingArrayList(dao.select(select), map -> (String) map.get("code"));

				boolean msgFlg = true;
				String place = "";
				for (String deployCode : deployCodes) {
					List<String> deployMessage = deploy(deployCode, tableName, dao);
					try {
						deployMessage.stream().forEachOrdered(msg -> Integer.parseInt(msg));

					} catch (Exception e) {
						msgFlg = false;
						place = deployCode;
						e.printStackTrace();
					}
				}

				if (msgFlg) {
					request.setAttribute("execute_message", "deployが完了しました。");

				} else {
					request.setAttribute("execute_message", "deployで" + place + "に不備が見つかりました。");
				}

			}

		} catch (DAOException e) {
			e.printStackTrace();
		}
		//ページへの転送
		gotoPage(request, response, page);

	}

	private CreateTable inputArriveAndDeparture(List<String> nameList, CreateTable table, CodeHeader header, Direction aORb) {

		table.setColumnAttr(FPAIPAColumnname.code.toString(), ColumnTypes.VARCHAR, 10, true, "");
		table.setColumnAttr(FPAIPAColumnname.shubetsu.toString(), ColumnTypes.VARCHAR, 20, false, "");
		table.setColumnAttr(FPAIPAColumnname.ikisaki.toString(), ColumnTypes.VARCHAR, 30, false, "");

		for (String name : nameList) {
			if (NameJudging.judgeEnding(name, header, aORb).equals(Terminus.isTerminusA)) {
				if (aORb.toString().contains("A")) {
					//Aの場合
					table.setColumnAttr(name + ＿ + ARR, ColumnTypes.TIME, 30, false, "");
				} else {
					//Bの場合
					table.setColumnAttr(name + ＿ + DEP, ColumnTypes.TIME, 30, false, "");
				}
			} else if (NameJudging.judgeEnding(name, header, aORb).equals(Terminus.isTerminusB)) {
				if (aORb.toString().contains("A")) {
					//Aの場合
					table.setColumnAttr(name + ＿ + DEP, ColumnTypes.TIME, 30, false, "");
				} else {
					//Bの場合
					table.setColumnAttr(name + ＿ + ARR, ColumnTypes.TIME, 30, false, "");
				}
			} else {
				table.setColumnAttr(name + ＿ + ARR, ColumnTypes.TIME, 30, false, "");
				table.setColumnAttr(name + ＿ + DEP, ColumnTypes.TIME, 30, false, "");
			}
		}

		return table;
	}

	private List<String> deploy(String deployCode, String tableName, MainDAO dao) throws DAOException {

		List<CodeHeader> codeHeaders = NameJudging.judgeAttribute(deployCode);

		ExecuteQuery deployCodeSelect = new ExecuteQuery(tableName, dao, Arrays.asList("code", "name", "arr", "dep"));
		deployCodeSelect.setFilter("code", Operator.EQUALS, deployCode);
		List<Map<String, Object>> schedules = dao.select(deployCodeSelect);
		String ikisaki = (String) schedules.get(schedules.size() - 1).get("name");

		List<String> executeMessage = new ArrayList<>();

		// ヘッダーごとの処理
		for (CodeHeader codeHeader : codeHeaders) {
			String namelistTableName = (HEADER + ＿ + codeHeader.toString() + ＿ + "nameList").toLowerCase();

			List<String> nameList = ScenarioUtil.mappingArrayList(dao.select(new ExecuteQuery(namelistTableName, dao, Arrays.asList("name"))), map -> (String) map.get("name"));
			String insertTableName = (HEADER + ＿ + codeHeader.toString() + ＿ + "direction").toLowerCase();
			if (deployCode.endsWith("A")) {
				insertTableName = insertTableName + "a";
			} else if (deployCode.endsWith("B")) {
				insertTableName = insertTableName + "b";
			}
			List<Map<String, Object>> filteredSchedules = ScenarioUtil.filteringArrayList(schedules,
					map -> NameJudging.judgeSchedule(codeHeader, deployCode, nameList, (String) map.get("name")));

			List<String> insertFilter = DirectionBaseColumns.getColumnNames();
			for (Map<String, Object> schedule : filteredSchedules) {

				if (ScenarioUtil.checkObjectValue(schedule.get("arr"))) {
					insertFilter.add((String) schedule.get("name") + ＿ + ARR);
				}
				if (ScenarioUtil.checkObjectValue(schedule.get("dep"))) {
					insertFilter.add((String) schedule.get("name") + ＿ + DEP);
				}
			}
			Insert insert = new Insert(insertTableName, dao, insertFilter);
			List<String> insertValues = new ArrayList<>();
			insertValues.add(deployCode);//codeの値挿入
			List<String> scheduleNames = ScenarioUtil.mappingArrayList(filteredSchedules, map -> (String) map.get("name"));
			insertValues.add(NameJudging.judgeShubetsu(codeHeader, deployCode, scheduleNames).toString());//shubetsuの値挿入
			insertValues.add(ikisaki);//ikisakiの値挿入

			for (Map<String, Object> schedule : filteredSchedules) {

				String arrTime = (String) schedule.get("arr");
				if (ScenarioUtil.checkStringValue(arrTime)) {
					insertValues.add(arrTime);
				}
				String depTime = (String) schedule.get("dep");
				if (ScenarioUtil.checkStringValue(depTime)) {
					insertValues.add(depTime);
				}

			}
			System.out.println("insertFilter   " + insertFilter);
			System.out.println("insertValues   " + insertValues);
			insert.setInsertValue(insertValues);
			executeMessage.add(dao.insert(insert));
		}

		//schedule_manuscript_inserted への代入
		Insert insert = new Insert("schedule_manuscript_inserted", dao);
		for (Map<String, Object> schedule : schedules) {
			List<String> insertValue = new ArrayList<>();
			insertValue.add(deployCode);
			insertValue.add((String) schedule.get("name"));
			insertValue.add((String) schedule.get("arr"));
			insertValue.add((String) schedule.get("dep"));
			insertValue.add(CurrentDateTimeStamp.CURRENT_TIMESTAMP.toString());
			insert.setInsertValue(insertValue);
		}

		executeMessage.add(dao.insert(insert));

		return executeMessage;
	}

	private ManuscriptScedule arrangeTextBook(TextBook book) {

		List<String> fileText = book.getFileText();
		book.getOrdinalCode();
		boolean firstText = true;
		ManuscriptScedule manuSchedule = new ManuscriptScedule();
		List<ScheduleTag> scheduleTags = new ArrayList<>();
		for (String text : fileText) {
			String[] manuscriptArray = text.split(",");
			if (manuscriptArray.length == 2) {
				if (firstText) {
					scheduleTags.add(new ScheduleTag(manuscriptArray[1], null, (manuscriptArray[0])));
					firstText = false;
				} else {
					scheduleTags.add(new ScheduleTag(manuscriptArray[1], (manuscriptArray[0])));
				}
			}
			if (manuscriptArray.length == 3) {
				scheduleTags.add(new ScheduleTag(manuscriptArray[2], (manuscriptArray[0]), (manuscriptArray[1])));
			}
		}
		List<String> nameList = ScenarioUtil.mappingArrayList(scheduleTags, scheduleTag -> scheduleTag.getName().trim());

		manuSchedule.setScheduleTags(scheduleTags);
		manuSchedule.setCode(book.getOrdinalCode(), nameList);

		return manuSchedule;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
