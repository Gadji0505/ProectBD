package com.codewiki.controller;

import com.codewiki.dao.CategoryDao;
import com.codewiki.dao.ArticleDao;
import com.codewiki.model.Category;
import com.codewiki.model.Article;
import com.codewiki.util.SlugGenerator;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/categories/*")
public class CategoryController extends HttpServlet {
    private final CategoryDao categoryDao = new CategoryDao();
    private final ArticleDao articleDao = new ArticleDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo() != null ? request.getPathInfo() : "/";

        switch (action) {
            case "/":
                listCategories(request, response);
                break;
            case "/view":
                viewCategory(request, response);
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
            saveCategory(request, response);
        } else if (action.equals("/update")) {
            updateCategory(request, response);
        } else if (action.equals("/delete")) {
            deleteCategory(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listCategories(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Category> categories = categoryDao.getAllCategories();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/templates/category/list.html").forward(request, response);
    }

    private void viewCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String slug = request.getParameter("slug");
        Category category = categoryDao.getCategoryBySlug(slug);
        
        if (category != null) {
            List<Article> articles = articleDao.getArticlesByCategory(category.getId());
            request.setAttribute("category", category);
            request.setAttribute("articles", articles);
            request.getRequestDispatcher("/templates/category/view.html").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/templates/category/create.html").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Category category = categoryDao.getCategoryById(id);
        
        if (category != null) {
            request.setAttribute("category", category);
            request.getRequestDispatcher("/templates/category/edit.html").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void saveCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Category category = new Category();
        category.setName(request.getParameter("name"));
        category.setSlug(SlugGenerator.generateSlug(category.getName()));
        category.setDescription(request.getParameter("description"));

        categoryDao.createCategory(category);
        response.sendRedirect(request.getContextPath() + "/categories/");
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Category category = categoryDao.getCategoryById(id);
        
        if (category != null) {
            category.setName(request.getParameter("name"));
            category.setSlug(SlugGenerator.generateSlug(category.getName()));
            category.setDescription(request.getParameter("description"));
            
            categoryDao.updateCategory(category);
        }
        
        response.sendRedirect(request.getContextPath() + "/categories/");
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        categoryDao.deleteCategory(id);
        response.sendRedirect(request.getContextPath() + "/categories/");
    }
}