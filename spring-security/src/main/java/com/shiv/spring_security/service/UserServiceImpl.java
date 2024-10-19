package com.shiv.spring_security.service;

import com.shiv.spring_security.entity.User;
import com.shiv.spring_security.entity.VerificationToken;
import com.shiv.spring_security.model.UserModel;
import com.shiv.spring_security.repository.UserRepository;
import com.shiv.spring_security.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.shiv.spring_security.config.WebSecurityConfig;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Override
    public User registerUser(UserModel userModel){
        User user = User.builder()
                .email(userModel.getEmail())
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .password(passwordEncoder.encode(userModel.getPassword()))
                .role("User")
                .build();
        userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user){
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if(verificationToken == null){
            return "invalid";
        }
        User user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if(verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificatinToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpirationTime(calculateExpirationTime());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    private static final int EXPIRATION_TIME = 10;

    private Date calculateExpirationTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());

    }

}
