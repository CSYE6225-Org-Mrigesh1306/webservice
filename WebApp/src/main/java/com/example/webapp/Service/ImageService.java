package com.example.webapp.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.webapp.Controller.UserController;
import com.example.webapp.DAO.ImageRepository;
import com.example.webapp.DAO.UserRepository;
import com.example.webapp.Model.Image;
import com.example.webapp.Model.User;

@Service
public class ImageService {

	@Value("${aws.s3.bucket}")
	private String bucketName;

	@Autowired
	private AmazonS3 amazonS3Client;
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Autowired
	private UserRepository userrepo;

	private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

	public List<Image> getUserImage(List<User> user) {
		
		List<Image> allImages = imageRepository.findAll();
		
		List<Image> userImage = new ArrayList<>();
		
		for(Image i : allImages ) {
			
			if(i.getUser_id() == user.get(0).getId() ) {
				
				userImage.add(i);
				
			}
		}

		return userImage;

		
	}

	public Image saveImage(MultipartFile imagefile, List<User> user) {
		
		List<Image> allImages = imageRepository.findAll();
		
		boolean isUserExists=false;
		
		for(Image i : allImages ) {
			
			if(i.getUser_id() == user.get(0).getId() ) {
				
				isUserExists=true;
				
			}
		}
		
		if(isUserExists) {
			
			for(Image i : allImages ) {
				
				if(i.getUser_id() == user.get(0).getId() ) {
					
					imageRepository.delete(i);
					
					deleteFileFromS3Bucket(i.getUrl(),user.get(0).getId());
					
				}
			}
			
		}

		String bucket_name = uploadeToS3(user.get(0).getId() + "/" + imagefile.getOriginalFilename(), imagefile);
		String url = bucket_name+"/"+ user.get(0).getId()+"/"+imagefile.getOriginalFilename();
		Random rand = new Random();
		Image img = new Image();
		img.setFilename(imagefile.getOriginalFilename());
		img.setUrl(url);
		img.setUpload_date(java.time.Clock.systemUTC().instant().toString());
		img.setUser_id(user.get(0).getId());
		img.setId(rand.nextLong());
		return imageRepository.save(img);

	}

	private String uploadeToS3(String key, MultipartFile imagefile) {

		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(imagefile.getSize());
			amazonS3Client.putObject(bucketName, key, imagefile.getInputStream(), metadata);

			return bucketName;
		} catch (IOException ioe) {
			logger.error("IOException: " + ioe.getMessage());
		} catch (AmazonServiceException serviceException) {
			logger.info("AmazonServiceException: " + serviceException.getMessage());
			throw serviceException;
		} catch (AmazonClientException clientException) {
			logger.info("AmazonClientException Message: " + clientException.getMessage());
			throw clientException;
		}
		// statsd.recordExecutionTime("S3 Response Time - Upload pic File",
		// System.currentTimeMillis() - startTime);
		return "File not uploaded: " + key;
	}

	public void deleteImage(List<User> user) {
		
		List<Image> allImages = imageRepository.findAll();
		
		String result;
		
		for(Image i : allImages ) {
			
			if(i.getUser_id() == user.get(0).getId() ) {
				
				imageRepository.delete(i);
				
				result = deleteFileFromS3Bucket(i.getUrl(),user.get(0).getId());
				
			}
		}
		 
	}

	private String deleteFileFromS3Bucket(String url, long id) {
		
		 String fileName = url.substring(url.lastIndexOf("/") + 1);
		 
	     System.out.println("fileName "+bucketName + "/"+fileName);
	     
	     amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, id+"/"+fileName));
	     
	     return "Successfully deleted";
	}

}
