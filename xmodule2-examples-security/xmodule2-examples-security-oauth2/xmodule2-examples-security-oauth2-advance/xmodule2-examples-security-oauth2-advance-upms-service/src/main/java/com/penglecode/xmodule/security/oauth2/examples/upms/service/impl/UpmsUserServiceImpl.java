package com.penglecode.xmodule.security.oauth2.examples.upms.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.penglecode.xmodule.common.consts.GlobalConstants;
import com.penglecode.xmodule.common.security.util.UserPasswordUtils;
import com.penglecode.xmodule.common.support.BusinessAssert;
import com.penglecode.xmodule.common.support.Page;
import com.penglecode.xmodule.common.support.Sort;
import com.penglecode.xmodule.common.support.ValidationAssert;
import com.penglecode.xmodule.common.util.DateTimeUtils;
import com.penglecode.xmodule.common.util.ModelDecodeUtils;
import com.penglecode.xmodule.common.util.ObjectUtils;
import com.penglecode.xmodule.common.util.StringUtils;
import com.penglecode.xmodule.platform.upms.consts.UpmsConstants;
import com.penglecode.xmodule.platform.upms.consts.em.UpmsUserStatusEnum;
import com.penglecode.xmodule.platform.upms.consts.em.UpmsUserTypeEnum;
import com.penglecode.xmodule.security.oauth2.examples.upms.mapper.UpmsUserMapper;
import com.penglecode.xmodule.security.oauth2.examples.upms.model.UpmsUser;
import com.penglecode.xmodule.security.oauth2.examples.upms.service.UpmsUserService;

@Service("upmsUserService")
public class UpmsUserServiceImpl implements UpmsUserService {

	@Autowired
	private UpmsUserMapper upmsUserMapper;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void createUser(UpmsUser parameter) {
		ValidationAssert.notNull(parameter, "参数不能为空");
		ValidationAssert.notEmpty(parameter.getUserName(), "用户名不能为空!");
		ValidationAssert.notEmpty(parameter.getPassword(), "用户密码不能为空!");
		//ValidationAssert.notEmpty(parameter.getRepassword(), "重复密码不能为空!");
		ValidationAssert.notEmpty(parameter.getRealName(), "真实姓名不能为空!");
		ValidationAssert.notEmpty(parameter.getMobilePhone(), "手机号码不能为空!");
		ValidationAssert.notEmpty(parameter.getEmail(), "Email不能为空!");
		//ValidationAssert.isTrue(parameter.getPassword().equals(parameter.getRepassword()), "两次密码输入不一致!");
		parameter.setUserId(null);
		parameter.setNickName(StringUtils.defaultIfEmpty(parameter.getNickName(), parameter.getUserName()));
		parameter.setPassword(UserPasswordUtils.encode(parameter.getPassword()));
		parameter.setUserType(UpmsUserTypeEnum.USER_TYPE_NORMAL.getTypeCode());
		parameter.setStatus(UpmsUserStatusEnum.USER_STATUS_ENABLED.getStatusCode());
		parameter.setUserIcon(StringUtils.defaultIfEmpty(parameter.getUserIcon(), UpmsConstants.DEFAULT_USER_AVATAR.value()));
		parameter.setCreateTime(DateTimeUtils.formatNow());
		parameter.setCreateBy(ObjectUtils.defaultIfNull(parameter.getCreateBy(), GlobalConstants.DEFAULT_SUPER_ADMIN_USER_ID));
		try {
			upmsUserMapper.insertModel(parameter);
		} catch (DuplicateKeyException e) {
            BusinessAssert.isTrue(!e.getCause().getMessage().toUpperCase().contains("USER_NAME"), "对不起,该用户名已存在!");
            throw e;
        }
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void updateUser(UpmsUser parameter) {
		ValidationAssert.notNull(parameter, "参数不能为空");
		ValidationAssert.notNull(parameter.getUserId(), "用户ID不能为空!");
		ValidationAssert.notEmpty(parameter.getUserName(), "用户名不能为空!");
		ValidationAssert.notEmpty(parameter.getRealName(), "真实姓名不能为空!");
		ValidationAssert.notEmpty(parameter.getMobilePhone(), "手机号码不能为空!");
		ValidationAssert.notEmpty(parameter.getEmail(), "Email不能为空!");
		parameter.setNickName(StringUtils.defaultIfEmpty(parameter.getNickName(), parameter.getUserName()));
		parameter.setUserIcon(StringUtils.defaultIfEmpty(parameter.getUserIcon(), UpmsConstants.DEFAULT_USER_AVATAR.value()));
		parameter.setUpdateTime(DateTimeUtils.formatNow());
		parameter.setUpdateBy(ObjectUtils.defaultIfNull(parameter.getUpdateBy(), GlobalConstants.DEFAULT_SUPER_ADMIN_USER_ID));
		UpmsUser puser = upmsUserMapper.selectModelById(parameter.getUserId());
		ValidationAssert.notNull(puser, "该用户已经不存在了!");
		try {
			Map<String,Object> paramMap = parameter.mapBuilder()
												   .withUserName()
												   .withMobilePhone()
												   .withNickName()
												   .withRealName()
												   .withEmail()
												   .withUserIcon()
												   .withUpdateBy()
												   .withUpdateTime()
												   .build();
			upmsUserMapper.updateModelById(parameter.getUserId(), paramMap);
		} catch (DuplicateKeyException e) {
            BusinessAssert.isTrue(!e.getCause().getMessage().toUpperCase().contains("USER_NAME"), "对不起,该用户名已存在!");
            throw e;
        }
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void deleteUserById(Long id) {
		ValidationAssert.notNull(id, "id不能为空");
		upmsUserMapper.deleteModelById(id);
	}

	@Override
	public UpmsUser getUserById(Long id) {
		return ModelDecodeUtils.decodeModel(upmsUserMapper.selectModelById(id));
	}

	@Override
	public UpmsUser getUserByUserName(String userName) {
		UpmsUser example = new UpmsUser();
		example.setUserName(userName);
		return ModelDecodeUtils.decodeModel(upmsUserMapper.selectModelByExample(example));
	}

	@Override
	public List<UpmsUser> getUserListByPage(UpmsUser condition, Page page, Sort sort) {
		List<UpmsUser> dataList = ModelDecodeUtils.decodeModel(upmsUserMapper.selectModelPageListByExample(condition, sort, new RowBounds(page.getOffset(), page.getLimit())));
    	page.setTotalRowCount(upmsUserMapper.countModelPageListByExample(condition));
		return dataList;
	}

	@Override
	public List<UpmsUser> getAllUserList() {
		return ModelDecodeUtils.decodeModel(upmsUserMapper.selectAllModelList());
	}

}