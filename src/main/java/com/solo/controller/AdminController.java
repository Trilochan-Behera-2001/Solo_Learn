package com.solo.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.solo.entity.Comments;
import com.solo.entity.FreeContent;
import com.solo.entity.Posts;
import com.solo.entity.User;
import com.solo.repo.ICommentsRepo;
import com.solo.repo.IPostsRepo;
import com.solo.repo.IUserRepo;
import com.solo.service.ICommentsService;
import com.solo.service.IFreeContentService;
import com.solo.service.IPostsService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private IUserRepo userRepo;

	@Autowired
	private IPostsRepo postsRepo;

	@Autowired
	private ICommentsRepo commentsRepo;

	@Autowired
	private IPostsService postsService;

	@Autowired
	private IFreeContentService contentService;

	// method for adding common data to response
	@ModelAttribute
	public User getAdminData(Model m, Principal p) {

		String username = p.getName();

		User user = userRepo.findByEmail(username);

		m.addAttribute("user", user);

		return user;

	}

	// admin dash-board is shown when admin enter in the browser
	// "localhost:4041/user/index"
	@GetMapping("/profile")
	public String dashboard(Model model) {

		model.addAttribute("title", "Admin : Dash - Profile");

		return "admin/profile";

	}

	// open setting handler
	@GetMapping("/setting")
	public String openSettings() {

		return "admin/settings";

	}

	@PostMapping("/change-password")
	public String getChangePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

		System.out.println(newPassword);

		String email = principal.getName();

		User loginuser = userRepo.findByEmail(email);

		if (this.bCryptPasswordEncoder.matches(oldPassword, loginuser.getPassword())) {

			// change the password
			loginuser.setPassword(bCryptPasswordEncoder.encode(newPassword));

			userRepo.save(loginuser);

			// message
			session.setAttribute("msg", "Password change successfully ...");
			session.setAttribute("type", "alert-success");

			return "redirect:/admin/setting";

		} else {

			// error

			// message
			session.setAttribute("msg", "Enter correct password ...");
			session.setAttribute("type", "alert-danger");

			return "redirect:/admin/setting";

		}

	}

	// show all posts handler

	@GetMapping("/show-all-posts")
	public String showAllPosts(Model m) {

		List<Posts> posts = postsService.getAllPosts();

		// m.addAttribute("postsList",posts ); //old

		m.addAttribute("posts", posts);

		return "admin/all_posts";

	}

	// showing perticular posts details

	@GetMapping("/{pid}/posts")
	public String showContactDetails(@PathVariable("pid") Integer pId, Model model, Principal principal) {

		// get the posts object by using post id
		Optional<Posts> postsOptional = postsRepo.findById(pId);
		Posts posts = postsOptional.get();

		String userNmae = posts.getUser().getName();

		// gets the all comments of the posts

		List<Comments> comments = commentsRepo.findAllByPid(pId);

		model.addAttribute("posts", posts);

		model.addAttribute("comments", comments);

		model.addAttribute("name", userNmae);

		model.addAttribute("title", posts.getTitle());

		return "admin/posts-details";

	}

	// update user

	@PostMapping("/edit-user")
	public String updateUser(Model m, Principal p) {

		m.addAttribute("title", "Update : Contact Form ");

		// get the user details

		User user = getAdminData(m, p);

		m.addAttribute("user", user);

		return "admin/edit_profile";

	}

	// update form processing

	@PostMapping("/process-update")
	public String updateContact(@ModelAttribute("user") User user, @RequestParam("imageName") MultipartFile file,
			Model model, HttpSession session) {

		try {

			// get the old user object
			User oldUser = userRepo.findById(user.getUid()).get();

			// image
			if (!file.isEmpty()) {
				// file work
				// rewrite

				// delete old phote

				// get the name of the folder where old image stored
				File deleteFile = new ClassPathResource("static/img").getFile();

				File file1 = new File(deleteFile, oldUser.getProfile());

				file1.delete();

				// update old photo

				// upload the file to folder and update the name of the file into the database

				// give the name of the folder where image stored
				File saveFile = new ClassPathResource("static/img").getFile();

				// get the path of the image
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				// Files.copy(input, target, option);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				// set the image name to User table
				user.setProfile(file.getOriginalFilename());

			} else {

				user.setProfile(oldUser.getProfile());

			}

			// save the new data of User

			userRepo.save(user);

			// message
			session.setAttribute("msg", "Admin Details update successfully ...");
			session.setAttribute("type", "alert-success");

		} catch (Exception se) {
			se.printStackTrace();
		}

		System.out.println(user);

		return "redirect:/admin/profile";

	}

	// content page

	@GetMapping("/free-content")
	public String getContentPage(Model m) {
		m.addAttribute("title", "Add : Free Content ");

		m.addAttribute("freeContent", new FreeContent());

		return "admin/add_free_content";

	}

	// procession add posts form

	@PostMapping("/process-content")
	public String processContent(@ModelAttribute("freeContent") FreeContent content,
			@RequestParam("imageName") MultipartFile file, Principal principal, HttpSession session) {

		try {

			// processing and uploading file

			if (file.isEmpty()) {

				// if the file is empty then try our message
				System.out.println("File is empty");
				content.setPic("content.png");

			} else {
				// upload the file to folder and update the name of the file into the database
				// set the image name to Contact table
				content.setPic(file.getOriginalFilename());

				// give the name of the folder where image stored
				File saveFile = new ClassPathResource("static/img").getFile();

				// get the path of the image
				java.nio.file.Path path = Paths
						.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				// Files.copy(input, target, option);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("file saved");

			}

			// set the current data
			content.setDate(LocalDateTime.now());

			contentService.saveContent(content);// save Contents

			System.out.println("DATA " + content);

			// success message
			session.setAttribute("msg", "Content saved successfully .");
			session.setAttribute("type", "alert-info");

		} catch (Exception se) {

			se.printStackTrace();
			// error message
			session.setAttribute("msg", "something went wrong ." + se.getMessage());
			session.setAttribute("type", "alert-danger");

		}

		return "redirect:/admin/free-content";

	}

	// show all Content handler

	@GetMapping("/show-all-contents")
	public String showAllContents(Model m) {

		List<FreeContent> content = contentService.getAllFreeContent();

		m.addAttribute("contents", content);

		return "admin/all_content";

	}

	// show all Users handler

	@GetMapping("/show-all-users")
	public String showAllUsers(Model m) {

		List<User> user = userRepo.findByRole("ROLE_USER");

		m.addAttribute("users", user);

		return "admin/all_user";

	}

	// showing perticular user details

	@GetMapping("/{uid}/user")
	public String showUserDetails(@PathVariable("uid") Integer uId, Model model) {

		// get the contact object by using contact id
		User user = userRepo.findById(uId).get();

		model.addAttribute("user", user);
		model.addAttribute("title", user.getName());

		return "admin/perticular-usee-profile";

	}

}
