<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %> 
<!DOCTYPE html>
<html lang="zh-cn">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="搜索页面">
    <meta name="author" content="张帅，沈科伟">
    <% String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/"; %> 
	<base href="<%=basePath%>">
    <title>搜索</title>
    <!-- Bootstrap core CSS -->
	<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.2.0/css/bootstrap.min.css">
    <!-- Custom styles for this template -->
	<link href="CSS/search.css" rel="stylesheet">
	<link href="IMAG/favicon.ico" rel="shortcut icon">
	<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
	<script src="http://cdn.bootcss.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
  </head>
  <body>
  
   <!-- Search bar -->
  <nav class="navbar navbar-default navbar-fixed-top">
    <div class="container-fluid">        
       <form class="navbar-form form-inline row" action="search/search.do">
         <div class="form-group col-md-offset-2 col-md-1">
          <label class="sr-only">Search Field</label>
          <select class="form-control" name="field">
          <c:choose>
          	<c:when test="${field == 'content'}"><option selected="selected" value="content">关键字</option></c:when>
          	<c:otherwise><option value="content">关键字</option></c:otherwise>
          </c:choose>
           <c:choose>
          	<c:when test="${field == 'author'}"><option selected="selected" value="author">作者</option></c:when>
          	<c:otherwise><option value="author">作者</option></c:otherwise>
          </c:choose>
           <c:choose>
          	<c:when test="${field == 'paragraph'}"><option selected="selected" value="paragraph">溯源</option></c:when>
          	<c:otherwise> <option value="paragraph">溯源</option></c:otherwise>
          </c:choose>
        </select>
        </div>
        <div class="form-group col-md-6 input-group search__box"> 
          <input type="text" class="form-control" name="term" value="${term}">
          <div class="input-group-btn">
          	<button type="submit" class="btn btn-info"><span class="glyphicon glyphicon-search"></span> 搜 索</button>
          </div>
        </div>
        <div class="form-group">
        	<input type="checkbox" name="deleted" value="true"/> 已删除
      	</div>
      </form>
    </div>
  </nav>  
  
  <!-- Search Results -->
  <div class="container"> 	
	<div class="main-full">
	<h6><small class="info"> 为您找到相关结果约<c:out value="${resultsNumber}"/>个</small></h6>
      <div class="content search__result">
      
		<c:forEach var="result" items="${results}" varStatus="i">
          <div class="post">
		    <div class="post__body">
		    
		      <h3 class="post__title">
		      	<c:choose>
		      	  <c:when test="${result.deleted}">
		      	    <del><a href="${result.url}" style="color:red"><c:out value="${result.title}"/></a></del>
		      	  </c:when>
		      	  <c:otherwise>
		      	    <a href="${result.url}"><c:out value="${result.title}"/></a>
		      	  </c:otherwise>
		      	</c:choose>
		      </h3>
		      
		      <div class="post__content panel panel-default">
    			<div class="panel-heading">
      				<h4 class="panel-title">
        				<a data-toggle="collapse" data-parent="#accordion" href="#content${i.index}">
         					<c:out value="${result.digest}"/>
        				</a>
      				</h4>
    			</div>
    		  	<div id="content${i.index}" class="panel-collapse collapse">
      		  		<div class="panel-body">
						<c:forEach var="par" items="${result.paragraph}">
							<p><c:out value="${par}"/></p>
						</c:forEach>
						<hr/>
						<c:forEach var="remark" items="${result.remarks}">
							<p>
							<span class="post__website"><c:out value="${remark.observer}"/></span> &middot; 
			  				<span class="post__date"><fmt:formatDate type="both" value="${remark.time}" /></span>
			  				</p>
							<p><c:out value="${remark.content}"/></p>
						</c:forEach>
      		  		</div>
    		  	</div>
  			  </div>
  			  
		      <div class="post__meta">		      	
			  	<span class="post__website"><c:out value="${result.website}"/></span> &middot; 
			  	<span class="post__date"><fmt:formatDate type="both" value="${result.date}" /></span> - 
			  	<c:set var="authors" value="${result.author}"></c:set> 
			  	<c:if test="${authors!=''&&authors!=null}">
			  		<c:forTokens var="author" items="${authors}" delims=" ">
			  			<a href="./search/search.do?field=author&term=${author}">${author}</a> 
			  		</c:forTokens>- 
			  	</c:if>
			  	<span><a href="./search/search.do?field=paragraph&term=${result.title}"><ins>同源</ins></a></span> - 
			  	<span class="post__remark"><ins>共有<c:out value="${result.remarks.size()}"/>条评论</ins></span>			  		  	
		      </div>
	        </div>
          </div>
        </c:forEach>
        
      </div>
    </div>
    
    <!-- Page turning -->
    <c:set var='minPage' value="${(page-5)>1 ? page-5 : 1}"/>
    <c:set var='maxPage' value="${(minPage+9)<pageNumber ? minPage+9 : pageNumber}"/>
	<div class="wp-pagenavi">
	  <c:if test="${page != 1}">
	  	<a class="previouspostslink" href="./search/getPage.do?term=${term}&page=${page-1}">&lt;上一页</a>
	  </c:if>
	  <c:forEach var="i" begin="${minPage}" end="${page-1}">
	  	<a class="page larger" href="./search/getPage.do?term=${term}&page=${i}"><c:out value="${i}"/></a>
	  </c:forEach>
	  	<span><c:out value="${page}"/></span>
	  <c:forEach var="i" begin="${page+1}" end="${maxPage}">
	  	<a class="page larger" href="./search/getPage.do?term=${term}&page=${i}"><c:out value="${i}"/></a>
	  </c:forEach>
	  <c:if test="${page != pageNumber}">
	  	<a class="page larger" href="./search/getPage.do?term=${term}&page=${page+1}">下一页&gt;</a>
	  </c:if>
	</div>
           
  </div>
  </body>  
</html>