<%@ page language="java" contentType="text/html; charset=UTF-8" import="java.text.*" import="java.util.*"
         pageEncoding="UTF-8" %>

<form class="api-form" method="post" action="<%request.getContextPath();%>/unionpay/api/commons/pay/unionPay.do"
      target="_blank">

    <p>
        <label>商户订单号：</label>
        <input id="orderId" type="text" name="orderId" placeholder="商户订单号"
               value="<%=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) %>" title="自行定义，8-32位数字字母"
               required="required"/>
    </p>
    <p>
        <label>银行卡号：</label>
        <input id="accNo" type="text" name="accNo" placeholder="" value="6221558812340000"
               title="银行卡号" required="required"/>
    </p>
    <p>
        <label>金额：</label>
        <input id="prize" type="text" name="prize" placeholder="" value="1000"
               title="银行卡号" required="required"/>
    </p>
    <p>
        <label>分期：</label>
        <input id="fq_num" type="text" name="fq_num" placeholder="" value="06"
               title="分期 03 06 12" required="required"/>
    </p>
    <p>
        <label>验证码：</label>
        <input id="smsCode" type="text" name="smsCode" placeholder="" value="111111"
               title="短信验证码" required="required"/>
    </p>
    <p>
        <label>&nbsp;</label>
        <input type="submit" class="button" value="消费"/>
        <input type="button" class="showFaqBtn" value="遇到问题？"/>
    </p>
</form>

<div class="question">
    <hr/>
    <h4>消费撤销您可能会遇到...</h4>
    <p class="faq">
        暂无
    </p>
    <hr/>
    <jsp:include page="/pages/more_faq.jsp"/>
</div>