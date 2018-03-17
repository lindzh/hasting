<!DOCTYPE html>
<html>
<head>
   <title>权重列表</title>
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
		<#assign page='weight'/>
		<#include 'wrap/navbar.ftl'/>
		<div class="panel panel-primary">
		  	<div class="panel-body">
				<div class="kop-list-select">
					<div class="kop-list-select-left">
                        <div class="form-inline">
							<div class="btn-group">
								<button type="button" class="btn btn-primary">应用</button>
								<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
									${app.name}<span class="caret"></span>
								</button>
								<ul class="dropdown-menu" role="menu">
									<li><a href="/weight/list?appId=0">全部</a></li>
									<#list apps as ap>
										<#if ap.id!=app.id>
										<li><a href="/weight/list?appId=${ap.id}">${ap.name}</a></li>
										</#if>
									</#list>
								</ul>
							</div>
						</div>
					</div>
					<div class="kop-list-select-right">
					</div>
				</div>
				<br/>
				<table class="table table-hover">
					<thead>
						<tr>
							<th>所属应用</th>
							<th>机器ip</th>
							<th>机器端口</th>
							<th>当前权重</th>
							<th>期望权重</th>
							<th>机器状态</th>
							<th>权重状态</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<#if (total>0)>
						<#list hosts as host>
						<tr>
							<td>${host.app.name}</td>
							<td>${host.host}</td>
							<td>${host.port}</td>
							<td>${host.weight}</td>
							<td>${host.wantWeight}</td>
							<td><#if (host.status>0)><span class="label label-success">up</span><#else><span class="label label-warning">down</span></#if></td>
							<td><#if (host.weight==host.wantWeight)><span class="label label-success">已同步</span><#else><span class="label label-warning">等待同步</span></#if></td>
							<td><a href="/weight/edit/${host.app.id}">修改权重</a></td>
						</tr>
						</#list>
						</#if>
					</tbody>
				</table>
			</div>
		</div>
		<#if (total>0)>
		<#assign pages=(total/limit+1)?floor>
			<#if (pages>1)>
			<#assign c=(offset/limit+1)?floor>
			<nav>
			  <ul class="pagination">
				<#if (c>1)>
				    <li>
				  		<#assign pp=(c-2)*limit>
						<a href="/weight/list?appId=${app.id}&limit=${limit}&offset=${pp}" aria-label="Previous">
							<span aria-hidden="true">&laquo;</span>
				        </a>
				    </li>
			    </#if>
			   	<#list 1..pages as p>
				   	<#assign off=(p-1)*limit>
				   	<#if off==offset>
						<li class="active"><a>${p}</a></li>
				   	<#else>
					    <li><a href="/weight/list?appId=${app.id}&limit=${limit}&offset=${off}">${p}</a>
				   	</#if>
			    </#list>
				
			    <#if (c<pages)>
				    <li>
				    <#assign p=c*limit>
						<a href="/weight/list?appId=${app.id}&limit=${limit}&offset=${p}" aria-label="Next">
							<span aria-hidden="true">&raquo;</span>
						</a>
				    </li>
			    </#if>
			  </ul>
			</nav>
			</#if>
		</#if>
	</div>
</body>
</html>