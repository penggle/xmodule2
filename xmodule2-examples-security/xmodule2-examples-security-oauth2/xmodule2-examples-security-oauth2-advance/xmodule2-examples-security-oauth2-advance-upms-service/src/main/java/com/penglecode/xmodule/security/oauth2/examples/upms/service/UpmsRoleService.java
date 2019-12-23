package com.penglecode.xmodule.security.oauth2.examples.upms.service;

import java.util.List;


import com.penglecode.xmodule.common.support.Page;
import com.penglecode.xmodule.common.support.Sort;
import com.penglecode.xmodule.security.oauth2.examples.upms.model.UpmsRole;

/**
 * UPMS角色服务
 * 
 * @author 	AutoGen
 * @date	2019年12月21日 下午 12:43:18
 */
public interface UpmsRoleService {

	/**
	 * 创建UPMS角色
	 * @param parameter
	 */
	public void createRole(UpmsRole parameter);
	
	/**
	 * 根据ID更新UPMS角色
	 * @param parameter
	 */
	public void updateRole(UpmsRole parameter);
	
	/**
	 * 根据ID删除UPMS角色
	 * @param id
	 */
	public void deleteRoleById(Long id);
	
	/**
	 * 根据ID获取UPMS角色
	 * @param id
	 * @return
	 */
	public UpmsRole getRoleById(Long id);
	
	/**
	 * 根据条件查询UPMS角色列表(排序、分页)
	 * @param condition		- 查询条件
	 * @param page			- 分页参数
	 * @param sort			- 排序参数
	 * @return
	 */
	public List<UpmsRole> getRoleListByPage(UpmsRole condition, Page page, Sort sort);
	
	/**
	 * 获取所有UPMS角色列表
	 * @return
	 */
	public List<UpmsRole> getAllRoleList();
	
}