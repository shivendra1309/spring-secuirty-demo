package com.shiv.spring_security.service;

import com.shiv.spring_security.entity.User;
import com.shiv.spring_security.entity.VerificationToken;
import com.shiv.spring_security.model.UserModel;

public interface UserService {

    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificatinToken(String oldToken);
}
