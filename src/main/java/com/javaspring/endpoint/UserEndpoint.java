package com.javaspring.endpoint;

import com.javaspring.entity.User;
import com.javaspring.gs_ws.*;
import com.javaspring.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.ArrayList;
import java.util.List;

@Endpoint
public class UserEndpoint {

    public static final String NAMESPACE_URI = "http://www.javaspring.com/users-ws";

    private UserService service;

    public UserEndpoint() {
    }

    public UserEndpoint(UserService service) {
        this.service = service;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserByIdRequest")
    @ResponsePayload
    public GetUserByIdResponse getUserById(@RequestPayload GetUserByIdRequest request) {
        GetUserByIdResponse response = new GetUserByIdResponse();
        User user = service.getUserById(request.getUserId());
        UserType userType = new UserType();
        BeanUtils.copyProperties(user, userType);
        response.setUserType(userType);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllUsersRequest")
    @ResponsePayload
    public GetAllUsersResponse getAllUsers(@RequestPayload GetAllUsersRequest request) {
        GetAllUsersResponse response = new GetAllUsersResponse();
        List<UserType> userTypeList = new ArrayList<UserType>();
        List<User> usersList = service.getAllUsers();
        for (User user : usersList) {
            UserType userType = new UserType();
            BeanUtils.copyProperties(user, userType);
            userTypeList.add(userType);
        }
        response.getUserType().addAll(userTypeList);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addUserRequest")
    @ResponsePayload
    public AddUserResponse addMovie(@RequestPayload AddUserRequest request) {
        AddUserResponse response = new AddUserResponse();
        UserType newUserType = new UserType();
        ServiceStatus serviceStatus = new ServiceStatus();

        User user = new User(request.getEmail());
        User savedUser = service.addUser(user);

        if (savedUser == null) {
            serviceStatus.setStatusCode("CONFLICT");
            serviceStatus.setMessage("Exception while adding Entity");
        } else {

            BeanUtils.copyProperties(savedUser, newUserType);
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Content Added Successfully");
        }
        response.setUserType(newUserType);
        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateUserRequest")
    @ResponsePayload
    public UpdateUserResponse updateUser(@RequestPayload UpdateUserRequest request) {
        UpdateUserResponse response = new UpdateUserResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        //1. find if user exist
        User userFromDB = service.getUserByEmail(request.getEmail());

        if(userFromDB == null) {
            serviceStatus.setStatusCode("NOT FOUND");
            serviceStatus.setMessage("User: " + request.getEmail() + " not found");
        } else {
            //2. Get updated user from the request
            userFromDB.setEmail(request.getEmail());

            boolean flag = service.updateUser(userFromDB);

            if(flag == false) {
                serviceStatus.setStatusCode("CONFLICT");
                serviceStatus.setMessage("Exception while updating user= " + request.getEmail());
            } else {
                serviceStatus.setStatusCode("SUCCESS");
                serviceStatus.setMessage("Connect updated Successfully");
            }
        }
        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteUserRequest")
    @ResponsePayload
    public DeleteUserResponse deleteUser(@RequestPayload DeleteUserRequest request) {
        DeleteUserResponse response = new DeleteUserResponse();
        ServiceStatus serviceStatus = new ServiceStatus();

        boolean flag = service.deleteUser(request.getUserId());

        if (flag == false) {
            serviceStatus.setStatusCode("FAIL");
            serviceStatus.setMessage("Exception while deleting user id=" + request.getUserId());
        } else {
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Content Delete Successfully");
        }
        response.setServiceStatus(serviceStatus);
        return response;
    }


}
