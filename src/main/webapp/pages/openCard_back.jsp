<%@ page language="java" contentType="text/html; charset=UTF-8"  import="java.text.*" import="java.util.*" 
    pageEncoding="UTF-8"%>

<form class="api-form" method="post" action="<%request.getContextPath();%>/unionpay/api/commons/pay/unionPayOpenCardBack.do" target="_blank">
<p>
<label>商户订单号：</label>
<input id="orderId" type="text" name="orderId" placeholder="商户订单号" value="<%=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) %>" title="自行定义，8-32位数字字母 " required="required"/>
</p>
<p>
<label>卡号：</label>
<input id="accNo" type="text" name="accNo" placeholder="卡号" value="6221558812340000" title="需要开通的卡号 " required="required"/>
</p>
 <p>
  <label>手机号：</label>
  <input id="phoneNo" type="text" name="phoneNo" placeholder="" value="13552535506" title="手机号" required="required"/>
 </p>
 <p>
  <label>CVN：</label>
  <input id="cvn2" type="text" name="cvn2" placeholder="" value="123" title="手机号" required="required"/>
 </p>
 <p>
  <label>有效期：</label>
  <input id="expired" type="text" name="expired" placeholder="" value="2311" title="手机号" required="required"/>
 </p>
<p>
<label>&nbsp;</label>
<input type="submit" class="button" value="开通" />
<input type="button" class="showFaqBtn" value="遇到问题？" />
</p>
</form>

<div class="question">
<hr />
<h4>代收您可能会遇到...</h4>
<p class="faq">
1.交易返回包含“HTTP Status 400”的错误html串<br>
&nbsp;&nbsp;1)不要出现规范不存在的字段。<br>
&nbsp;&nbsp;2)如果您是自己实现接口的，请注意http头加上Content-Type，Content-Length注意不要不小心设了0。<br><br>
2.<a href="https://open.unionpay.com/ajweb/help/respCode/respCodeList?respCode=9100004">交易失败 11[9100004]Signature verification failed</a><br><br>
</p>
<hr />
 <jsp:include  page="/pages/more_faq.jsp"/>
</div>
