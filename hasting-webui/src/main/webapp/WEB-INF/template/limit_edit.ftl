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

            <div class="panel panel-primary">
                <div class="panel-heading">
                    限流编辑
                </div>
                <div class="panel-body">
                    <form id="weightForm" action="/limit/edit/${app.id}<#if info??>/${info.id}<#else>/0</#if>" method="post">

                        <div class="form-group">
                            <label for="limitType">限流类型</label>
                            <select class="form-control" id="limitType" name="limitType">
                              <option value="0" <#if info??><#if info.type==0>selected="true"</#if></#if>>全局限流</option>
                              <option value="1" <#if info??><#if info.type==1>selected="true"</#if></#if>>全局服务</option>
                              <option value="2" <#if info??><#if info.type==2>selected="true"</#if></#if>>全局方法</option>
                              <option value="10" <#if info??><#if info.type==10>selected="true"</#if></#if>>应用限流</option>
                              <option value="11" <#if info??><#if info.type==11>selected="true"</#if></#if>>应用服务</option>
                              <option value="12" <#if info??><#if info.type==12>selected="true"</#if></#if>>应用方法</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="limitApp">应用选择</label>
                            <select class="form-control" id="limitApp" name="limitAppId">
                                <option value="0">所有</option>
                                <#list apps as ap>
                                <option value="${ap.id}" <#if info??><#if info.limitAppId==ap.id>selected="true"</#if></#if>>${ap.name}</option>
                                </#list>
                            </select>
                        </div>
                         <div class="form-group">
                            <label for="service">服务</label>
                            <input type="text" class="form-control" id="service" name="service" <#if info??><#if info.service??>value="${info.service}"</#if></#if>>
                          </div>
                           <div class="form-group">
                              <label for="method">方法</label>
                              <input type="text" class="form-control" id="method" name="method"  <#if info??><#if info.method??>value="${info.method}"</#if></#if>>
                            </div>

                         <div class="form-group">
                            <label for="service">时长 毫秒</label>
                            <input type="ttl" class="form-control" id="ttl" name="ttl" <#if info??>value="${info.ttl}"</#if>>
                          </div>
                           <div class="form-group">
                              <label for="count">访问量</label>
                              <input type="text" class="form-control" id="count" name="count"  <#if info??>value="${info.count}"</#if>>
                            </div>
                            <button type="submit" class="btn btn-primary">提交</button>
                    </form>
                </div>
            </div>
	</div>
</body>
</html>