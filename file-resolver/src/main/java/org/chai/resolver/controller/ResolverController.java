package org.chai.resolver.controller;

import org.apache.commons.io.IOUtils;
import org.chai.resolver.entity.vo.OrderResultVO;
import org.chai.resolver.service.ResolverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ResolverController {

	@Autowired
	ResolverService resolverService;

	@RequestMapping("/")
	public String home() {
//		ResponseEntity<String> responseEntity = new ResponseEntity<>("home page", HttpStatus.OK);
		return "redirect:index.html";
	}

	@RequestMapping("/resolverPage")
	public String resolverPage() {
		return "resolver.html";
	}

	@PostMapping("/resolve")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> resolve(@RequestParam("isYesterdayParam") String isYesterdayParam) {
		System.out.println(isYesterdayParam);
		Map<String, Object> resBody = new HashMap<>();
		resBody.put("status", "success");

		Boolean isYesterday = Boolean.FALSE;
		if ("昨日".equals(isYesterdayParam)) {
			isYesterday = Boolean.TRUE;
		}
//		Boolean isYesterday = Boolean.valueOf(isYesterday);
		try {
			List<OrderResultVO> resDataList = resolverService.resolveOrder(isYesterday);
			resBody.put("tableData", resDataList);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(200).body(resBody);
	}

	@RequestMapping("/upload")
	@ResponseBody
	public ResponseEntity<String> upload(HttpServletRequest request) {
		try {
			System.out.println("上传文件");
			MultipartHttpServletRequest fileRequest = (MultipartHttpServletRequest) request;
			Map<String, MultipartFile> fileMap = fileRequest.getFileMap();
			MultipartFile inputFile = fileMap.get("file");
			String originalFilename = inputFile.getOriginalFilename();
			System.out.println("文件名："+originalFilename);
			Path path = Paths.get("C:\\file_resolver\\input\\"+originalFilename);
			if (!Files.exists(path.getParent())) {
				Files.createDirectories(path.getParent());
			}
			inputFile.transferTo(path);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return ResponseEntity.ok("success");
	}

	@GetMapping("/download")
	@ResponseBody
	public void download(@RequestParam("filename") String filename) {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletResponse response = requestAttributes.getResponse();
		// 设置信息给客户端不解析
//		String type = new MimetypesFileTypeMap().getContentType(filename);
		// 设置contenttype，即告诉客户端所发送的数据属于什么类型
		response.setHeader("Content-type","application/force-download");
		String heFileName = "";
		try {
			heFileName = new String (filename.getBytes(StandardCharsets.UTF_8),"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 设置扩展头，当Content-Type 的类型为要下载的类型时 , 这个信息头会告诉浏览器这个文件的名字和类型。
		response.setHeader("Content-Disposition", "attachment;filename=" + heFileName);
		try (InputStream stream = resolverService.getFileInputStreamByName(filename);
			 ServletOutputStream outputStream = response.getOutputStream()){
			IOUtils.copy(stream, outputStream);
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
			response.setStatus(404);
		}
//		return "resolver.html";
	}
}
