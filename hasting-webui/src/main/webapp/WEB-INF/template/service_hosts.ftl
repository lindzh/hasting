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
		<#assign page='index'/>
		<#include 'navbar.ftl'/>
		  <div class="panel panel-primary">
		  		<div class="panel-heading">service info</div>
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
							<td>version</td>
							<td>${serviceVersion}</td>
						</tr>
						<tr>
							<td>service</td>
							<td>${serviceName}</td>
						</tr>
					</tbody>
				</table>
			</div>

			<div class="panel panel-primary">
		    	<div class="panel-heading">
					host list
		    	</div>
			    	<table class="table">
						<thead>
							<tr>
								<th>host</th>
								<th>port</th>
								<th>time</th>
								<th>token</th>
								<th>status</th>
							</tr>
						</thead>
						<tbody>
						<#list hosts as host>
							<tr>
								<td>${host.host}</td>
								<td>${host.port}</td>
								<td>${parseDate(host.time)}</td>
								<td>${host.token}</td>
								<td><#if (host.status>0)><span class="label label-success">up</span><#else><span class="label label-warning">down</span></#if></td>
							</tr>
						</#list>
						</tbody>
					</table>
		    </div>
	</div>
</body>
</html>