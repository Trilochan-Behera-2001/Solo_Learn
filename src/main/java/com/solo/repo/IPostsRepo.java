package com.solo.repo;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.solo.entity.Posts;
import com.solo.entity.User;

public interface IPostsRepo extends JpaRepository<Posts, Integer> {

	// public List<Contact> findByUser(User user); //old

	// currentPage - page
	// contact per page - 6

	public Page<Posts> findByUser(User user, Pageable pageable); // new

}
