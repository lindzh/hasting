<!DOCTYPE html>
<html>
<head>
   <title>机器详情</title>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <script src="/jquery/jquery-2.1.3.min.js"></script>
   <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css"/>
   <script src="/bootstrap/js/bootstrap.min.js"></script>

   <link rel="stylesheet" href="/css/webui.css"/>
</head>
<body>
	<div class="container">
		<#assign page='host'/>
		<#include 'wrap/navbar.ftl'/>
		  <div class="panel panel-primary">
		  		<div class="panel-heading">机器信息</div>
				<table class="table">
					<tbody>
						<tr>
							<td>机器ip</td>
							<td>${host.host}</td>
						</tr>
						<tr>
							<td>机器端口</td>
							<td>${host.port}</td>
						</tr>
						<tr>
							<td>所属应用</td>
							<td><a href="/app/info?appId=${host.app.id}">${host.app.name}</a></td>
						</tr>
						<tr>
							<td>机器权重</td>
							<td>${host.weight}</td>
						</tr>
						<tr>
							<td>token</td>
							<td>${host.token}</td>
						</tr>
						<tr>
							<td>机器状态</td>
							<td><#if (host.status>0)><span class="label label-success">up</span><#else><span class="label label-warning">down</span></#if></td>
						</tr>
					</tbody>
				</table>
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
						该机器没有暴露任何服务
					</#if>
		    </div>

		    <div class="panel panel-primary">
		    	<div class="panel-heading">
					依赖服务列表
		    	</div>
		    		<#if (consumeServiceCount>0)>
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
						<#list consumeServices as service>
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
						该机器没有依赖任何服务
					</#if>
		    </div>
	</div>
</body>
</html>