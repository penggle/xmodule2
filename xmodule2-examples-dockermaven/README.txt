参考文章：
https://shanhy.blog.csdn.net/article/details/89645254


1、配置docker主机，开放远程端口

	CentOS7上 docker配置文件位置：/lib/systemd/system/docker.service
	修改ExecStart，添加：-H tcp://0.0.0.0:2375，修改之后如下所示：
	ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock
	
	重启docker服务
	
	# 1，加载docker守护线程
	systemctl daemon-reload
	# 2，重启docker 
	systemctl restart docker
	
	从另外一台机器telnet docker主机测试2375端口是否开启成功,或者 curl http://docker-host-ip:2375/info
	
2、为你打包工程的电脑配置环境变量，添加DOCKER_HOST，值为tcp://docker-host-ip:2375，如果需要动态改变请在eclipse的Run As对话框中修改

	如果机器之前配置过DOCKER_HOST环境变量修改后可能需要重启才能生效，具体生不生效可用System.out.println(System.getenv("DOCKER_HOST"));来测试下

3、使用maven编译打包镜像

	打开cmd窗口，确定环境变量配置生效：输入 echo %DOCKER_HOST%，会输出 tcp://docker-host-ip:2375
	使用命令 mvn clean package dockerfile:build -Dmaven.test.skip=true 编译项目并构建docker镜像，编译结束自动推送镜像到docker主机中。
	使用命令 mvn clean package dockerfile:push 将构建好的docker镜像推送到阿里云上。记得使用参数 -Ddockerfile.username=xxx -Ddockerfile.password=yyy指定仓库的用户名与密码
	
	eclipse中首次运行的话：
	
	项目选中右击"Run As" -> "Maven build..."，在弹出页面中对话框中，name输入：<项目名称>-dockerbuild，goals输入：clean package dockerfile:build dockerfile:push (构建+上传aliyun)
	
	下面勾上Skip Tests，再在下面的Parameter框子中输入键值对：dockerfile.username = xxxx，dockerfile.password = yyyy (这个用户名密码是阿里云镜像仓库的访问控制)
	
	再在最上面的Environment(环境变量)这栏，输入键值对：DOCKER_HOST = tcp://docker-host-ip:2375
	
4、注意Dockerfile文件的位置，默认放在项目根路径下面