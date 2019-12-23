package com.penglecode.xmodule.security.oauth2.examples.upms.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.penglecode.xmodule.common.support.Page;
import com.penglecode.xmodule.common.support.Sort;
import com.penglecode.xmodule.common.support.ValidationAssert;
import com.penglecode.xmodule.common.util.ModelDecodeUtils;
import com.penglecode.xmodule.security.oauth2.examples.upms.mapper.UpmsResourceMapper;
import com.penglecode.xmodule.security.oauth2.examples.upms.model.UpmsResource;
import com.penglecode.xmodule.security.oauth2.examples.upms.service.UpmsResourceService;

@Service("upmsResourceService")
public class UpmsResourceServiceImpl implements UpmsResourceService {

	@Autowired
	private UpmsResourceMapper upmsResourceMapper;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void createResource(UpmsResource parameter) {
		ValidationAssert.notNull(parameter, "参数不能为空");
		upmsResourceMapper.insertModel(parameter);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void updateResource(UpmsResource parameter) {
		ValidationAssert.notNull(parameter, "参数不能为空");
		Map<String, Object> paramMap = parameter.mapBuilder()
												.withResourceId()
												.withResourceName()
												.withHttpMethod()
												.withActionType()
												.withResourceUrl()
												.withResourceIcon()
												.withIndexPage()
												.withResourceType()
												.withCreateTime()
												.withCreateBy()
												.withUpdateTime()
												.withUpdateBy()
												.withParentResourceId()
												.withSiblingsIndex()
												.withAppRootResource()
												.build();
		upmsResourceMapper.updateModelById(parameter.getResourceId(), paramMap);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void deleteResourceById(Long id) {
		ValidationAssert.notNull(id, "id不能为空");
		upmsResourceMapper.deleteModelById(id);
	}

	@Override
	public UpmsResource getResourceById(Long id) {
		return ModelDecodeUtils.decodeModel(upmsResourceMapper.selectModelById(id));
	}

	@Override
	public List<UpmsResource> getResourceListByPage(UpmsResource condition, Page page, Sort sort) {
		List<UpmsResource> dataList = ModelDecodeUtils.decodeModel(upmsResourceMapper.selectModelPageListByExample(condition, sort, new RowBounds(page.getOffset(), page.getLimit())));
    	page.setTotalRowCount(upmsResourceMapper.countModelPageListByExample(condition));
		return dataList;
	}

	@Override
	public List<UpmsResource> getAllResourceList() {
		return ModelDecodeUtils.decodeModel(upmsResourceMapper.selectAllModelList());
	}

}