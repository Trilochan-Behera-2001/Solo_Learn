package com.solo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solo.entity.User;

public interface IUserRepo extends JpaRepository<User, Integer> {

	
	public User findByEmail(String email);
	
	public User findByVerificationCode(String verificationCode);
	
	
	public List<User> findByRole(String role);
	
	
}
