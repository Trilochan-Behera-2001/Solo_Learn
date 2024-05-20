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
import org.springframework.data.domain.Page;
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
@RequestMapping("/user")
public class UserController {

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

	@Autowired
	private ICommentsService commentsService;

	// method for adding common data to response
	@ModelAttribute
	public User getUserData(Model m, Principal p) {

		String username = p.getName();

		User user = userRepo.findByEmail(username);

		m.addAttribute("user", user);

		return user;

	}

	// user dash-board is shown when you enter in the browser
	// "localhost:4041/user/index"
	@GetMapping("/profile")
	public String dashboard(Model model) {

		model.addAttribute("title", "User : Dash - Profile");

		return "user/profile";

	}

	// open add posts form
	@GetMapping("/add-posts")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add : Posts");
		model.addAttribute("post", new Posts());

		return "user/add_post";

	}

	// procession add posts form

	@PostMapping("/process-posts")
	public String processContact(@ModelAttribute("posts") Posts posts, @RequestParam("imageName") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			// get the user object
			String email = principal.getName();
			User user = userRepo.findByEmail(email);

			// processing and uploading file

			if (file.isEmpty()) {

				// if the file is empty then try our message
				System.out.println("File is empty");
				posts.setPic("post.png");

			} else {
				// upload the file to folder and update the name of the file into the database
				// set the image name to Contact table
				posts.setPic(file.getOriginalFilename());

				// give the name of the folder where image stored
				File saveFile = new ClassPathResource("static/img").getFile();

				// get the path of the image
				java.nio.file.Path path = Paths
						.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				// Files.copy(input, target, option);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("file saved");

			}

			// set the user details to posts table for foreign key purpose
			posts.setUser(user);

			postsService.addPosts(posts);// save Posts

			System.out.println("DATA " + posts);

			// success message
			session.setAttribute("msg", "Your post saved successfully .");
			session.setAttribute("type", "alert-info");

		} catch (Exception se) {

			se.printStackTrace();
			// error message
			session.setAttribute("msg", "something went wrong ." + se.getMessage());
			session.setAttribute("type", "alert-danger");

		}

		return "redirect:/user/add-posts";

	}

	// show only user posts handler

	@GetMapping("/show-posts")
	public String showPosts(@RequestParam(defaultValue = "0") int pageNo, Model m, Principal p) {

		User user = getUserData(m, p);

		Page<Posts> posts = postsService.getPostsByUser(user, pageNo);

		m.addAttribute("currentPage", pageNo);
		m.addAttribute("totalElements", posts.getTotalElements());
		m.addAttribute("totalPages", posts.getTotalPages());

		// m.addAttribute("postsList",posts ); //old

		m.addAttribute("postsList", posts.getContent());

		return "user/your_posts";

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

		return "user/posts-details";

	}

	// show all posts handler

	@GetMapping("/show-all-posts")
	public String showAllPosts(Model m) {

		List<Posts> posts = postsService.getAllPosts();

		// m.addAttribute("postsList",posts ); //old

		m.addAttribute("posts", posts);

		return "user/all_posts";

	}

	// open setting handler
	@GetMapping("/setting")
	public String openSettings() {

		return "user/settings";

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

			return "redirect:/user/setting";

		} else {

			// error

			// message
			session.setAttribute("msg", "Enter correct password ...");
			session.setAttribute("type", "alert-danger");

			return "redirect:/user/setting";

		}

	}

	// update user

	// update user

	@PostMapping("/edit-user")
	public String updateUser(Model m, Principal p) {

		m.addAttribute("title", "Update : Contact Form ");

		// get the user details

		User user = getUserData(m, p);

		m.addAttribute("user", user);

		return "user/edit_profile";

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
			session.setAttribute("msg", "User Details update successfully ...");
			session.setAttribute("type", "alert-success");

		} catch (Exception se) {
			se.printStackTrace();
		}

		System.out.println(user);

		return "redirect:/user/profile";

	}

	// process posts comments

	// user/1/comments/1
	@PostMapping("/{pid}/comments")
	public String getComments(@ModelAttribute("comments") Comments comments, @PathVariable("pid") Integer pid, Model m,
			HttpSession session, Principal p) {

		try {

			// get the posts object

			Posts post = postsRepo.findById(pid).get();

			// get the user object

			User user = getUserData(m, p);

			// set the data
			comments.setPosts(post);
			comments.setUser(user);
			comments.setCdate(LocalDateTime.now());

			// save the comments

			commentsService.saveComments(comments);

			System.out.println(comments);

			// success message
			session.setAttribute("msg", "You comment successfully .");
			session.setAttribute("type", "alert-info");

		} catch (Exception se) {
			se.printStackTrace();
			// error message
			session.setAttribute("msg", "something went wrong ." + se.getMessage());
			session.setAttribute("type", "alert-danger");
		}

		return "redirect:/user/{pid}/posts";

	}

	// show all Content handler

	@GetMapping("/show-all-contents")
	public String showAllContents(Model m) {

		List<FreeContent> content = contentService.getAllFreeContent();

		m.addAttribute("contents", content);

		return "user/all_content";

	}
	

}
