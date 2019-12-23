package com.penglecode.xmodule.security.oauth2.examples.upms.service;

import java.util.List;


import com.penglecode.xmodule.common.support.Page;
import com.penglecode.xmodule.common.support.Sort;
import com.penglecode.xmodule.security.oauth2.examples.upms.model.UpmsUser;

/**
 * UPMS用户服务
 * 
 * @author 	AutoGen
 * @date	2019年12月21日 下午 12:43:17
 */
public interface UpmsUserService {

	/**
	 * 创建UPMS用户
	 * @param parameter
	 */
	public void createUser(UpmsUser parameter);
	
	/**
	 * 根据ID更新UPMS用户
	 * @param parameter
	 */
	public void updateUser(UpmsUser parameter);
	
	/**
	 * 根据ID删除UPMS用户
	 * @param id
	 */
	public void deleteUserById(Long id);
	
	/**
	 * 根据ID获取UPMS用户
	 * @param id
	 * @return
	 */
	public UpmsUser getUserById(Long id);
	
	/**
	 * 根据用户名获取UPMS用户
	 * @param userName
	 * @return
	 */
	public UpmsUser getUserByUserName(String userName);
	
	/**
	 * 根据条件查询UPMS用户列表(排序、分页)
	 * @param condition		- 查询条件
	 * @param page			- 分页参数
	 * @param sort			- 排序参数
	 * @return
	 */
	public List<UpmsUser> getUserListByPage(UpmsUser condition, Page page, Sort sort);
	
	/**
	 * 获取所有UPMS用户列表
	 * @return
	 */
	public List<UpmsUser> getAllUserList();
	
}