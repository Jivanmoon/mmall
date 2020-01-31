package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by Jivan on 2020/1/23.
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);

    public ServerResponse<String> register(User user);

    public ServerResponse<String> checkVaild(String str, String type);

    public ServerResponse<String> selectQuestion(String username);

    public ServerResponse<String> checkAnswer(String username, String question, String answer);

    public ServerResponse<String> forgetResetPasword(String username, String passwordNew, String forgetToken);

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    public ServerResponse<User> updateInformation(User user);

    public ServerResponse<User> getInformation(Integer userId);

    //backend
    public ServerResponse checkAdminRole(User user);
}
