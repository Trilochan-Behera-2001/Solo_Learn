package com.solo.service;

import java.util.List;

import com.solo.entity.FreeContent;

public interface IFreeContentService {

	public FreeContent saveContent(FreeContent content);

	public List<FreeContent> getAllFreeContent();

}
