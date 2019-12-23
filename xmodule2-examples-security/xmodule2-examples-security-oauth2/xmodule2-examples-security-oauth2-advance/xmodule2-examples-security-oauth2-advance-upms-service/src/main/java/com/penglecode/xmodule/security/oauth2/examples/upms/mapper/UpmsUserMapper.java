package com.penglecode.xmodule.security.oauth2.examples.upms.mapper;

import com.penglecode.xmodule.common.mybatis.mapper.BaseMybatisMapper;
import com.penglecode.xmodule.common.support.DefaultDatabase;
import com.penglecode.xmodule.security.oauth2.examples.upms.model.UpmsUser;

@DefaultDatabase
public interface UpmsUserMapper extends BaseMybatisMapper<UpmsUser> {
}