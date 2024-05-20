package com.solo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solo.entity.Comments;
import com.solo.repo.ICommentsRepo;

@Service
public class CommentsServiceImpl implements ICommentsService {

	@Autowired
	private ICommentsRepo commentsRepo;

	@Override
	public Comments saveComments(Comments comments) {

		return commentsRepo.save(comments);

	}

}
