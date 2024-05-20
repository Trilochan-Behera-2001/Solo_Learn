package com.solo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.solo.entity.User;
import com.solo.repo.IUserRepo;

import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserRepo userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private IEmailService emailService;

	public void removeSessionMessage() {

		HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest()
				.getSession();
		session.removeAttribute("msg");

	}

	@Override
	public User registerUser(User user, String url) {

		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setProfile("default.png");
		user.setVerified_img("unverified.png");
		user.setAdminVerify("Not_Verified");

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// user email verification logic during user registration

		user.setVerifiedEmail(false);

		// store random code at user table during registration

		user.setVerificationCode(UUID.randomUUID().toString());

		User newUser = userRepo.save(user);

		if (newUser != null) {

			try {

				emailService.sendEmail(newUser, url);

			} catch (Exception se) {
				se.printStackTrace();
			}

		}

		return newUser;

	}

}
