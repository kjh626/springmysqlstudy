package com.gdu.app11.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.app11.service.UploadService;

@RequestMapping("/upload")
@Controller
public class UploadController {
	
	// field
	@Autowired
	private UploadService uploadService;

	@GetMapping("/list.do")
	public String list(HttpServletRequest request, Model model) {
		uploadService.getUploadList(request, model);
		return "upload/list";
	}
	
	@GetMapping("/write.do")
	public String write() {
		return "upload/write";
	}
	
	@PostMapping("/add.do")
	public String add(MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttributes) { // MultipartHttpServletRequest: 파일업로드할 때 쓰임(jsp에서 <input type="file">인 놈을 처리할 수 있는 request)
		int uploadResult = uploadService.addUpload(multipartRequest);
		redirectAttributes.addFlashAttribute("uploadResult", uploadResult);
		return "redirect:/upload/list.do";
	}
	
	@GetMapping("/detail.do")
	public String detail(@RequestParam(value="uploadNo", required=false, defaultValue="0") int uploadNo
					   , Model model) {
		uploadService.getUploadByNo(uploadNo, model);
		return "upload/detail";
	}
	
	@GetMapping("/display.do")
	public ResponseEntity<byte[]> display(@RequestParam("attachNo") int attachNo) {
		return uploadService.display(attachNo);
	}
	
	// 다운로드할 때 보통 화면 안 바뀜! 그래서 Ajax로 처리해야 하는데 반환타입은 ResponseEntity, Map으로 써보고 있음. 파일을 전달받을 때 byte[]로 받지 않고 Resource로 받는다
	// 파라미터로 attachNo뿐만 아니라 요청헤더값도 넘겨준다.(F12-Network-Request Headers)
	// User-Agent: 어떤 브라우저를 사용해서 접속했구나를 알 수 있음(MS - IE - Edge 이 둘의 처리가 일부 달라서 if로 처리해주는 코드 넣어줄 것이다.)
	// 이렇게 2개로 나눠서 매개변수 받지 말고 HttpServletRequest로 한번에 받을 수 있다. -> 좋은 메소드는 인수를 적게 쓰는 메소드(0~3)
	@GetMapping("/download.do")
	public ResponseEntity<Resource> download(@RequestParam("attachNo") int attachNo, @RequestHeader("User-Agent") String userAgent) {
		return uploadService.download(attachNo, userAgent);
	}
	
	@GetMapping("/downloadAll.do")
	public ResponseEntity<Resource> downloadAll(@RequestParam("uploadNo") int uploadNo) {
		return uploadService.downloadAll(uploadNo);
	}
	
	@PostMapping("/removeUpload.do")
	public String removeUpload(@RequestParam("uploadNo") int uploadNo, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("removeResult", uploadService.removeUpload(uploadNo));
		return "redirect:/upload/list.do";
	}
	
	@PostMapping("/editUpload.do")
	public String editUpload(@RequestParam("uploadNo") int uploadNo, Model model) {
		uploadService.getUploadByNo(uploadNo, model);
		return "upload/edit";
	}
	
	@PostMapping("/modify.do")
	public String modify(MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttributes) {
		int modifyResult = uploadService.modifyUpload(multipartRequest);
		redirectAttributes.addFlashAttribute("modifyResult", modifyResult);
		return "redirect:/upload/detail.do?uploadNo=" + multipartRequest.getParameter("uploadNo");
	}
	
	@GetMapping("/removeAttach.do")
	public String removeAttach(@RequestParam("uploadNo") int uploadNo, @RequestParam("attachNo") int attachNo) {
		uploadService.removeAttach(attachNo);
		return "redirect:/upload/detail.do?uploadNo=" + uploadNo;
	}
}
