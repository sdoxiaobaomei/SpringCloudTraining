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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
		try {
			resolverService.resolveOrder(isYesterday);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail.html";
		}
		return "success.html";
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

	@PostMapping("/download")
	public String download(String filename) {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletResponse response = requestAttributes.getResponse();
		// 设置信息给客户端不解析
		String type = new MimetypesFileTypeMap().getContentType(filename);
		// 设置contenttype，即告诉客户端所发送的数据属于什么类型
		response.setHeader("Content-type",type);
		// 设置编码
		String hehe = null;
		try {
			hehe = new String(filename.getBytes("utf-8"), "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 设置扩展头，当Content-Type 的类型为要下载的类型时 , 这个信息头会告诉浏览器这个文件的名字和类型。
		response.setHeader("Content-Disposition", "attachment;filename=" + hehe);
//		FileUtil.download(filename, response);

		return "resolver.html";
	}
}
