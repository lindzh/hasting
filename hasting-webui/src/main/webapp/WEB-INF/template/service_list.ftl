<!DOCTYPE html>
<html>
<head>
   <title>服务列表</title>
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
		<#assign page='service'/>
		<#include 'wrap/navbar.ftl'/>
		<div class="panel panel-primary">
		  	<div class="panel-body">
                <form id="searchParams" action="/service/list" method="get">
				<div class="kop-list-select">
					<div class="kop-list-select-left">
                        <div class="form-inline">
							<div class="btn-group">
								<button type="button" class="btn btn-primary">应用</button>
								<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
									${app.name}<span class="caret"></span>
								</button>
								<ul class="dropdown-menu" role="menu">
									<li><a href="/service/list?appId=0">全部</a></li>
									<#list apps as ap>
										<#if ap.id!=app.id>
										<li><a href="/service/list?appId=${ap.id}">${ap.name}</a></li>
										</#if>
									</#list>
								</ul>
							</div>
							<div class="input-group">
								<input id="keyword" name="keyword" type="text" class="form-control searchIpt" aria-describedby="basic-addon1"
									   placeholder="service keyword" value="${keyword}">
							</div>
							<div class="btn-group">
								<button type="submit"  class="btn btn-primary" >查询</button>
							</div>
                            <input type="hidden"  name="appId" value="${app.id}"/>
						</div>
					</div>
					<div class="kop-list-select-right">
					</div>
				</div>
                </form>
				<br/>
				<table class="table table-hover">
					<thead>
						<tr>
							<th>服务名称</th>
							<th>服务版本</th>
							<th>服务分组</th>
							<th>所属应用</th>
							<th>服务状态</th>
							<th>提供者数量</th>
							<th>消费者数量</th>
						</tr>
					</thead>
					<tbody>
						<#if (total>0)>
						<#list services as service>
						<tr>
							<td><a href="/service/detail?serviceId=${service.id}">${service.name}</a></td>
							<td>${service.version}</td>
							<td>${service.group}</td>
							<td><a href="/app/info?appId=${service.app.id}">${service.app.name}</a></td>
							<td><#if (service.status>0)><span class="label label-success">ok</span><#else><span class="label label-warning">down</span></#if></td>
							<td>${service.providerCount}</td>
							<td>${service.consumerCount}</td>
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
						<a href="/service/list?appId=${app.id}&keyword=${keyword}&limit=${limit}&offset=${pp}" aria-label="Previous">
							<span aria-hidden="true">&laquo;</span>
				        </a>
				    </li>
			    </#if>
			   	<#list 1..pages as p>
				   	<#assign off=(p-1)*limit>
				   	<#if off==offset>
						<li class="active"><a>${p}</a></li>
				   	<#else>
					    <li><a href="/service/list?appId=${app.id}&keyword=${keyword}&limit=${limit}&offset=${off}">${p}</a>
				   	</#if>
			    </#list>
				
			    <#if (c<pages)>
				    <li>
				    <#assign p=c*limit>
						<a href="/service/list?appId=${app.id}&keyword=${keyword}&limit=${limit}&offset=${p}" aria-label="Next">
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