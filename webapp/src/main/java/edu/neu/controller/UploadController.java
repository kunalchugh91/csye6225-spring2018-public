package edu.neu.controller;

import edu.neu.model.User;
import edu.neu.service.S3Services;
import edu.neu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Controller
public class UploadController {



    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    //Save the uploaded file to this folder
    private String UPLOADED_FOLDER;

    private String PROFILE_NAME;

    @Autowired
    S3Services s3Services;

    @Value("${jsa.aws.access_key_id}")
    private String awsId;

    @Value("${jsa.aws.secret_access_key}")
    private String awsKey;

    @Value("${jsa.s3.region}")
    private String region;

    /*
    @GetMapping("/")
    public String index() {
        return "upload";
    }
    */

    @PostMapping("upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        UPLOADED_FOLDER = environment.getProperty("app.profile.path");

        PROFILE_NAME = environment.getProperty("app.profile.name");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());


        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:home";
        }

        String originalFileName = file.getOriginalFilename();
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            String ext = originalFileName.substring(i + 1);
            if (!(ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg"))){
                redirectAttributes.addFlashAttribute("message", "Please select a JPEG, PNG or JPG file to upload");
                redirectAttributes.addFlashAttribute("aboutme", user.getAboutMe());
                return "redirect:home";
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Please select a JPEG, PNG or JPG file to upload");
            redirectAttributes.addFlashAttribute("aboutme", user.getAboutMe());
            return "redirect:home";
        }

        try {
                File directory;

                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();

                //Path path = Paths.get(UPLOADED_FOLDER + user.getId() +'/'+ file.getOriginalFilename());
                File f = new File(UPLOADED_FOLDER + user.getId());

                if (f.exists()) {
                    for (String s : f.list()) {
                        File fi = new File(f.getPath(), s);
                        if (fi.exists() && fi.isFile()) fi.delete();
                    }
                    if (!f.delete()) {
                        redirectAttributes.addFlashAttribute("message", "Could not delete existing profile picture");
                        redirectAttributes.addFlashAttribute("aboutme", user.getAboutMe());
                        return "redirect:home";
                    }
                }



                    f.mkdir();
                    f.setReadable(true, false);
                    f.setWritable(true, false);
                    File imageFile = new File(f.getPath(), originalFileName);
                    file.transferTo(imageFile);
                    imageFile.setReadable(true, false);
                    imageFile.setWritable(true, false);

            // Upload to s3
            if(PROFILE_NAME.equals("aws")){
                String keyName = "profiles/"+user.getId() +"/"+originalFileName;
                s3Services.uploadFile(keyName, f.getPath()+'/'+originalFileName);
            }

            user.setPath(imageFile.getPath());
                userService.updateUser(user);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + originalFileName + "'");

        } catch (IOException e) {
            user.setPath("/profiles/default/defaultpic.jpeg");
            userService.updateUser(user);
            e.printStackTrace();
        }

        redirectAttributes.addFlashAttribute("aboutme", user.getAboutMe());

        return "redirect:/home";
    }

    @PostMapping("delete") // //new annotation since 4.3
    public String singleFileDelete(//@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());


        try {

            //Path path = Paths.get(UPLOADED_FOLDER + user.getId() +'/'+ file.getOriginalFilename());
            File f = new File(UPLOADED_FOLDER + user.getId());
            if (f.exists()){
                for(String s : f.list()){
                    File fi = new File(f.getPath(), s);
                    if (fi.exists() && fi.isFile()) fi.delete();
                }
                f.delete();
            }

            user.setPath("/profiles/default/defaultpic.jpeg");
            userService.updateUser(user);


            redirectAttributes.addFlashAttribute("message",
                    "You successfully deleted profile picture");

        } catch (Exception e) {
            e.printStackTrace();
        }

        redirectAttributes.addFlashAttribute("aboutme", user.getAboutMe());


        return "redirect:/home";
    }

    /*
    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }
    */
    @PostMapping("aboutme") // //new annotation since 4.3
    public String updateAboutMe(@RequestParam("aboutme") String aboutme,
                                   RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());


        try {

            user.setAboutMe(aboutme);
            userService.updateUser(user);

            redirectAttributes.addFlashAttribute("aboutme",
                    user.getAboutMe());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/home";
    }

}