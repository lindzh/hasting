<!DOCTYPE html>
<html>
<head>
   <title>rpc admin</title>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <script src="/jquery/jquery-2.1.3.min.js"></script>
   <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css"/>
   <script src="/bootstrap/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container">
		<#assign page='hosts'/>
		<#include 'navbar.ftl'/>

		<div class="panel panel-primary">
		    <div class="panel-body">

		    	<div class="form-inline">
					<div class="btn-group">
						<button type="button" class="btn btn-primary">namespace</button>
						<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
							${namespaceConfig.protocol}-${namespaceConfig.namespace}&nbsp;&nbsp;&nbsp;${namespaceConfig.md5}<span class="caret"></span>
						</button>
						<ul class="dropdown-menu" role="menu">
							<#list configs as config>
								<#if config.md5!=namespace>
								<li><a href="/webui/hosts?namespace=${config.md5}">${config.protocol}-${config.namespace}&nbsp;&nbsp;&nbsp;${config.md5}</a></li>
								</#if>
							</#list>
						</ul>
					</div>
				</div>
		    	<table class="table">
					<thead>
						<tr>
							<th>host</th>
							<th>port</th>
							<th>time</th>
							<th>token</th>
							<th>status</th>
							<th>services</th>
						</tr>
					</thead>
					<tbody>
					<#list hosts as host>
						<tr>
							<td>${host.host}</td>
							<td>${host.port}</td>
							<td>${parseDate(host.time)}</td>
							<td>${host.token}</td>
							<td>online</td>
							<td><a href="/webui/host/services?namespace=${namespace}&hostAndPort=${host.host}:${host.port}">services</a></td>
						</tr>
					</#list>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>