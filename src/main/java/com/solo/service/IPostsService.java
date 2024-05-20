package com.solo.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.solo.entity.Posts;
import com.solo.entity.User;

public interface IPostsService {

	public Posts addPosts(Posts contact);

	// public List<Contact> getContactsByUser(User user);

	public Page<Posts> getPostsByUser(User user, int pageNo);

	public List<Posts> getAllPosts();
	
	
	public void removeSessionMessage();

}
