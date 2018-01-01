package com.carlosmecha.notebooks.controllers;

import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.pages.Comment;
import com.carlosmecha.notebooks.pages.Page;
import com.carlosmecha.notebooks.pages.PageService;
import com.carlosmecha.notebooks.users.User;
import com.carlosmecha.notebooks.utils.StringUtils;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Pages controller.
 *
 * Created by Carlos on 12/28/16.
 */
@Controller
@RequestMapping("/notebooks/{notebookCode}/pages")
public class PagesController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(PagesController.class);

    private PageService service;
    private Parser parser;
    private HtmlRenderer renderer;

    @Autowired
    public PagesController(DataSource source, Parser parser, HtmlRenderer renderer) {
        super(source);
        this.service = new PageService();
        this.parser = parser;
        this.renderer = renderer;
    }

    /**
     * Shows a form to create new ones.
     * @return Template name.
     */
    @GetMapping
    public ModelAndView index(HttpServletRequest request, Principal principal) throws SQLException {
        try (Connection conn = getConnection()) {
            User user = fromPrincipal(conn, principal);
            Notebook notebook = getNotebook(conn, request);

            ModelAndView model = new ModelAndView("pages");
            model.addObject("name", user.getName());
            model.addObject("notebook", notebook);
            model.addObject("page", new PageForm());
            return model;
        }
    }

    /**
     * Creates the new page and redirects to the main page.
     * @param page Page.
     * @param result Result of binding.
     * @param attributes Redirect attributes.
     * @return Redirection.
     */
    @PostMapping
    public ModelAndView create(@ModelAttribute PageForm page,
                         BindingResult result,
                         RedirectAttributes attributes,
                         HttpServletRequest request, 
                         Principal principal) throws SQLException {

        try (Connection conn = getConnection()) {
            User user = fromPrincipal(conn, principal);
            Notebook notebook = getNotebook(conn, request);

            logger.debug("User {} is trying to create page for notebook {}", principal.getName(), notebook.getCode());

            if(result.hasErrors()) {
                attributes.addFlashAttribute("error", "Missing information!");
                return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/pages"));
            }

            try {
                conn.setAutoCommit(false);
                service.create(conn, notebook,
                    StringUtils.unsafeToDate(page.date, "yyyy-MM-dd", logger),
                    StringUtils.split(page.tagCodes, ","),
                    page.text, user);

                attributes.addFlashAttribute("message", "Page created");
                conn.commit();
                return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/pages"));
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

    }

    /**
     * Gets the page.
     * @param pageId Page id
     * @return The page requested.
     */
    @GetMapping("/{id}")
    public ModelAndView getPage(@PathVariable("id") String pageId,
                            HttpServletRequest request, 
                            Principal principal) throws SQLException {

        try (Connection conn = getConnection()) {
            User user = fromPrincipal(conn, principal);
            Notebook notebook = getNotebook(conn, request);
            
            logger.debug("User {} is trying to access to notebook {}", user.getLoginName(), notebook.getCode());

            ModelAndView model = new ModelAndView("page");
            model.addObject("name", user.getName());
            model.addObject("notebook", notebook);
            
            int id;
            try {
                id = Integer.parseInt(pageId);
            } catch (NumberFormatException e) {
                String lower = pageId.toLowerCase();

                if("last".equals(lower) || "first".equals(lower)) {
                    boolean first = "first".equals(lower);

                    List<Integer> ids = service.getAllIds(conn, notebook);
                    if(ids.isEmpty()) {
                        return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/pages"));
                    }

                    int index = first ? 0 : ids.size() - 1;

                    model.addObject("page", service.get(conn, ids.get(index)).get());
                    model.addObject("comments", renderComments(service.getSortedComments(conn, ids.get(index))));
                    if(ids.size() > 1) {
                        if(first) {
                            model.addObject("next", ids.get(1));
                        } else {
                            model.addObject("prev", ids.get(index - 1));
                        }
                    }

                    return model;

                } else {
                    return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/pages"));
                }
            }

            Optional<Page> page = service.get(conn, id);
            if(!page.isPresent() || !page.get().getNotebookCode().equals(notebook.getCode())) {
                logger.warn("Requesting an unknown page {} from the notebook {}", pageId, notebook.getCode());
                return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/pages"));
            }

            model.addObject("page", page.get());
            model.addObject("comments", renderComments(service.getSortedComments(conn, id)));
            List<Integer> ids = service.getAllIds(conn, notebook);
            int prev = -1;
            int i = 0;
            while(i < ids.size()) {
                int pId = ids.get(i);
                if(pId == id) {
                    if(prev != -1) {
                        model.addObject("prev", prev);
                    }
                    if(i < ids.size() - 1) {
                        model.addObject("next", ids.get(i+1));
                    }
                    break;
                }
                prev = pId;
                i++;
            }

            return model;
        }

    }

    private List<RenderedComment> renderComments(List<Comment> comments) {
        return comments.stream().map(c ->
                new RenderedComment(c.getWroteBy(), c.getWroteOn(),
                        renderer.render(parser.parse(c.getContent()))))
                .collect(Collectors.toList());
    }

    /**
     * Category render model
     */
    public static class RenderedComment {

        private String wroteBy;
        private Date wroteOn;
        private String content;

        public RenderedComment(String wroteBy, Date wroteOn, String content) {
            this.wroteBy = wroteBy;
            this.wroteOn = wroteOn;
            this.content = content;
        }

        public String getWroteBy() {
            return wroteBy;
        }

        public Date getWroteOn() {
            return wroteOn;
        }

        public String getContent() {
            return content;
        }
    }

    /**
     * Expense form model.
     */
    public static class PageForm {

        @NotNull
        private float value;
        @NotNull
        private String date;
        private String tagCodes;
        @NotEmpty
        private String text;

        public PageForm() {
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTagCodes() {
            return tagCodes;
        }

        public void setTagCodes(String tagCodes) {
            this.tagCodes = tagCodes;
        }

        @Override
        public String toString() {
            return "PageForm{" +
                    "value=" + value +
                    ", date=" + date +
                    ", tagCodes=" + tagCodes +
                    ", text='" + text + '\'' +
                    '}';
        }
    }

}
