package com.gdu.app13.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdu.app13.service.CommentService;

@RequestMapping("/comment")
@Controller
public class CommentController {

  @Autowired
  private CommentService commentService;
  
  @ResponseBody
  @PostMapping(value="/addComment.do", produces="application/json")
  public Map<String, Object> addComment(HttpServletRequest request) {
    return commentService.addComment(request);
  }
  
  // MVC로 할 때는 모델로 한다.
  // ajax으로 요청했을 때는 Map으로 반환!
  @ResponseBody
  @GetMapping(value="/list.do", produces="application/json")
  public Map<String, Object> list(HttpServletRequest request) {
    return commentService.getCommentList(request);
  }
  
}
