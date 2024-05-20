package com.solo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.solo.entity.Comments;

public interface ICommentsRepo extends JpaRepository<Comments, Integer> {

	@Query("SELECT c FROM Comments c WHERE c.posts.pid = :postsId")
	public List<Comments> findAllByPid(int postsId);
	
}
