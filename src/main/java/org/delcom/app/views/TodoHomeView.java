package org.delcom.app.views;

import java.util.List;
import org.delcom.app.dto.TodoForm;
import org.delcom.app.entities.Todo;
import org.delcom.app.entities.User;
import org.delcom.app.services.TodoService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/todos")
public class TodoHomeView {

    private final TodoService todoService;

    public TodoHomeView(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public String index(Model model, @RequestParam(required = false) String search) {
        // Autentikasi
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/login";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/login";
        }
        User authUser = (User) principal;
        model.addAttribute("auth", authUser);

        // Data todos
        List<Todo> todos = todoService.getAllTodos(authUser.getId(), search != null ? search : "");
        model.addAttribute("todos", todos);
        model.addAttribute("todoForm", new TodoForm());

        return ConstUtil.TEMPLATE_PAGES_TODOS_HOME;
    }
}