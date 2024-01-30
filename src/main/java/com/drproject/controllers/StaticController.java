package com.drproject.controllers;

import com.drproject.service.ClassroomService;
import com.drproject.service.ARservice;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Controller
public class StaticController {
    private final ARservice ARserivce;
    private final ClassroomService classroomService;
    private final ResourceLoader resourceLoader;


    public StaticController(ARservice ARserivce, ClassroomService classroomService, ResourceLoader resourceLoader) {   /////user service class needed
        this.ARserivce = ARserivce;
        this.classroomService = classroomService;
        this.resourceLoader = resourceLoader;
    }


    @GetMapping(value = {"/", ""})
    public String home() {
        return "html/homepage.html";
    }

    @GetMapping("/signup")
    public String signup() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute("username") != null) {
            return "redirect:/panel";
        }
        return "html/login_signUp.html";
    }

    @GetMapping("/FAQ")
    public String faq() {
        return "html/faq.html";
    }
    @GetMapping("/aboutUs")
    public String aboutUs() {
        return "html/aboutUs.html";
    }
    @GetMapping("/contact")
    public String contact() {
        return "html/askQ.html";
    }



    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidate the session
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/classrooms/{classroomId}")
    public ResponseEntity<String> getClassroom(@PathVariable String classroomId, HttpSession session) throws IOException {
        if (session.getAttribute("username") == null){
            return ResponseEntity.status(302).header("Location", "/login?msg=Sign in first!").build();
        }
        else {
            //gerUserRole method that takes username and classroomID and returns the role of the user in string format(teacher or student)
            Map<String, Object> res = classroomService.getUserRole((String) session.getAttribute("username"), classroomId);
            if (res.get("status").equals(true)){
                if (res.get("obj").equals("teacher") || res.get("obj").equals("admin"))
                    return getHtmlFile("/static/html/teacherClassroom.html");
                else if (res.get("obj").equals("student")){
                    return getHtmlFile("static/html/StudentClassroom.html");
                }
            }
        }
        return ResponseEntity.status(302).header("Location", "/login?msg=403").build();
    }
    @GetMapping("/classrooms/{classroomId}/grades")
    public ResponseEntity<String> gradesClassroom(@PathVariable String classroomId, HttpSession session) throws IOException {
        if (session.getAttribute("username") == null){
            return ResponseEntity.status(302).header("Location", "/login?msg=Sign in first!").build();
        }
        else {
            HashMap<String, Object> res = classroomService.getUserRole((String) session.getAttribute("username"), classroomId);
            if (res.get("status").equals(false)){
                return ResponseEntity.status(302).header("Location", "/login?msg=You dont have access to this classroom!").build();
            }else {
                if (res.get("obj").equals("student")){
                    return getHtmlFile("static/html/studentGrade.html");
                }
                else {
                    return getHtmlFile("static/html/teacherGrades.html");
                }
            }
        }
    }
    @GetMapping("/classrooms/{classroomId}/glossary")
    public ResponseEntity<String> glossaryClassroom(@PathVariable String classroomId, HttpSession session) throws IOException {
        if (session.getAttribute("username") == null){
            return ResponseEntity.status(302).header("Location", "/login?msg=Sign in first!").build();
        }
        else {
            HashMap<String, Object> res = classroomService.getUserRole((String) session.getAttribute("username"), classroomId);
            if (res.get("status").equals(false)){
                return ResponseEntity.status(302).header("Location", "/login?msg=You dont have access to this classroom!").build();
            }else {
                if (res.get("obj").equals("student")){
                    return getHtmlFile("static/html/glossary.html");
                }
                else {
                    return getHtmlFile("static/html/teacherGlossary.html");
                }
            }
        }
    }

    @GetMapping("/classrooms/{classroomId}/sections/{sectionId}/addQuiz")
    public ResponseEntity<String> addQuizzzzzzz(@PathVariable String classroomId, HttpSession session, @PathVariable String sectionId) throws IOException {
        if (session.getAttribute("username") == null){
            return ResponseEntity.status(302).header("Location", "/login?msg=Sign in first!").build();
        }
        else {
            HashMap<String, Object> res = classroomService.getUserRole((String) session.getAttribute("username"), classroomId);
            if (res.get("obj").equals("teacher") || res.get("obj").equals("admin")){
                return getHtmlFile("static/html/addQuiz.html");
            }else {
                return ResponseEntity.status(302).header("Location", "/login?msg=You dont have access to this classroom!").build();
            }
        }
    }


    @GetMapping("/{classroomId}/sections/{sectionId}/addAssignment")
    public ResponseEntity<String> addAssignment(@PathVariable String classroomId, HttpSession session) throws IOException {
        if (session.getAttribute("username") == null){
            return ResponseEntity.status(302).header("Location", "/login?msg=Sign in first!").build();
        }
        else {
            HashMap<String, Object> res = classroomService.getUserRole((String) session.getAttribute("username"), classroomId);
            if (res.get("obj").equals("teacher") || res.get("obj").equals("admin")){
                return getHtmlFile("static/html/addAssignment.html");
            }else {
                return ResponseEntity.status(302).header("Location", "/login?msg=You dont have access to this classroom!").build();
            }
        }
    }

    //returns quiz or poll pr assignment templates and has AR= param
//    @GetMapping("/{classroomId}/sections/{sectionId}")
//    public ResponseEntity<?> getAR(@RequestParam("AR") String ARid, @PathVariable String classroomId, @PathVariable String sectionId, HttpSession session) throws IOException {
//        if (session.getAttribute("username") == null){
//            return ResponseEntity.status(302).header("Location", "/login?msg=Sign in first!").build();
//        }
//        else if (classroomService.isInClass((String) session.getAttribute("username"), classroomId).get("status").equals(false)){
//            return ResponseEntity.status(302).header("Location", "/login?msg=403!").build();
//        }
//        else {
//            Map<String,Object> res = ARservice.getARtype(ARid); /////////////////getARtype that takes classroomId and sectionId and ARid and returns the type of the AR (quiz for example)
//            if (res.get("obj") == "quiz"){
//                return getHtmlFile("static/html/quiz.html");
//            }
//            else if (res.get("obj") == "assignment"){    ////getType method needed for ARs
//                return getHtmlFile("static/html/assignment.html");
//            }
//            else if (res.get("obj") == "poll"){    ////getType method needed for ARs
//                return getHtmlFile("static/html/poll.html");
//            }
//            else {
//                return ResponseEntity.status(302).header("Location", "/panel?msg=403!").build();
//            }
//        }
//    }

    @GetMapping("/panel")
    public String panel(HttpSession session) {
        if (session.getAttribute("username") != null){
            return "html/memberPanel.html";                                     //////panel.html missing
        }
        return "redirect:/login?msg=Sign in first!";
    }

    @GetMapping("/panel/notifications")
    public ResponseEntity<String> getNotifs(HttpSession session) throws IOException {
        if (session.getAttribute("username") == null){
            return ResponseEntity.status(302).header("Location", "/login?msg=Sign in first!").build();
        }
        return getHtmlFile("static/html/notification.html");
    }


    @GetMapping("/panel/profile")
    public ResponseEntity<String> getHtmlFile(HttpSession session) throws IOException {
        if (session.getAttribute("username") == null){
           return ResponseEntity.status(302).header("Location", "/login?msg=Sign in first!").build();
       }
        return getHtmlFile("static/html/profile.html");
    }

    private ResponseEntity<String> getHtmlFile(String filePath) throws IOException {
        // Load the HTML file as a resource
        Resource resource = new ClassPathResource(filePath);
        // Check if the resource exists
        if (resource.exists()) {
            // Read the contents of the HTML file
            byte[] htmlBytes = Files.readAllBytes(Path.of(resource.getURI()));
            String htmlContent = new String(htmlBytes);

            // Set the Content-Type header to text/html
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlContent);
        } else {
            // Return a 404 Not Found response if the file does not exist
            return ResponseEntity.notFound().build();
        }
    }

}

