package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Jivan on 2020/1/23.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0) {
            return ServerResponse.creatByErrorMessage("用户名不存在");
        }

        //密码登录MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if(user == null) {
            return ServerResponse.creatByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccess("登录成功", user);
    }

    public ServerResponse<String> register(User user) {
        ServerResponse validResponse = this.checkVaild(user.getUsername(), Const.USERNAME);
        if(!validResponse.isSuccess()) {
            return validResponse;
        }

        validResponse = this.checkVaild(user.getEmail(), Const.EMAIL);
        if(!validResponse.isSuccess()) {
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount == 0) {
            return ServerResponse.creatByErrorMessage("注册失败");
        }
        return ServerResponse.creatBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkVaild(String str, String type) {
        if(StringUtils.isNotBlank(type)) {
            //开始校验
            if(Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0) {
                    return ServerResponse.creatByErrorMessage("用户名已存在");
                }
            }

            if(Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0) {
                    return ServerResponse.creatByErrorMessage("email已存在");
                }
            }
        }
        else {
            return ServerResponse.creatByErrorMessage("参数错误");
        }
        return ServerResponse.creatBySuccessMessage("校验成功");
    }

    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse vaildResponse = this.checkVaild(username, Const.USERNAME);
        if (vaildResponse.isSuccess()) {
            //用户不存在
            return ServerResponse.creatByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.creatBySuccess(question);
        }
        return ServerResponse.creatByErrorMessage("找回密码的问题是空的");
    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0) {
            //说明问题及问题答案是这个用户的并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);
            return ServerResponse.creatBySuccess(forgetToken);
        }
        return ServerResponse.creatByErrorMessage("问题的答案错误");
    }

    public ServerResponse<String> forgetResetPasword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)) {
            return ServerResponse.creatByErrorMessage("参数错误，Token需要传递");
        }
        ServerResponse vaildResponse = this.checkVaild(username, Const.USERNAME);
        if (vaildResponse.isSuccess()) {
            //用户不存在
            return ServerResponse.creatByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)) {
            return ServerResponse.creatByErrorMessage("Token无效或者过期");
        }
        if(StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
            if(rowCount > 0) {
                return ServerResponse.creatBySuccessMessage("修改密码成功");
            }
        }
        else {
            return ServerResponse.creatByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.creatByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户，因为我们会查询一个count(1)，如果不指定id，那么结果就是
        //true
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if(resultCount == 0) {
            return ServerResponse.creatByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0) {
            return ServerResponse.creatBySuccessMessage("密码更新成功");
        }
        return ServerResponse.creatByErrorMessage("密码更新失败");
    }

    public ServerResponse<User> updateInformation(User user) {
        //username是不能被更新的
        //email也要进行一个校验，校验新的email是不是已经存在，并且存在的Email如果相同的话，不能是我们当前这个用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if(resultCount > 0) {
            return ServerResponse.creatByErrorMessage("email已经存在，请更换email再进行更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setUsername(user.getUsername());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0) {
            return ServerResponse.creatBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.creatByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null) {
            ServerResponse.creatByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccess(user);
    }

    //backend

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user) {
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.creatBySuccess();
        }
        return ServerResponse.creatByError();
    }
}
