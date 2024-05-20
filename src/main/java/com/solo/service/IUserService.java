package com.solo.service;

import java.util.List;

import com.solo.entity.User;

public interface IUserService {
	
	public void removeSessionMessage();
	
	public User registerUser(User user ,String url);
	

}
