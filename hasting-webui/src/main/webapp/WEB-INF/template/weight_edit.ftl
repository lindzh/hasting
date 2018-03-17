<!DOCTYPE html>
<html>
<head>
   <title>权重编辑</title>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <script src="/jquery/jquery-2.1.3.min.js"></script>
   <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css"/>
   <script src="/bootstrap/js/bootstrap.min.js"></script>
	<script src="/js/weight.js"></script>
   <link rel="stylesheet" href="/css/webui.css"/>
</head>
<body>
	<div class="container">
		<#assign page='weight'/>
		<#include 'wrap/navbar.ftl'/>
		<div class="panel panel-primary">
	  		<div class="panel-heading">应用基本</div>
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
						<td><a href="/app/consumers?appId=${app.id}" target="_blank">查看消费情况</a></td>
					</tr>
				</tbody>
			</table>
		</div>

		<div class="panel panel-primary">
	    	<div class="panel-heading">
				权重编辑
	    	</div>
	    		<#if (hostCount>0)>
	    		<form id="weightForm" action="/weight/edit/${app.id}" method="post">
	    			<input type="hidden" id="weightData" name="data" value=""/>
	    		</form>
		    	<table class="table">
					<thead>
						<tr>
							<td>机器ip</td>
							<td>机器端口</td>
							<td>状态</td>
							<td>当前权重</td>
							<td>设置新的权重</td>
						</tr>
					</thead>
					<tbody>
					<#list hosts as provider>
						<tr>
							<td>${provider.host}</td>
							<td>${provider.port}</td>
							<td><#if (provider.status>0)><span class="label label-success">up</span><#else><span class="label label-warning">down</span></#if></td>
							<td>${provider.wantWeight}</td>
							<td>
								<input type="text" class="form-control" id="weight-${provider.id}" name="${provider.id}" value="${provider.wantWeight}"/>
							</td>
						</tr>
					</#list>
					</tbody>
				</table>
				<div class="btn-group">
					<button type="submit" onclick="weightSubmmit()" class="btn btn-primary">提交</button>
				</div>
				<#else>
					没有机器，请先启动服务，再修改权重
				</#if>
				<br/><br/>
	    </div>
	</div>
</body>
</html>