package controllers;

import java.io.IOException;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Task;
import utils.DBUtil;

/**
 * Servlet implementation class UpdateServlet
 */
@WebServlet("/update")
public class UpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String _token = request.getParameter("_token");
        //CSRF対策
        if(_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();

         // 既にある物なのでインスタンス生成はいらない
         // セッションスコープからメッセージのIDを取得して
         // 該当のIDのメッセージ1件のみをデータベースから取得
            //editページから飛んでいるので、task_idで取得
            Task t = em.find(Task.class, (Integer)(request.getSession().getAttribute("task_id")));

         // 各フィールドに上書きするコード
            String title = request.getParameter("title");
            t.setTitle(title);

            String content = request.getParameter("content");
            t.setContent(content);

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            t.setUpdated_at(currentTime);       // 更新日時のみ上書き

         // コミットすれば変更が反映されるので、永続化em.persist(t);は不要
         // データベースを更新
            em.getTransaction().begin();
            em.getTransaction().commit();
            em.close();

         // セッションスコープ上の不要になったデータを削除
            request.getSession().removeAttribute("task_id");

         // indexページへリダイレクト
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }

}