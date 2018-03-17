<!DOCTYPE html>
<html>
<head>
   <title>服务详情</title>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <script src="/jquery/jquery-2.1.3.min.js"></script>
   <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css"/>
   <script src="/bootstrap/js/bootstrap.min.js"></script>

   <link rel="stylesheet" href="/css/webui.css"/>
</head>
<body>
	<div class="container">
		<#assign page='service'/>
		<#include 'wrap/navbar.ftl'/>
		  <div class="panel panel-primary">
		  		<div class="panel-heading">服务信息</div>
				<table class="table">
					<tbody>
						<tr>
							<td>服务名称</td>
							<td>${info.name}</td>
						</tr>
						<tr>
							<td>服务版本</td>
							<td>${info.version}</td>
						</tr>
						<tr>
							<td>服务所属应用</td>
							<td><a href="/app/info?appId=${info.app.id}">${info.app.name}</a></td>
						</tr>
						<tr>
							<td>服务分组</td>
							<td>${info.group}</td>
						</tr>
						<tr>
							<td>服务状态</td>
							<td><#if (info.status>0)><span class="label label-success">up</span><#else><span class="label label-warning">down</span></#if></td>
						</tr>
					</tbody>
				</table>
			</div>
			<br><br>

			<div class="panel panel-primary">
		    	<div class="panel-heading">
					服务提供机器列表
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
					消费者列表
		    	</div>
		    		<#if (consumerCount>0)>
			    	<table class="table">
						<thead>
							<tr>
								<td>机器ip</td>
								<td>机器端口</td>
								<td>所属应用</td>
								<td>机器权重</td>
								<td>token</td>
								<td>状态</td>
							</tr>
						</thead>
						<tbody>
						<#list consumers as provider>
							<tr>
								<td>${provider.host}</td>
								<td>${provider.port}</td>
								<td>${provider.app.name}</td>
								<td>${provider.weight}</td>
								<td>${provider.token}</td>
								<td><#if (provider.status>0)><span class="label label-success">up</span><#else><span class="label label-warning">down</span></#if></td>
							</tr>
						</#list>
						</tbody>
					</table>
					<#else>
						没有消费者
					</#if>
		    </div>
	</div>
</body>
</html>