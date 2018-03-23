package edu.neu.controller;

import edu.neu.model.User;
import edu.neu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.services.sns.*;
import com.amazonaws.auth.*;
import com.amazonaws.regions.*;
import com.amazonaws.services.sns.model.*;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.*;

@Controller
public class ResetController {

    @Autowired
    private UserService userService;

    @RequestMapping(value={"/reset"}, method = RequestMethod.GET)
    public ModelAndView goToResetPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("reset");
        return modelAndView;
    }

    @RequestMapping(value={"/reset"}, method = RequestMethod.POST)
    public ModelAndView resetPassword(@ModelAttribute("user") User user, BindingResult bindingResult){
        ModelAndView modelAndView = new ModelAndView();
        
        try{
            String email = user.getEmail();
            System.out.println("email "+email);
            AmazonSNSClient snsClient = new AmazonSNSClient(new DefaultAWSCredentialsProviderChain());
            snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));

            List<Topic> topicArns = new ArrayList<>();

            ListTopicsResult result = snsClient.listTopics();
            topicArns.addAll(result.getTopics());

            for (Topic topic : topicArns) {
                System.out.println("Topic ARN "+topic.getTopicArn());
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            return modelAndView;
        }

    }

}
