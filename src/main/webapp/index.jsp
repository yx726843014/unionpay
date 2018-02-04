<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>无跳转产品示例</title>
  <link rel="stylesheet" href="static/jquery-ui.min.css">
  <script src="static/jquery-1.11.2.min.js"></script>
  <script src="static/jquery-ui.min.js"></script>
  <script src="static/demo.js"></script>
  <script>
  	$(function() {
	    setApiDemoTabs("#tabs-tab1");
	    setApiDemoTabs("#tabs-tab2");
	    setApiDemoTabs("#tabs-tab3");
	    setApiDemoTabs("#tabs-tab4");
	  });
  </script>
  <link rel="stylesheet" href="static/demo.css">
</head>

<body style="background-color:#e5eecc;">
<div id="wrapper">
<div id="header">
<h2>无跳转产品示例</h2>

</div>

<div id="tabs-api">
  <ul>
    <li><a href="#tabs-api-1">前言</a></li>
    <li><a href="#tabs-api-2">银联侧开通</a></li>
    <li><a href="#tabs-api-3">商户侧开通</a></li>
    <li><a href="#tabs-api-4">银联侧开通（开通并支付）</a></li>
    <li><a href="#tabs-api-5">常见开发问题</a></li>
  </ul>
  
  <div id="tabs-api-1">
    <jsp:include  page="/pages/introduction.jsp"/>
  </div>

  <div id="tabs-api-2">
	<div id="tabs-tab1">
	  <ul>
	    <li><a href="#tabs-tab1-1">说明</a></li>
	    <li><a href="pages/openQuery.jsp">开通状态查询</a></li>
	    <li><a href="pages/openCard_front.jsp">银联侧开通</a></li>
	    <li><a href="pages/comsumeSMS.jsp">消费短信</a></li>
	    <li><a href="pages/consume.jsp">消费</a></li>
		<li><a href="pages/query.jsp">交易状态查询</a></li>
		<li><a href="pages/consumeUndo.jsp">消费撤销</a></li>
		<li><a href="pages/refund.jsp">退货</a></li>
		<li><a href="pages/file_transfer.jsp">对账文件下载</a></li>
	  </ul>
	  <div id="tabs-tab1-1">
	     <jsp:include  page="/pages/wutiaozhuan_front_intro.jsp"/>
	  </div>
	</div>
  </div>
  
   <div id="tabs-api-3">
	<div id="tabs-tab2">
	  <ul>
	    <li><a href="#tabs-tab2-1">说明</a></li>
	    <li><a href="pages/openSMS.jsp">开通短信</a></li>
	    <li><a href="pages/openCard_back.jsp">商户侧开通</a></li>
	    <li><a href="pages/comsumeSMS.jsp">消费短信</a></li>
	    <li><a href="pages/consume.jsp">消费</a></li>
		<li><a href="pages/query.jsp">交易状态查询</a></li>
		<li><a href="pages/consumeUndo.jsp">消费撤销</a></li>
		<li><a href="pages/refund.jsp">退货</a></li>
		<li><a href="pages/file_transfer.jsp">对账文件下载</a></li>
	  </ul>
	  <div id="tabs-tab2-1">
	     <jsp:include  page="/pages/wutiaozhuan_back_intro.jsp"/>
	  </div>
	</div>
  </div>
  
  <div id="tabs-api-4">
	<div id="tabs-tab3">
	  <ul>
	    <li><a href="#tabs-tab3-1">说明</a></li>
	    <li><a href="pages/openAndConsume.jsp">开通并消费</a></li>
	    <li><a href="pages/openQuery.jsp">开通状态查询</a></li>
	    <li><a href="pages/comsumeSMS.jsp">消费短信</a></li>
	    <li><a href="pages/consume.jsp">消费</a></li>
		<li><a href="pages/query.jsp">交易状态查询</a></li>
		<li><a href="pages/consumeUndo.jsp">消费撤销</a></li>
		<li><a href="pages/refund.jsp">退货</a></li>
		<li><a href="pages/file_transfer.jsp">对账文件下载</a></li>
	  </ul>
	  <div id="tabs-tab3-1">
	     <jsp:include  page="/pages/wutiaozhuan_openAndConsume_intro.jsp"/>
	  </div>
	</div>
  </div>
  
  <div id="tabs-api-5">
    <jsp:include  page="/pages/devlopHelp.jsp"/>
  </div>
    
  </div> <!-- end of tabs-api-->
</div><!-- end of wrapper-->
 
 
</body>
</html>