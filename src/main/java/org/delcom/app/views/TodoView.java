package org.delcom.app.views;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.delcom.app.dto.CoverTodoForm;
import org.delcom.app.dto.TodoForm;
import org.delcom.app.entities.Todo;
import org.delcom.app.entities.User;
import org.delcom.app.services.FileStorageService;
import org.delcom.app.services.TodoService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/todos")
public class TodoView {

    private final TodoService todoService;
    private final FileStorageService fileStorageService;

    public TodoView(TodoService todoService, FileStorageService fileStorageService) {
        this.todoService = todoService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/add")
    public String postAddTodo(@Valid @ModelAttribute("todoForm") TodoForm todoForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        if (todoForm.getTitle() == null || todoForm.getTitle().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Judul tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addTodoModalOpen", true);
            return "redirect:/todos";
        }

        if (todoForm.getDescription() == null || todoForm.getDescription().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Deskripsi tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addTodoModalOpen", true);
            return "redirect:/todos";
        }

        var entity = todoService.createTodo(
                authUser.getId(),
                todoForm.getTitle(),
                todoForm.getDescription());

        if (entity == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan todo");
            redirectAttributes.addFlashAttribute("addTodoModalOpen", true);
            return "redirect:/todos";
        }

        redirectAttributes.addFlashAttribute("success", "Todo berhasil ditambahkan.");
        return "redirect:/todos";
    }

    @PostMapping("/edit")
    public String postEditTodo(@Valid @ModelAttribute("todoForm") TodoForm todoForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;

        if (todoForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID todo tidak valid");
            redirectAttributes.addFlashAttribute("editTodoModalOpen", true);
            return "redirect:/todos";
        }

        if (todoForm.getTitle() == null || todoForm.getTitle().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Judul tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editTodoModalOpen", true);
            redirectAttributes.addFlashAttribute("editTodoModalId", todoForm.getId());
            return "redirect:/todos";
        }

        if (todoForm.getDescription() == null || todoForm.getDescription().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Deskripsi tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editTodoModalOpen", true);
            redirectAttributes.addFlashAttribute("editTodoModalId", todoForm.getId());
            return "redirect:/todos";
        }

        var updated = todoService.updateTodo(
                authUser.getId(),
                todoForm.getId(),
                todoForm.getTitle(),
                todoForm.getDescription(),
                todoForm.getIsFinished());
        if (updated == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui todo");
            redirectAttributes.addFlashAttribute("editTodoModalOpen", true);
            redirectAttributes.addFlashAttribute("editTodoModalId", todoForm.getId());
            return "redirect:/todos";
        }

        redirectAttributes.addFlashAttribute("success", "Todo berhasil diperbarui.");
        return "redirect:/todos";
    }

    @PostMapping("/delete")
    public String postDeleteTodo(@Valid @ModelAttribute("todoForm") TodoForm todoForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;

        if (todoForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID todo tidak valid");
            redirectAttributes.addFlashAttribute("deleteTodoModalOpen", true);
            return "redirect:/todos";
        }

        if (todoForm.getConfirmTitle() == null || todoForm.getConfirmTitle().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Konfirmasi judul tidak boleh kosong");
            redirectAttributes.addFlashAttribute("deleteTodoModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteTodoModalId", todoForm.getId());
            return "redirect:/todos";
        }

        Todo existingTodo = todoService.getTodoById(authUser.getId(), todoForm.getId());
        if (existingTodo == null) {
            redirectAttributes.addFlashAttribute("error", "Todo tidak ditemukan");
            redirectAttributes.addFlashAttribute("deleteTodoModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteTodoModalId", todoForm.getId());
            return "redirect:/todos";
        }

        if (!existingTodo.getTitle().equals(todoForm.getConfirmTitle())) {
            redirectAttributes.addFlashAttribute("error", "Konfirmasi judul tidak sesuai");
            redirectAttributes.addFlashAttribute("deleteTodoModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteTodoModalId", todoForm.getId());
            return "redirect:/todos";
        }

        boolean deleted = todoService.deleteTodo(
                authUser.getId(),
                todoForm.getId());
        if (!deleted) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus todo");
            redirectAttributes.addFlashAttribute("deleteTodoModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteTodoModalId", todoForm.getId());
            return "redirect:/todos";
        }

        redirectAttributes.addFlashAttribute("success", "Todo berhasil dihapus.");
        return "redirect:/todos";
    }

    @GetMapping("/{todoId}")
    public String getDetailTodo(@PathVariable UUID todoId, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;
        model.addAttribute("auth", authUser);

        Todo todo = todoService.getTodoById(authUser.getId(), todoId);
        if (todo == null) {
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);

        CoverTodoForm coverTodoForm = new CoverTodoForm();
        coverTodoForm.setId(todoId);
        model.addAttribute("coverTodoForm", coverTodoForm);

        return ConstUtil.TEMPLATE_PAGES_TODOS_DETAIL;
    }

    @PostMapping("/edit-cover")
    public String postEditCoverTodo(@Valid @ModelAttribute("coverTodoForm") CoverTodoForm coverTodoForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        if (coverTodoForm.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File cover tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editCoverTodoModalOpen", true);
            return "redirect:/todos/" + coverTodoForm.getId();
        }

        Todo todo = todoService.getTodoById(authUser.getId(), coverTodoForm.getId());
        if (todo == null) {
            redirectAttributes.addFlashAttribute("error", "Todo tidak ditemukan");
            redirectAttributes.addFlashAttribute("editCoverTodoModalOpen", true);
            return "redirect:/todos";
        }

        if (!coverTodoForm.isValidImage()) {
            redirectAttributes.addFlashAttribute("error", "Format file tidak didukung. Gunakan JPG, PNG, atau GIF");
            redirectAttributes.addFlashAttribute("editCoverTodoModalOpen", true);
            return "redirect:/todos/" + coverTodoForm.getId();
        }

        if (!coverTodoForm.isSizeValid(5 * 1024 * 1024)) {
            redirectAttributes.addFlashAttribute("error", "Ukuran file terlalu besar. Maksimal 5MB");
            redirectAttributes.addFlashAttribute("editCoverTodoModalOpen", true);
            return "redirect:/todos/" + coverTodoForm.getId();
        }

        try {
            String fileName = fileStorageService.storeFile(coverTodoForm.getCoverFile(), coverTodoForm.getId());

            todoService.updateCover(coverTodoForm.getId(), fileName);

            redirectAttributes.addFlashAttribute("success", "Cover berhasil diupload");
            return "redirect:/todos/" + coverTodoForm.getId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengupload cover");
            redirectAttributes.addFlashAttribute("editCoverTodoModalOpen", true);
            return "redirect:/todos/" + coverTodoForm.getId();
        }
    }

    @GetMapping("/cover/{filename:.+}")
    @ResponseBody
    public Resource getCoverByFilename(@PathVariable String filename) {
        try {
            Path file = fileStorageService.loadFile(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

}
