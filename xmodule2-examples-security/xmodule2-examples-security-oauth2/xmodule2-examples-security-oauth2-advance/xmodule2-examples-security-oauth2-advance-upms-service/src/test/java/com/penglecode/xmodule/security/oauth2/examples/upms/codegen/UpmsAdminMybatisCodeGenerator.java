package com.penglecode.xmodule.security.oauth2.examples.upms.codegen;

import com.penglecode.xmodule.common.codegen.mybatis.AbstractMybatisCodeGenerator;

public class UpmsAdminMybatisCodeGenerator extends AbstractMybatisCodeGenerator {

	public UpmsAdminMybatisCodeGenerator(String generatorConfigFileLocation) {
		super(generatorConfigFileLocation);
	}

	public static void main(String[] args) {
		String configFileLocation = "classpath:mybatis-codegen-config-upms.xml";
		UpmsAdminMybatisCodeGenerator generator = new UpmsAdminMybatisCodeGenerator(configFileLocation);
		generator.run();
	}

}
