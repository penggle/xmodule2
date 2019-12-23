package com.penglecode.xmodule.security.oauth2.examples.upms.service;

import java.util.List;


import com.penglecode.xmodule.common.support.Page;
import com.penglecode.xmodule.common.support.Sort;
import com.penglecode.xmodule.security.oauth2.examples.upms.model.UpmsResource;

/**
 * UPMS资源服务
 * 
 * @author 	AutoGen
 * @date	2019年12月21日 下午 12:43:18
 */
public interface UpmsResourceService {

	/**
	 * 创建UPMS资源
	 * @param parameter
	 */
	public void createResource(UpmsResource parameter);
	
	/**
	 * 根据ID更新UPMS资源
	 * @param parameter
	 */
	public void updateResource(UpmsResource parameter);
	
	/**
	 * 根据ID删除UPMS资源
	 * @param id
	 */
	public void deleteResourceById(Long id);
	
	/**
	 * 根据ID获取UPMS资源
	 * @param id
	 * @return
	 */
	public UpmsResource getResourceById(Long id);
	
	/**
	 * 根据条件查询UPMS资源列表(排序、分页)
	 * @param condition		- 查询条件
	 * @param page			- 分页参数
	 * @param sort			- 排序参数
	 * @return
	 */
	public List<UpmsResource> getResourceListByPage(UpmsResource condition, Page page, Sort sort);
	
	/**
	 * 获取所有UPMS资源列表
	 * @return
	 */
	public List<UpmsResource> getAllResourceList();
	
}