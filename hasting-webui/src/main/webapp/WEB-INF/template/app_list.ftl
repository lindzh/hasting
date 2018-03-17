<!DOCTYPE html>
<html>
<head>
   <title>应用列表</title>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <script src="/jquery/jquery-2.1.3.min.js"></script>
   <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css"/>
   <script src="/bootstrap/js/bootstrap.min.js"></script>
   <link rel="stylesheet" href="/css/webui.css"/>
   <script>
       $(function () {
           $('[data-toggle="tooltip"]').tooltip()
       })
   </script>
</head>
<body>
	<div class="container">
		<#assign page='app'/>
		<#include 'wrap/navbar.ftl'/>
		<div class="panel panel-primary">
		  	<div class="panel-body">
				<br/>
				<table class="table table-hover">
					<thead>
						<tr>
							<th>应用名称</th>
							<th>负责人</th>
							<th>邮箱</th>
							<td>操作</td>
						</tr>
					</thead>
					<tbody>
						<#if (total>0)>
						<#list appList as app>
						<tr>
							<td><a href="/app/info?appId=${app.id}">${app.name}</a></td>
							<td><#if app.owner??>${app.owner}<#else><a href="/app/edit?appId=${app.id}">填写</a></#if></td>
							<td><#if app.email??>${app.email}<#else><a href="/app/edit?appId=${app.id}">填写</a></#if></td>
							<td><a href="/app/consumers?appId=${app.id}">查看消费情况</a>&nbsp;&nbsp;&nbsp;<a href="/app/edit?appId=${app.id}">编辑</a>&nbsp;&nbsp;&nbsp;<a href="/weight/edit/${app.id}">权重编辑</a></td>
						</tr>
						</#list>
						</#if>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>