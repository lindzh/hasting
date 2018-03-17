<!DOCTYPE html>
<html>
<head>
   <title>应用详情</title>
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
		  		<div class="panel-heading">应用信息</div>
				<table class="table">
					<tbody>
						<tr>
							<td>应用名称</td>
							<td>${app.name}</td>
						</tr>
						<tr>
							<td>负责人</td>
							<td><#if app.owner??>${app.owner}</#if></td>
						</tr>
						<tr>
							<td>email</td>
							<td><#if app.email??>${app.email}</#if></td>
						</tr>
						<tr>
							<td>操作</td>
							<td><a href="/app/consumers?appId=${app.id}">查看消费情况</a></td>
						</tr>
					</tbody>
				</table>
			</div>

			<div class="panel panel-primary">
		    	<div class="panel-heading">
					应用提供者机器列表 &nbsp;&nbsp;&nbsp;<a href="/weight/edit/${app.id}">权重编辑</a>
		    	</div>
		    		<#if (providerCount>0)>
			    	<table class="table">
						<thead>
							<tr>
								<td>机器ip</td>
								<td>机器端口</td>
								<td>机器权重</td>
								<td>token</td>
								<td>状态</td>
							</tr>
						</thead>
						<tbody>
						<#list providers as provider>
							<tr>
								<td>${provider.host}</td>
								<td>${provider.port}</td>
								<td>${provider.weight}</td>
								<td>${provider.token}</td>
								<td><#if (provider.status>0)><span class="label label-success">up</span><#else><span class="label label-warning">down</span></#if></td>
							</tr>
						</#list>
						</tbody>
					</table>
					<#else>
						没有提供者
					</#if>
		    </div>

			<div class="panel panel-primary">
		    	<div class="panel-heading">
					暴露服务列表
		    	</div>
		    		<#if (provideServiceCount>0)>
			    	<table class="table">
						<thead>
							<tr>
								<td>服务名称</td>
								<td>服务版本</td>
								<td>服务分组</td>
								<td>服务状态</td>
							</tr>
						</thead>
						<tbody>
						<#list provideServices as service>
							<tr>
								<td>${service.name}</td>
								<td>${service.version}</td>
								<td>${service.group}</td>
								<td><#if (service.status>0)><span class="label label-success">up</span><#else><span class="label label-warning">down</span></#if></td>
							</tr>
						</#list>
						</tbody>
					</table>
					<#else>
						该应用没有暴露任何服务
					</#if>
		    </div>

		    <div class="panel panel-primary">
		    	<div class="panel-heading">
					依赖服务列表
		    	</div>
		    		<#if (dependServiceCount>0)>
			    	<table class="table">
						<thead>
							<tr>
								<td>应用</td>
								<td>服务名称</td>
								<td>服务版本</td>
								<td>服务分组</td>
								<td>服务状态</td>
							</tr>
						</thead>
						<tbody>
						<#list dependServices as service>
							<tr>
								<td>${service.app.name}</td>
								<td>${service.name}</td>
								<td>${service.version}</td>
								<td>${service.group}</td>
								<td><#if (service.status>0)><span class="label label-success">up</span><#else><span class="label label-warning">down</span></#if></td>
							</tr>
						</#list>
						</tbody>
					</table>
					<#else>
						该应用没有依赖任何服务
					</#if>
		    </div>
	</div>
</body>
</html>