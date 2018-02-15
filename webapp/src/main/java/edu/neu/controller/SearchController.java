package edu.neu.controller;


import edu.neu.model.User;
import edu.neu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class SearchController {

    @Autowired
    private UserService userService;

    @RequestMapping(value={"/search"}, method = RequestMethod.GET)
    public ModelAndView goToSearchPage(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("search");
        return modelAndView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView searchUser(@ModelAttribute("user") User user, BindingResult bindingResult) {
        System.out.println("1" + user.getEmail());
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists == null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "No user with this email");
            System.out.println("3");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("search");
            System.out.println("4");
        } else {
            modelAndView.addObject("user", userExists);
            //modelAndView.addObject("aboutMe", userExists.getAboutMe());
            System.out.println("2" + userExists.getAboutMe());
            modelAndView.setViewName("search");

        }
        return modelAndView;
    }

}
