		<nav class="navbar navbar-inverse navbar-fixed-top">
		  <div class="container">
		    <!-- Brand and toggle get grouped for better mobile display -->
		    <div class="navbar-header">
		      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
		        <span class="sr-only">Toggle navigation</span>
		        <span class="icon-bar"></span>
		        <span class="icon-bar"></span>
		        <span class="icon-bar"></span>
		      </button>
		      <a class="navbar-brand" href="/service/list">WebUI</a>
		    </div>

		    <!-- Collect the nav links, forms, and other content for toggling -->
		    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
		      <ul class="nav navbar-nav">
		        <li <#if page=='service'>class="active"</#if>>
		        	<a href="/service/list">服务<span class="sr-only">(current)</span></a>
		        </li>
		        <li <#if page=='app'>class="active"</#if>>
		        	<a href="/app/list">应用</a>
		        </li>
               	<li <#if page=='host'>class="active"</#if>>
		        	<a href="/host/list">机器</a>
		        </li>
		        <li <#if page=='weight'>class="active"</#if>>
		        	<a href="/weight/list">权重</a>
		        </li>
		       	<li <#if page=='limit'>class="active"</#if>>
		        	<a href="/limit/list">限流</a>
		        </li>
		      </ul>
		      <ul class="nav navbar-nav navbar-right">
		      	<li>
		      	<a class="navbar-brand" href="https://github.com/lindzh/rpc" target="_blank"><img src="/images/github.jpg" width="22" height="22" /></a>
		      	</li>
		        <li class="dropdown">
		          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
		          lindezhi<span class="caret"></span>
		          </a>
		          <ul class="dropdown-menu" role="menu">
		            <li><a href="#">个人中心</a></li>
		            <li class="divider"></li>
		            <li><a href="/logout">退出</a></li>
		          </ul>
		        </li>
		      </ul>
		    </div><!-- /.navbar-collapse -->
		  </div><!-- /.container-fluid -->
		</nav>
				<br/><br/>		<br/><br/>