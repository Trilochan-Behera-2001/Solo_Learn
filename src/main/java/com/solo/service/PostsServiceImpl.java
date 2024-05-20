package com.solo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.solo.entity.Posts;
import com.solo.entity.User;
import com.solo.repo.IPostsRepo;

import jakarta.servlet.http.HttpSession;

@Service
public class PostsServiceImpl implements IPostsService {

	@Autowired
	private IPostsRepo postsRepo;

	@Override
	public Posts addPosts(Posts posts) {

		posts.setPdate(LocalDateTime.now());

		return postsRepo.save(posts);

	}

	@Override
	public Page<Posts> getPostsByUser(User user, int pageNo) {

		Pageable pageable = PageRequest.of(pageNo, 6);

		return postsRepo.findByUser(user, pageable);

	}

	@Override
	public void removeSessionMessage() {

		HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest()
				.getSession();
		session.removeAttribute("msg");

	}

	@Override
	public List<Posts> getAllPosts() {

		return postsRepo.findAll();

	}

}
