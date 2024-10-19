package com.shiv.spring_security.controller;

import com.shiv.spring_security.entity.User;
import com.shiv.spring_security.entity.VerificationToken;
import com.shiv.spring_security.event.RegistrationCompleteEvent;
import com.shiv.spring_security.model.UserModel;
import com.shiv.spring_security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "success";
    }

    @GetMapping("/hello")
    public String helloAPi(){
        return "hello!";
    }

    @GetMapping("/verify-registration")
    public String verifyRegistration(@RequestParam("token") String token){
        String result = userService.validateVerificationToken(token);
        if(result.equalsIgnoreCase("valid")){
            return "validation successful!";
        }else{
            return result;
        }

    }

    @GetMapping("/resend-verify-token")
    public String resendVerificationToken(@RequestParam("token") String oldToken, final HttpServletRequest request){
        log.info("request recieved");
        VerificationToken verificationToken = userService.generateNewVerificatinToken(oldToken);
        User user = verificationToken.getUser();
       resendVerificationTokenMail(user, applicationUrl(request), verificationToken.getToken());
        //publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Verification link sent!";

    }

    private void resendVerificationTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl + "/verify-registration?token=" + token ;
        //send mail
        log.info("resend url : {}", url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
