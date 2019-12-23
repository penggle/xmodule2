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
import com.penglecode.xmodule.security.oauth2.examples.upms.mapper.UpmsRoleMapper;
import com.penglecode.xmodule.security.oauth2.examples.upms.model.UpmsRole;
import com.penglecode.xmodule.security.oauth2.examples.upms.service.UpmsRoleService;

@Service("upmsRoleService")
public class UpmsRoleServiceImpl implements UpmsRoleService {

	@Autowired
	private UpmsRoleMapper upmsRoleMapper;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void createRole(UpmsRole parameter) {
		ValidationAssert.notNull(parameter, "参数不能为空");
		upmsRoleMapper.insertModel(parameter);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void updateRole(UpmsRole parameter) {
		ValidationAssert.notNull(parameter, "参数不能为空");
		Map<String, Object> paramMap = parameter.mapBuilder()
												.withRoleId()
												.withRoleName()
												.withDescription()
												.withRoleCode()
												.withRoleType()
												.withCreateTime()
												.withCreateBy()
												.withUpdateTime()
												.withUpdateBy()
												.build();
		upmsRoleMapper.updateModelById(parameter.getRoleId(), paramMap);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void deleteRoleById(Long id) {
		ValidationAssert.notNull(id, "id不能为空");
		upmsRoleMapper.deleteModelById(id);
	}

	@Override
	public UpmsRole getRoleById(Long id) {
		return ModelDecodeUtils.decodeModel(upmsRoleMapper.selectModelById(id));
	}

	@Override
	public List<UpmsRole> getRoleListByPage(UpmsRole condition, Page page, Sort sort) {
		List<UpmsRole> dataList = ModelDecodeUtils.decodeModel(upmsRoleMapper.selectModelPageListByExample(condition, sort, new RowBounds(page.getOffset(), page.getLimit())));
    	page.setTotalRowCount(upmsRoleMapper.countModelPageListByExample(condition));
		return dataList;
	}

	@Override
	public List<UpmsRole> getAllRoleList() {
		return ModelDecodeUtils.decodeModel(upmsRoleMapper.selectAllModelList());
	}

}