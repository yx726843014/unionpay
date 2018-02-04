<%@ page language="java" contentType="text/html; charset=UTF-8" import="java.text.*" import="java.util.*"
         pageEncoding="UTF-8" %>

<form class="api-form" method="post" action="<%request.getContextPath();%>/unionpay/api/commons/pay/unionPayConsumeSMS.do"
      target="_blank">
    <p>
        <label>商户订单号：</label>
        <input id="orderId" type="text" name="orderId" placeholder="商户订单号"
               value="<%=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) %>" title="自行定义，8-32位数字字母"
               required="required"/>
    </p>
    <p>
        <label>银行卡号：</label>
        <input id="accNo" type="text" name="accNo" placeholder="商户订单号" value="6221558812340000" title="自行定义，8-32位数字字母"
               required="required"/>
    </p>
    <p>
        <label>分期：</label>
        <input id="fq_num" type="text" name="fq_num" placeholder="" value="06"
               title="分期 03 06 12" required="required"/>
    </p>
    <p>
        <label>短信通知手机号：</label>
        <input id="phoneNo" type="text" name="phoneNo" placeholder="用来接收短信的手机号" value="13552535506"
               title="用来接收短信的手机号，测试环境使用测试卡号对应的手机号，不会真正收到短信" required="required"/>
    </p>
    <p>
        <label>交易金额：</label>
        <input id="prize" type="text" name="prize" placeholder="交易金额" value="1000" title="单位分，需与原消费一致"
               required="required"/>
    </p>
    <p>
        <label>&nbsp;</label>
        <input type="submit" class="button" value="提交"/>
        <input type="button" class="showFaqBtn" value="遇到问题？"/>
    </p>
</form>

<div class="question">
    <hr/>
    <h4>消费撤销您可能会遇到...</h4>
    <p class="faq">
        暂无br>
    </p>
    <hr/>
    <jsp:include page="/pages/more_faq.jsp"/>
</div>