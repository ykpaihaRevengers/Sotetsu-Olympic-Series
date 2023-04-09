package home.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import home.tool.ScenarioUtil;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//アクションパラメーター
	protected String action;
	protected HttpSession session;
	protected String page;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainServlet() {
		super();
	}

	/**
	 * @throws IOException 
	 * @throws ServletException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// 文字コードをUTF-8に指定
		request.setCharacterEncoding("UTF-8");
		//パラメーターの解析
		this.action = getRequestParameter(request, "action");
		request.setAttribute("action", action);
		//セッションオブジェクトの取得
		this.session = request.getSession();
		//遷移先の取得
		this.page = getRequestParameter(request, "page");
		request.setAttribute("page", page);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public static void gotoPage(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
		request.getRequestDispatcher("/jsp/" + page).forward(request, response);
	}

	/**
	 *  パラメーターのチェックメソッド
	 *  nullや空文字ではないことのチェック
	 *  
	 * @param request
	 * @param parameterTagName
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public static boolean checkRequestParameter(HttpServletRequest request, String... parameterTagNames) {
		return Arrays.asList(parameterTagNames).stream().map(name -> request.getParameter(name)).allMatch(p -> ScenarioUtil.checkStringValue(p));
	}

	/**
	 *  パラメーターの取得メソッド
	 *  nullの場合のみ、sessionから値を取得する
	 *  
	 * @param request
	 * @param parameterTagName
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String getRequestParameter(HttpServletRequest request, String parameterTagName) {
		return Optional.ofNullable(request.getParameter(parameterTagName)).orElse(Optional.ofNullable(request.getSession().getAttribute(parameterTagName)).orElse("").toString());
	}

	/**
	 *  パラメーターの取得メソッド
	 *  nullの場合のみ、sessionから値を取得する
	 *  
	 * @param request
	 * @param textareaName
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public static Stream<String> createStreamFromTextArea(HttpServletRequest request, String textareaName) {
		return Arrays.asList(getRequestParameter(request, textareaName).split("\r\n")).stream().filter(n -> n != null).filter(n -> !n.isEmpty()).map(n -> n.trim());
	}

	/**
	 * actionのパラメーターチェック
	 * @param action
	 * @param actionValue
	 * @return
	 */
	public boolean actionEquals(String action, String actionValue) {
		return Optional.ofNullable(action).orElse("").equals(actionValue);
	}

	/**
	 *  セッションのチェックメソッド
	 *  nullや空文字ではないことのチェック
	 *  
	 * @param session
	 * @param parameterTagNames
	 * @return
	 */
	public boolean checkSessionAttribute(HttpSession session, String... parameterTagNames) {
		return Arrays.asList(parameterTagNames).stream().map(name -> session.getAttribute(name)).allMatch(p -> ScenarioUtil.checkObjectValue(p));
	}

}
