
		<nav class="navbar navbar-inverse navbar-fixed-top">
		  <div class="container">
		    <div class="navbar-header">
		      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
		        <span class="sr-only">Toggle navigation</span>
		        <span class="icon-bar"></span>
		        <span class="icon-bar"></span>
		        <span class="icon-bar"></span>
		      </button>
		      <a class="navbar-brand" href="/webui/services">RPC</a>
		    </div>

		    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
		      <ul class="nav navbar-nav">
		        <li <#if page=='index'>class="active"</#if>>
		        	<a href="/webui/services">index<span class="sr-only">(current)</span></a>
		        </li>
		        <li <#if page=='hosts'>class="active"</#if>>
		        	<a href="/webui/hosts">hosts</a>
		        </li>
		       	<li <#if page=='configs'>class="active"</#if>>
		        	<a href="/webui/configs">configs</a>
		        </li>
		      </ul>
		    </div>
		  </div>
		</nav>
		<br/><br/><br/><br/>