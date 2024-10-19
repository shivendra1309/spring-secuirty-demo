package com.shiv.spring_security.event.listener;

import com.shiv.spring_security.entity.User;
import com.shiv.spring_security.event.RegistrationCompleteEvent;
import com.shiv.spring_security.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //create the verification Token for the user with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);
        //send mail to the user
        String url = event.getApplicationUrl() + "/verify-registration?token=" + token ;


        //Send Verification email

        log.info("Click the link to verify your account: {}", url);

    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
