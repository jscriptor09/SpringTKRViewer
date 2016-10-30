package com.mjm.tkr.controller;




import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjm.tkr.domain.Image;
import com.mjm.tkr.services.ImageService;

@Controller
public class HomeController {
	
	private static final String BASE_PATH = "/images";
	private static final String FILENAME = "{filename:.+}";
	
	private final ImageService imageService;

	@Autowired
	public HomeController(ImageService imageService) {
		this.imageService = imageService;
	}
	
	@RequestMapping(value="/")
	public String index(Model model, Pageable pageable){
		final Page<Image> page = imageService.findPage(pageable);
		model.addAttribute("page", page);
		return "index";
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value = BASE_PATH + "/" + FILENAME + "/raw")
	@ResponseBody
	public ResponseEntity<?> oneRawImage(@PathVariable String filename)  {
		

		try {
			Resource file = imageService.findOneImage(filename);
			return ResponseEntity.ok()
					.contentLength(file.contentLength())
					.contentType(MediaType.IMAGE_JPEG)
					.body(new InputStreamResource(file.getInputStream()));
		} catch (IOException e) {
			return ResponseEntity.badRequest()
					.body("Could not find " + filename + " => " + e.getMessage());
		}
		
	}
	
	
	@RequestMapping(method=RequestMethod.POST, value = BASE_PATH)
	@ResponseBody
	public ResponseEntity<?> createFile(@RequestParam ("file") MultipartFile file, HttpServletRequest servletRequest) throws URISyntaxException  {
		
		try {
			imageService.createImage(file);
			final URI locationUri = new URI(servletRequest.getRequestURL().toString() + "/")
					.resolve(file.getOriginalFilename() + "/raw");
			return ResponseEntity.created(locationUri)
					.body("Successfully upload " + file.getOriginalFilename());
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
		}

	}
	
	@RequestMapping(method=RequestMethod.DELETE, value = BASE_PATH + "/" + FILENAME)
	@ResponseBody
	public ResponseEntity<?> deleteFile(@PathVariable String filename)  {
		
		try {
			imageService.deleteImage(filename);
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body("Successfully delete " + filename);
		} catch (IOException e) {
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to delete " + filename + " => " + e.getMessage());
		}
		
	}
}
