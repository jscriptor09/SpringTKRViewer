package com.mjm.tkr.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mjm.tkr.Repository.ImageRepository;
import com.mjm.tkr.domain.Image;


@Service
public class ImageService {

	private ImageRepository repository;
	private ResourceLoader resourceLoader;
	
	private static String UPLOAD_ROOT = "upload-dir";

	@Autowired
	public ImageService(ImageRepository repository, ResourceLoader resourceLoader) {
		this.repository = repository;
		this.resourceLoader = resourceLoader;
	}
	
	public Page<Image> findPage(Pageable pageable){
		return repository.findAll(pageable);
	}

	public Resource findOneImage(String filename){
		return resourceLoader.getResource("file:" + UPLOAD_ROOT+ "/" + filename);
	}
	
	public void createImage(MultipartFile file) throws IOException{
		if(!file.isEmpty()){
			Files.copy(file.getInputStream(),  Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
			repository.save(new Image(file.getOriginalFilename()));
		}
	}
	
	public void deleteImage(String filename) throws IOException{
		final Image byName = repository.findByName(filename);
		repository.delete(byName);
		Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
	}
	
	@Bean
	//@Profile("dev")
	CommandLineRunner setUp(ImageRepository repository) throws IOException {
		
		return(args) -> {
			
			FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
			Files.createDirectory(Paths.get(UPLOAD_ROOT));
			
			FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test"));
			repository.save(new Image("test"));
			
			FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test"));
			repository.save(new Image("test2"));
			
			FileCopyUtils.copy("Test file3", new FileWriter(UPLOAD_ROOT + "/test"));
			repository.save(new Image("test3"));
			
		};
	}
}
