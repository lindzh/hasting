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
		<#assign page='limit'/>
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
                    </tbody>
                </table>
            </div>

        <#if (total>0)>
        <div class="kop-list-select">
            <div class="kop-list-select-right">
                <div class="btn-group">
                    <a class="btn btn-primary" href="/limit/edit/${app.id}/0" role="button" target="_blank">添加限流</a>
                </div>
            </div>
        </div>
        <br><br>

		<div class="panel panel-primary">
		    <div class="panel-heading">限流列表</div>
		  	<div class="panel-body">
				<br/>
				<table class="table table-hover">
					<thead>
						<tr>
							<th>限流类型</th>
							<th>限流应用</th>
							<th>限流服务</th>
							<th>限流方法</th>
							<th>限流时长</th>
							<th>限流次数</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<#if (total>0)>
						<#list limits as limitDef>
						<tr>
							<td><#if (limitDef.type<10)><span class="label label-primary">全局</span><#else><span class="label label-info">应用</span></#if></td>
							<td><#if limitDef.limitAppInfo??>${limitDef.limitAppInfo.name}<#else></#if></td>
							<td><#if limitDef.service??>${limitDef.service}<#else></#if></td>
							<td><#if limitDef.method??>${limitDef.method}<#else></#if></td>
							<td>${limitDef.ttl}</td>
							<td>${limitDef.count}</td>
							<td><a href="/limit/edit/${app.id}/${limitDef.id}">编辑</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="/limit/del/${app.id}/${limitDef.id}">删除</a></td>

						</tr>
						</#list>
						</#if>
					</tbody>
				</table>
			</div>
		</div>
		<#else>
            现在还没有任何限流&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="/limit/edit/${app.id}/0">添加限流</a>
		</#if>
	</div>
</body>
</html>