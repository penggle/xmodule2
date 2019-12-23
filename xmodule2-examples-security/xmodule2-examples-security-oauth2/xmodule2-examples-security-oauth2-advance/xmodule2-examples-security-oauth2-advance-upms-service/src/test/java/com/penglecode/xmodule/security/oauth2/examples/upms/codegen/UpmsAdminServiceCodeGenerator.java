package com.penglecode.xmodule.security.oauth2.examples.upms.codegen;

import com.penglecode.xmodule.common.codegen.service.ServiceCodeConfig;
import com.penglecode.xmodule.common.codegen.service.ServiceCodeGenerator;
import com.penglecode.xmodule.security.oauth2.examples.upms.model.UpmsResource;
import com.penglecode.xmodule.security.oauth2.examples.upms.model.UpmsRole;
import com.penglecode.xmodule.security.oauth2.examples.upms.model.UpmsUser;

public class UpmsAdminServiceCodeGenerator extends ServiceCodeGenerator {

	public static void main(String[] args) throws Exception {
		String projectBasePackage = "com.penglecode.xmodule.security.oauth2.examples.upms";
		String serviceInterfacePackage = "com.penglecode.xmodule.security.oauth2.examples.upms.service";
		String serviceImplementationPackage = "com.penglecode.xmodule.security.oauth2.examples.upms.service.impl";
		ServiceCodeConfig config = new ServiceCodeConfig(projectBasePackage, serviceInterfacePackage, serviceImplementationPackage);
		ServiceCodeGenerator serviceCodeGenerator = new UpmsAdminServiceCodeGenerator();
		serviceCodeGenerator.generateServiceCode(config, UpmsUser.class, UpmsRole.class, UpmsResource.class);
		//serviceCodeGenerator.generateServiceCode(config, UpmsUser.class);
	}

}
