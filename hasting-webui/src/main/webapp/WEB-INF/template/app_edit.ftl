<!DOCTYPE html>
<html>
<head>
   <title>应用编辑</title>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <script src="/jquery/jquery-2.1.3.min.js"></script>
   <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css"/>
   <script src="/bootstrap/js/bootstrap.min.js"></script>

   <link rel="stylesheet" href="/css/webui.css"/>
</head>
<body>
	<div class="container">
		<#assign page='app'/>
		<#include 'wrap/navbar.ftl'/>
		  <div class="panel panel-primary">
		  		<div class="panel-heading">编辑</div>
		  		<form action="/app/editsubmit" method="post">
		            <input type="hidden" name="appId" id="appId" value="${app.id}">
		            <div class="form-group">
		                <label for="name">name</label>
		                <input type="text" class="form-control" id="name" name="name" value="${app.name}" disabled>
		            </div>
		            <div class="form-group">
		                <label for="owner">Owner</label>
		                <input type="text" class="form-control" id="owner" name="owner" <#if app.owner??>value="${app.owner}"</#if>>
		            </div>
		            <div class="form-group">
		                <label for="email">email</label>
		                <input type="text" class="form-control" id="email" name="email" <#if app.email??>value="${app.email}"</#if>>
		            </div>
		            <div class="form-group">
		                <label for="desc">desc</label>
		                <input type="textarea" class="form-control" id="desc" name="desc" <#if app.desc??>value="${app.desc}"</#if>>
		            </div>
					<div class="btn-group">
						<button type="submit" class="btn btn-primary">提交</button>
					</div>
				</form>
				<br>
			</div>

	</div>
</body>
</html>