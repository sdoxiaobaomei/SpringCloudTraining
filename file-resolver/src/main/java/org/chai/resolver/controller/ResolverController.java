package org.chai.resolver.controller;

import org.chai.resolver.service.ResolverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ResolverController {

	@Autowired
	ResolverService resolverService;

	@RequestMapping("/")
	public String home() {
//		ResponseEntity<String> responseEntity = new ResponseEntity<>("home page", HttpStatus.OK);
		return "index.html";
	}

	@RequestMapping("/resolverPage")
	public String resolverPage() {
		return "resolver.html";
	}

	@RequestMapping("/resolve")
	@ResponseBody
	public String resolve(String isYesterdayParam) {
		Boolean isYesterday = Boolean.valueOf(isYesterdayParam);
		resolverService.resolveOrder(isYesterday);
		return "解析成功";
	}

	@PostMapping("/upload")
	public String upload(MultipartFile inputFile, HttpServletRequest request) {
		try {
			String originalFilename = inputFile.getOriginalFilename();
			Path path = Paths.get("C:\\file_resolver\\input\\"+originalFilename);

			if (!Files.exists(path.getParent())) {
				Files.createDirectories(path.getParent());
			}
			inputFile.transferTo(path);
		} catch (Exception e) {
			e.printStackTrace();
			return "上传失败";
		}
		return "resolver.html";
	}
}
