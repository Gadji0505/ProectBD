package com.codewiki.controller;

import com.codewiki.dao.ReportDao;
import com.codewiki.dao.UserDao;
import com.codewiki.model.Report;
import com.codewiki.model.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final ReportDao reportDao = new ReportDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo() != null ? request.getPathInfo() : "/";

        switch (action) {
            case "/dashboard":
                showDashboard(request, response);
                break;
            case "/users":
                listUsers(request, response);
                break;
            case "/reports":
                listReports(request, response);
                break;
            case "/delete-user":
                deleteUser(request, response);
                break;
            case "/delete-article":
                deleteArticle(request, response);
                break;
            case "/resolve-report":
                resolveReport(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/templates/admin/dashboard.html").forward(request, response);
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> users = userDao.getAllUsers();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/templates/admin/users.html").forward(request, response);
    }

    private void listReports(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Report> reports = reportDao.getAllReports();
        request.setAttribute("reports", reports);
        request.getRequestDispatcher("/templates/admin/reports.html").forward(request, response);
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = Integer.parseInt(request.getParameter("id"));
        userDao.deleteUser(userId);
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

    private void deleteArticle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int articleId = Integer.parseInt(request.getParameter("id"));
        // Implement article deletion logic
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    private void resolveReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int reportId = Integer.parseInt(request.getParameter("id"));
        reportDao.resolveReport(reportId);
        response.sendRedirect(request.getContextPath() + "/admin/reports");
    }
}