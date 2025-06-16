package com.codewiki.controller;

import com.codewiki.dao.TagDao;
import com.codewiki.dao.ArticleDao;
import com.codewiki.model.Tag;
import com.codewiki.model.Article;
import com.codewiki.util.SlugGenerator;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/tags/*")
public class TagController extends HttpServlet {
    private final TagDao tagDao = new TagDao();
    private final ArticleDao articleDao = new ArticleDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo() != null ? request.getPathInfo() : "/";

        switch (action) {
            case "/":
                listTags(request, response);
                break;
            case "/view":
                viewTag(request, response);
                break;
            case "/create":
                showCreateForm(request, response);
                break;
            case "/edit":
                showEditForm(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo() != null ? request.getPathInfo() : "/";

        if (action.equals("/save")) {
            saveTag(request, response);
        } else if (action.equals("/update")) {
            updateTag(request, response);
        } else if (action.equals("/delete")) {
            deleteTag(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listTags(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Tag> tags = tagDao.getAllTags();
        request.setAttribute("tags", tags);
        request.getRequestDispatcher("/templates/tag/list.html").forward(request, response);
    }

    private void viewTag(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        Tag tag = tagDao.getTagByName(name);
        
        if (tag != null) {
            List<Article> articles = articleDao.getArticlesByTag(tag.getId());
            request.setAttribute("tag", tag);
            request.setAttribute("articles", articles);
            request.getRequestDispatcher("/templates/tag/view.html").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/templates/tag/create.html").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Tag tag = tagDao.getTagById(id);
        
        if (tag != null) {
            request.setAttribute("tag", tag);
            request.getRequestDispatcher("/templates/tag/edit.html").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void saveTag(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Tag tag = new Tag();
        tag.setName(request.getParameter("name"));
        
        tagDao.createTag(tag);
        response.sendRedirect(request.getContextPath() + "/tags/");
    }

    private void updateTag(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Tag tag = tagDao.getTagById(id);
        
        if (tag != null) {
            tag.setName(request.getParameter("name"));
            tagDao.updateTag(tag);
        }
        
        response.sendRedirect(request.getContextPath() + "/tags/");
    }

    private void deleteTag(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        tagDao.deleteTag(id);
        response.sendRedirect(request.getContextPath() + "/tags/");
    }
}