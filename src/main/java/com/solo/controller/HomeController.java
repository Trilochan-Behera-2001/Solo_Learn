package com.solo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.solo.entity.User;
import com.solo.repo.IUserRepo;
import com.solo.service.IEmailService;
import com.solo.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private IEmailService emailService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IUserRepo userRepo;

	@GetMapping("/")
	public String getHome(Model model) {

		model.addAttribute("title", "Home - SoloLearn Platform");
		return "index";

	}

	@GetMapping("/signin")
	public String getSignin(Model model) {

		model.addAttribute("title", "Signin - SoloLearn Platform");
		return "signin";

	}

	@GetMapping("/signup")
	public String getSignup(Model model) {

		model.addAttribute("title", "Signup - SoloLearn Platform");

		model.addAttribute("user", new User());

		return "signup";

	}

	@GetMapping("/startsFree")
	public String getFreeContent(Model model) {

		model.addAttribute("title", "Starts its free - SoloLearn Platform");
		return "starts_its_free";

	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult validation,
			@RequestParam(value = "agrement", defaultValue = "false") boolean agrement, Map<String, Object> map,
			HttpSession session, HttpServletRequest req) {

		// for server side form validation
		if (validation.hasErrors()) {

			System.out.println("ERROR " + validation.toString());
			map.put("user", user);
			return "signup";

		}

		try {
			if (!agrement) {
				System.out.println("You have not agreed the term and conditions");
				throw new Exception("  You have not agreed the term and conditions");
			}

			// url

			// check the url getting from the browser

			String url = req.getRequestURL().toString();

			url = url.replace(req.getServletPath(), "");

			// add user data into database
			User result = this.userService.registerUser(user, url);

			map.put("user", new User());

			// message shown into the webpage
			session.setAttribute("msg", "Successfully Registered   !!");
			session.setAttribute("type", "alert-info");

			return "redirect:/signup";

		} catch (Exception se) {

			se.printStackTrace();
			map.put("user", user);
			session.setAttribute("msg", "Something Went Wrong !!" + se.getMessage());
			session.setAttribute("type", "alert-danger");

			return "redirect:/signup";

		}

	}

	// verify email

	@GetMapping("/verify-email")
	public String verifyUserEmail(@Param("code") String code, @Param("email") String email, Model m) {

		User user = userRepo.findByVerificationCode(code);

		boolean f = emailService.verifyAccount(code);

		// String email = user.getEmail();

		if (f) {

			m.addAttribute("email", email);

			m.addAttribute("msg", "Your account successfully verified .");

		} else {
			m.addAttribute("email", email);

			m.addAttribute("msg", "may be your verification code is incorrect or already verified .");

		}

		return "verifyEmail";

	}

	// contact us page

	@GetMapping("/contact")
	public String openContactUsPage() {

		return "contactus";

	}

}
