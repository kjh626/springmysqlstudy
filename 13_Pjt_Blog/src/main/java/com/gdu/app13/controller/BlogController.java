package com.gdu.app13.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.app13.service.BlogService;

@RequestMapping("/blog")
@Controller
public class BlogController {

  @Autowired
  private BlogService blogService;
  
  @GetMapping("/list.do")
  public String list(HttpServletRequest request, Model model) {
    blogService.loadBlogList(request, model);
    return "blog/list";
  }
  
  @GetMapping("/write.form")
  public String write() {
    return "blog/write";
  }
  
  @PostMapping("/add.do")
  public void add(HttpServletRequest request, HttpServletResponse response) {
    blogService.addBlog(request, response);
  }
  @ResponseBody
  @PostMapping(value="imageUpload.do", produces="application/json")
  public Map<String, Object> imageUpload(MultipartHttpServletRequest multipartRequest){  // 요청에 파일첨부가 되어있을 때 MultipartHttpServletRequest 를 쓴다.
                                                                                         // MVC : <input type="file>   ,  ajax : new formData() => 받는 건 MultipartHttpServletRequest 쓴다
                                                                                         // 이거 작업하려면 FileConfig 필요하다.
    return blogService.imageUpload(multipartRequest);
  }
  
  @GetMapping("/increaseHit.do")
  public String increaseHit(@RequestParam(value="blogNo", required = false, defaultValue = "0") int blogNo) {
    // 조회수 증가 서비스(성공하면 상세보기, 실패하면 목록보기로 )
    int increaseResult = blogService.increaseHit(blogNo);
    if (increaseResult == 1) {
      return "redirect:/blog/detail.do?blogNo=" + blogNo;
    } else {
      return "redirect:/blog/list.do";
    }
  }
  
  @GetMapping("/detail.do")
  public String detail(@RequestParam(value="blogNo", required = false, defaultValue = "0") int blogNo
                     , Model model) {
    // 상세보기 서비스
    blogService.loadBlog(blogNo, model);
    return "blog/detail";
  }
  
  
}
