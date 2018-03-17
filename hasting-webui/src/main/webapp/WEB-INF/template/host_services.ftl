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
		  		<div class="panel-heading">host info</div>
				<table class="table">
					<tbody>
						<tr>
							<td>namespace</td>
							<td>${namespace}</td>
						</tr>
						<tr>
							<td>protocol</td>
							<td>${namespaceConfig.protocol}</td>
						</tr>
						<tr>
							<td>host</td>
							<td>${host}</td>
						</tr>
						<tr>
							<td>port</td>
							<td>${port}</td>
						</tr>
					</tbody>
				</table>
			</div>

			<div class="panel panel-primary">
		    	<div class="panel-heading">
					service list
		    	</div>
				<table class="table table-hover">
					<thead>
						<tr>
							<th>service</th>
							<th>version</th>
							<th>application</th>
							<th>group</th>
							<th>time</th>
						</tr>
					</thead>
					<tbody>
						<#if (services??)>
						<#list services as service>
						<tr>
							<td>${service.name}</td>
							<td>${service.version}</td>
							<td>${service.application}</td>
							<td>${service.group}</td>
							<td>${parseDate(service.time)}</td>
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