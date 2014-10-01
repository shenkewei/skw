<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-cn">
	<head>
	    <meta charset="utf-8">
    	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1">
    	<meta name="description" content="搜索页面">
    	<meta name="author" content="刘昌思 沈科伟">
    	<% String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/"; %> 
		<base href="<%=basePath%>">
    	<title>首页</title>
    	<!-- Bootstrap core CSS -->
		<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.2.0/css/bootstrap.min.css">
    	<!-- Custom styles for this template -->
		<link href="CSS/index.css" rel="stylesheet">
		<link href="IMAG/favicon.ico" rel="shortcut icon">
		<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
		<script src="http://cdn.bootcss.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
  </head>
<body>
	<!-- Brand -->
	<header>
		<div>
			<img src="IMAG/ban.png" id="brand" alt="Brand"/>
			<h1>可信分布式实验室</h1>
		</div>		
    </header>
    <!-- Navigation Bar -->
	<nav class="bs-docs-nav nav">
		<div class="collapse navbar-collapse">
			<ul class="nav nav-pills">
                <li><a href="" target="main">首页</a></li>
                <li><a href="" target="main">个人信息</a></li>
                <li><a href="" target="main">帮助</a></li>
                <li><a href="" target="main">退出</a></li>
            </ul>
		</div>
	</nav>
	<!-- Main Content -->
	<div class="container-fluid">
		<div class="row">			
			<!-- Left Sidebar -->
			<aside class="col-md-1 full">
				<div id="search__button" class="bg-primary">
					<a href="search/search?field=content&term=" target="main">搜 索</a>
				</div>
				<div class="list-group">
					<a href="" target="main" class="list-group-item">每日编报</a>
					<a href="" target="main" class="list-group-item">统计信息</a>
					<a href="" target="main" class="list-group-item">关键词配置</a>
				</div>
    		</aside>
    		<!-- WorkSpace -->
			<div class="col-md-11 full">
        		<iframe src="search/search?field=content&term=" id="main" name="main"></iframe>
    		</div>		
		</div>	
	</div>
    
</body>
</html>

