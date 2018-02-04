<%@ page language="java" contentType="text/html; charset=UTF-8"  import="java.text.*" import="java.util.*" 
    pageEncoding="UTF-8"%>

<form class="api-form" method="post" action="<%request.getContextPath();%>/unionpay/api/commons/pay/unionPayRefund.do" target="_blank">

<p>
<label>订单发送时间：</label>
<input id="txnTime" type="text" name="txnTime" placeholder="订单发送时间" value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>" title="取北京时间，YYYYMMDDhhmmss格式" required="required"/>
</p>
<p>
<label>商户订单号：</label>
<input id="orderId" type="text" name="orderId" placeholder="商户订单号" value="<%=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) %>" title="自行定义，8-32位数字字母" required="required"/>
</p>
<p>
<label>原交易流水号：</label>
<input id="origQryId" type="text" name="origQryId" placeholder="原交易流水号" value="" title="原交易流水号，从交易状态查询返回报文或代收的通知报文中获取 " required="required"/>
</p>
<p>
<label>&nbsp;</label>
<input type="submit" class="button" value="提交" />
<input type="button" class="showFaqBtn" value="遇到问题？" />
</p>
</form>

<div class="question">
<hr />
<h4>退货接口您可能会遇到...</h4>
<p class="faq">
<a href="https://open.unionpay.com//ajweb/help/respCode/respCodeList?respCode=2010002" target="_blank">订单重复[2010002]</a><br><br>
<a href="https://open.unionpay.com//ajweb/help/respCode/respCodeList?respCode=2040004" target="_blank"> 原交易状态不正确[2040004]</a><br><br>
<a href="https://open.unionpay.com//ajweb/help/respCode/respCodeList?respCode=2050004" target="_blank">与原交易信息不符[2050004]</a><br><br>
</p>
<hr />
 <jsp:include  page="/pages/more_faq.jsp"/>
</div>