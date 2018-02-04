package web;


import com.unionpay.acp.sdk.AcpService;
import com.unionpay.acp.sdk.LogUtil;
import com.unionpay.acp.sdk.SDKConfig;
import com.unionpay.acp.sdk.SDKConstants;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import util.UnionPayConstantUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/commons/pay")
public class UnionPayController {

    /**
     * 交易查询
     *
     * @param sessionId
     * @param accNo
     * @param orderId
     * @param txnTime
     * @param response
     * @return
     */
    @RequestMapping("/unionPayQuery.do")
    public @ResponseBody
    Map<String, Object> unionPay(String sessionId, String accNo, String orderId, String txnTime, HttpServletResponse response) {
        //判断参数
        //判断是否符合条件
        Map<String, String> contentData = new HashMap<String, String>();
        //配置银联全渠道信息
        contentData.put(SDKConstants.param_version, UnionPayConstantUtil.VERSION);                  //版本号
        contentData.put(SDKConstants.param_encoding, UnionPayConstantUtil.ENCODING);                //字符集编码 可以使用UTF-8,GBK两种方式
        contentData.put(SDKConstants.param_signMethod, SDKConfig.getConfig().getSignMethod()); //签名方法
        contentData.put(SDKConstants.param_txnType, UnionPayConstantUtil.TXN_TYPE_QUERY);                              //交易类型 78-开通查询
        contentData.put(SDKConstants.param_txnSubType, UnionPayConstantUtil.TXN_SUBTYPE_DEFAULT);                           //交易子类型 00-根据账号accNo查询(默认）
        contentData.put(SDKConstants.param_bizType, UnionPayConstantUtil.BIZ_TYPE_AUTH);                          //业务类型 认证支付2.0
        contentData.put(SDKConstants.param_channelType, UnionPayConstantUtil.CHANNEL_TYPE_PC);                          //渠道类型07-PC

        //商户信息
        contentData.put(SDKConstants.param_merId, UnionPayConstantUtil.MER_ID);
        contentData.put(SDKConstants.param_orderId, orderId);
        contentData.put(SDKConstants.param_txnTime, txnTime);
        contentData.put(SDKConstants.param_accessType, UnionPayConstantUtil.ACCESS_TYPE_DEFAULT);

        //买家账户信息
        ////////////如果商户号开通了【商户对敏感信息加密】的权限那么需要对 accNo,phoneNo加密使用：
        String accNo1 = AcpService.encryptData(accNo, UnionPayConstantUtil.ENCODING);            //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
        contentData.put(SDKConstants.param_accNo, accNo1);
        contentData.put(SDKConstants.param_encryptCertId, AcpService.getEncryptCertId());   //加密证书的certId，配置在acp_sdk.properties文件 acpsdk.encryptCert.path属性下

        //与银联通信
        try {
            Map<String, String> reqData = AcpService.sign(contentData, UnionPayConstantUtil.ENCODING);              //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
            String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();                          //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
            Map<String, String> rspData = AcpService.post(reqData, requestBackUrl, UnionPayConstantUtil.ENCODING); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
            StringBuffer parseStr = new StringBuffer("");
            if (!rspData.isEmpty()) {
                if (AcpService.validate(rspData, UnionPayConstantUtil.ENCODING)) {
                    LogUtil.writeLog("验证签名成功");
                    String respCode = rspData.get("respCode");
                    if (("00").equals(respCode)) {
                        //成功
                        parseStr.append("<br>解析敏感信息加密信息如下（如果有）:<br>");
                        String customerInfo = rspData.get("customerInfo");
                        if (null != customerInfo) {
                            Map<String, String> cm = AcpService.parseCustomerInfo(customerInfo, UnionPayConstantUtil.ENCODING);
                            parseStr.append("customerInfo明文: " + cm + "<br>");
                        }
                        String an = rspData.get("accNo");
                        if (null != an) {
                            an = AcpService.decryptData(an, UnionPayConstantUtil.ENCODING);
                            parseStr.append("accNo明文: " + an);
                        }
                        //TODO
                    } else {
                        //其他应答码为失败请排查原因或做失败处理
                        //TODO
                    }
                } else {
                    LogUtil.writeErrorLog("验证签名失败");
                    //TODO 检查验证签名失败的原因
                }
            } else {
                //未返回正确的http状态
                LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
            }
            String reqMessage = UnionPayConstantUtil.genHtmlResult(reqData);
            String rspMessage = UnionPayConstantUtil.genHtmlResult(rspData);
            response.getWriter().write("请求报文:<br/>" + reqMessage + "<br/>" + "应答报文:</br>" + rspMessage + parseStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    /**
     * 首次使用开通
     *
     * @param sessionId
     * @param orderId
     * @param accNo
     * @param phoneNo
     * @param cvn2
     * @param expired
     * @param response
     * @return
     */
    @RequestMapping("/unionPayOpenCardBack.do")
    public @ResponseBody
    Map<String, Object> unionPayOpenCardBack(String sessionId, String orderId, String accNo, String phoneNo, String cvn2,
                                             String expired, HttpServletResponse response) {
        //判断参数是否合法
        //判断用户是否存在
        //判断订单是否存在 订单状态是否合法
        //判断银行卡号 手机号
        //封装数据
        Map<String, String> contentData = new HashMap<String, String>();
        //银联全渠道默认参数
        contentData.put(SDKConstants.param_version, UnionPayConstantUtil.VERSION);                  //版本号
        contentData.put(SDKConstants.param_encoding, UnionPayConstantUtil.ENCODING);           //字符集编码 可以使用UTF-8,GBK两种方式
        contentData.put(SDKConstants.param_signMethod, SDKConfig.getConfig().getSignMethod()); //签名方法
        contentData.put(SDKConstants.param_txnType, UnionPayConstantUtil.TXN_TYPE_OPEN_CARD);                     //交易类型 79-开通
        contentData.put(SDKConstants.param_txnSubType, UnionPayConstantUtil.TXN_SUBTYPE_DEFAULT);                      //交易子类型 00
        contentData.put(SDKConstants.param_bizType, UnionPayConstantUtil.BIZ_TYPE_AUTH);                          //业务类型 认证支付2.0
        contentData.put(SDKConstants.param_channelType, UnionPayConstantUtil.CHANNEL_TYPE_PC);               //渠道类型07-PC
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String txnTime = sdf.format(date);
        //商户接入参数   商户订单内容的补充
        contentData.put(SDKConstants.param_merId, UnionPayConstantUtil.MER_ID);
        contentData.put(SDKConstants.param_accessType, UnionPayConstantUtil.ACCESS_TYPE_DEFAULT);                            //接入类型，商户接入固定填0，不需修改
        contentData.put(SDKConstants.param_orderId, orderId);                           //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        contentData.put(SDKConstants.param_txnTime, txnTime);                           //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        contentData.put(SDKConstants.param_accType, UnionPayConstantUtil.ACC_TYPE_BANK_CARD);                              //账号类型

        //商户需要开通对银行卡进行开通的权限  否则商户不能再自己的站点开通
        //消费交易要素  买家交易的账号进行补充   买家个人信息
        Map<String, String> customInfoData = new HashMap<String, String>();
        //商户号要是开启了对银行卡号进行加密  就需要这一步骤
        customInfoData.put(SDKConstants.param_phoneNo, phoneNo);
        customInfoData.put(SDKConstants.param_cvn2, cvn2);
        customInfoData.put(SDKConstants.param_expired, expired);
        String customerInfoWithEncrypt = AcpService.getCustomerInfoWithEncrypt(customInfoData, null, UnionPayConstantUtil.ENCODING);
        contentData.put(SDKConstants.param_customerInfo, customerInfoWithEncrypt);
        //根据商户是否开启加密而设置
        String accNo1 = AcpService.encryptData(accNo, UnionPayConstantUtil.ENCODING);
        contentData.put(SDKConstants.param_accNo, accNo1);
        contentData.put(SDKConstants.param_encryptCertId, AcpService.getEncryptCertId());   //获取加密证书的id
        //后台通知地址
        contentData.put(SDKConstants.param_backUrl, SDKConfig.getConfig().getBackUrl());

        //进行交易签名   并应答
        try {
            Map<String, String> signData = AcpService.sign(contentData, UnionPayConstantUtil.ENCODING);
            String backRequestUrl = SDKConfig.getConfig().getBackRequestUrl();
            //响应回来的数据   这里只能证明与银联连接是否接通  正真的完成交易是在银联给后台地址发送数据  才能证明交易完成
            Map<String, String> resposeData = AcpService.post(signData, backRequestUrl, UnionPayConstantUtil.ENCODING);
            String reqMessage = UnionPayConstantUtil.genHtmlResult(signData);
            String rspMessage = UnionPayConstantUtil.genHtmlResult(resposeData);
            response.getWriter().write("请求报文:<br/>" + reqMessage + "<br/>" + "应答报文:</br>" + rspMessage);
            if (resposeData == null) {
                return null;
            }
            if (AcpService.validate(resposeData, UnionPayConstantUtil.ENCODING)) {
                String respCode = resposeData.get("respCode");
                if ("00".equals(respCode)) {
                    //处理业务 交易以受理
                    //保存订单日志 PayRequestLog

                } else if (("03").equals(respCode) ||
                        ("04").equals(respCode) ||
                        ("05").equals(respCode)) {
                    //处理没有受理 情况
                } else {
                    //这里处理错误  返回给前台提示用户
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }


    /**
     * 银联支付接口
     *
     * @param sessionId
     * @param orderId
     * @param smsCode
     * @param accNo
     * @param fq_num
     * @return
     */
    @RequestMapping("/unionPay.do")
    public @ResponseBody
    Map<String, Object> unionPay(String sessionId, String orderId, String smsCode, String accNo, String fq_num, HttpServletResponse response, String prize) {
        //判断当前用户
        //判断参数是否符合要求
        //判断订单是否存在  判断订单状态
        //判断accno银行卡号是否符合规格   并判断是否是合作的银行所属的卡号
        Map<String, String> contentData = new HashMap<String, String>();
        //银联全渠道默认参数
        contentData.put(SDKConstants.param_version, UnionPayConstantUtil.VERSION);                  //版本号
        contentData.put(SDKConstants.param_encoding, UnionPayConstantUtil.ENCODING);           //字符集编码 可以使用UTF-8,GBK两种方式
        contentData.put(SDKConstants.param_signMethod, SDKConfig.getConfig().getSignMethod()); //签名方法
        //txnSubType 01-消费 03-分期
        contentData.put(SDKConstants.param_txnType, UnionPayConstantUtil.TXN_TYPE_CONSUMER);                     //交易类型 01-消费
        contentData.put(SDKConstants.param_txnSubType, UnionPayConstantUtil.TXN_SUBTYPE_CONSUMER);                      //交易子类型 01-消费  03-分期
        //numberOfInstallments分期期数03 06 12  instalRate分期期率  mchntFeeSubsidy 商户补贴
        //contentData.put("instalTransInfo", "{numberOfInstallments=" + fq_num + "}");
        contentData.put(SDKConstants.param_bizType, UnionPayConstantUtil.BIZ_TYPE_AUTH);                          //业务类型 认证支付2.0
        contentData.put(SDKConstants.param_channelType, UnionPayConstantUtil.CHANNEL_TYPE_PC);               //渠道类型07-PC
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String txnTime = sdf.format(date);
        //商户接入参数   商户订单内容的补充
        contentData.put(SDKConstants.param_merId, UnionPayConstantUtil.MER_ID);
        contentData.put(SDKConstants.param_accessType, UnionPayConstantUtil.ACCESS_TYPE_DEFAULT);                            //接入类型，商户接入固定填0，不需修改
        contentData.put(SDKConstants.param_orderId, orderId);                           //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        contentData.put(SDKConstants.param_txnTime, txnTime);                           //订单发送时间，格式为yyyyMMddHHmmss，必须取当前时间，否则会报txnTime无效
        contentData.put(SDKConstants.param_currencyCode, UnionPayConstantUtil.CURRENCY_CODE);    //交易币种（境内商户一般是156 人民币）
        contentData.put(SDKConstants.param_txnAmt, prize);                               //交易金额，单位分，不要带小数点
        contentData.put(SDKConstants.param_accType, UnionPayConstantUtil.ACC_TYPE_BANK_CARD);                              //账号类型
        //contentData.put("orderDesc", "订单描述");
        contentData.put(SDKConstants.param_reqReserved, "orderDesc=订单描述;remark=备注"); //请求保留域
        //消费交易要素  买家交易的账号进行补充
        Map<String, String> customInfoData = new HashMap<String, String>();
        customInfoData.put(SDKConstants.param_smsCode, smsCode);
        //商户号要是开启了对银行卡号进行加密  就需要这一步骤
        String customerInfoWithEncrypt = AcpService.getCustomerInfoWithEncrypt(customInfoData, accNo, UnionPayConstantUtil.ENCODING);
        String accNo1 = AcpService.encryptData(accNo, UnionPayConstantUtil.ENCODING);
        contentData.put(SDKConstants.param_accNo, accNo1);
        contentData.put(SDKConstants.param_encryptCertId, AcpService.getEncryptCertId());   //获取加密证书的id
        contentData.put(SDKConstants.param_customerInfo, customerInfoWithEncrypt);
        //后台通知地址
        contentData.put(SDKConstants.param_backUrl, SDKConfig.getConfig().getBackUrl());

        //进行交易签名   并应答
        try {
            Map<String, String> signData = AcpService.sign(contentData, UnionPayConstantUtil.ENCODING);
            String backRequestUrl = SDKConfig.getConfig().getBackRequestUrl();
            //响应回来的数据   这里只能证明与银联连接是否接通  正真的完成交易是在银联给后台地址发送数据  才能证明交易完成
            Map<String, String> resposeData = AcpService.post(signData, backRequestUrl, UnionPayConstantUtil.ENCODING);
            String reqMessage = UnionPayConstantUtil.genHtmlResult(signData);
            String rspMessage = UnionPayConstantUtil.genHtmlResult(resposeData);
            response.getWriter().write("请求报文:<br/>" + reqMessage + "<br/>" + "应答报文:</br>" + rspMessage);
            if (resposeData == null) {
                return null;
            }
            if (AcpService.validate(resposeData, UnionPayConstantUtil.ENCODING)) {
                String respCode = resposeData.get("respCode");
                if ("00".equals(respCode)) {
                    //处理业务 交易以受理
                    //保存订单日志 PayRequestLog
                    LogUtil.writeLog("consume=======》OK");
                } else if (("03").equals(respCode) ||
                        ("04").equals(respCode) ||
                        ("05").equals(respCode)) {
                    //处理没有受理 情况
                } else {
                    //这里处理错误  返回给前台提示用户
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 银联消费短信
     */
    @RequestMapping("/unionPayConsumeSMS.do")
    public @ResponseBody
    Map<String, Object> unionPayConsumeSMS(String sessionId, String orderId, String accNo, String phoneNo, HttpServletResponse response, String prize) {
        //判断参数是否合法
        //查看用户是否存在
        //查找订单  判断订单状态
        //判断电话是否符合要求

        //封装数据
        Map<String, String> contentData = new HashMap<String, String>();
        //银联全渠道默认参数
        contentData.put(SDKConstants.param_version, UnionPayConstantUtil.VERSION);                  //版本号
        contentData.put(SDKConstants.param_encoding, UnionPayConstantUtil.ENCODING);           //字符集编码 可以使用UTF-8,GBK两种方式
        contentData.put(SDKConstants.param_signMethod, SDKConfig.getConfig().getSignMethod()); //签名方法
        contentData.put(SDKConstants.param_txnType, UnionPayConstantUtil.TXN_TYPE_SMS);                     //交易类型 77短信
        contentData.put(SDKConstants.param_txnSubType, UnionPayConstantUtil.TXN_SUBTYPE_CONSUMER_SMS);                      //交易子类型 01-消费  03-分期
        contentData.put(SDKConstants.param_bizType, UnionPayConstantUtil.BIZ_TYPE_AUTH);                          //业务类型 认证支付2.0
        contentData.put(SDKConstants.param_channelType, UnionPayConstantUtil.CHANNEL_TYPE_PC);               //渠道类型07-PC
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String txnTime = sdf.format(date);
        //商户接入参数   商户订单内容的补充
        contentData.put(SDKConstants.param_merId, UnionPayConstantUtil.MER_ID);
        contentData.put(SDKConstants.param_accessType, UnionPayConstantUtil.ACCESS_TYPE_DEFAULT);                            //接入类型，商户接入固定填0，不需修改
        contentData.put(SDKConstants.param_orderId, orderId);                           //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        contentData.put(SDKConstants.param_txnTime, txnTime);                           //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        contentData.put(SDKConstants.param_currencyCode, UnionPayConstantUtil.CURRENCY_CODE);    //交易币种（境内商户一般是156 人民币）
        contentData.put(SDKConstants.param_txnAmt, prize);                               //交易金额，单位分，不要带小数点
        contentData.put(SDKConstants.param_accType, UnionPayConstantUtil.ACC_TYPE_BANK_CARD);                              //账号类型
        //contentData.put("orderDesc", "订单描述");
        contentData.put(SDKConstants.param_reqReserved, "orderDesc=订单描述;remark=备注"); //请求保留域
        //消费交易要素  买家交易的账号进行补充
        Map<String, String> customInfoData = new HashMap<String, String>();
        //添加手机号码
        customInfoData.put(SDKConstants.param_phoneNo, phoneNo);
        String customerInfoWithEncrypt = AcpService.getCustomerInfoWithEncrypt(customInfoData, null, UnionPayConstantUtil.ENCODING);
        //商户号要是开启了对银行卡号进行加密  就需要这一步骤
        String accNo1 = AcpService.encryptData(accNo, UnionPayConstantUtil.ENCODING);
        contentData.put(SDKConstants.param_accNo, accNo1);
        contentData.put(SDKConstants.param_encryptCertId, AcpService.getEncryptCertId());   //获取加密证书的id
        contentData.put(SDKConstants.param_customerInfo, customerInfoWithEncrypt);
        //后台通知地址
        contentData.put(SDKConstants.param_backUrl, SDKConfig.getConfig().getBackUrl());

        //进行交易签名   并应答
        try {
            Map<String, String> signData = AcpService.sign(contentData, UnionPayConstantUtil.ENCODING);
            String backRequestUrl = SDKConfig.getConfig().getBackRequestUrl();
            //响应回来的数据   这里只能证明与银联连接是否接通  正真的完成交易是在银联给后台地址发送数据  才能证明交易完成
            Map<String, String> resposeData = AcpService.post(signData, backRequestUrl, UnionPayConstantUtil.ENCODING);
            String reqMessage = UnionPayConstantUtil.genHtmlResult(signData);
            String rspMessage = UnionPayConstantUtil.genHtmlResult(resposeData);
            response.getWriter().write("请求报文:<br/>" + reqMessage + "<br/>" + "应答报文:</br>" + rspMessage);
            if (resposeData == null) {
                return null;
            }
            if (AcpService.validate(resposeData, UnionPayConstantUtil.ENCODING)) {
                String respCode = resposeData.get("respCode");
                if ("00".equals(respCode)) {
                    //处理业务 交易以受理
                    //发送短信成功
                    LogUtil.writeLog("成功");
                } else if (("03").equals(respCode) ||
                        ("04").equals(respCode) ||
                        ("05").equals(respCode)) {
                    //处理没有受理 情况
                } else {
                    //这里处理错误  返回给前台提示用户
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 后台接收地址
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/unionPayNotify.do", method = {RequestMethod.POST})
    public @ResponseBody
    Map<String, Object> unionPayNotify(HttpServletRequest request, HttpServletResponse response) {
        try {

            LogUtil.writeLog("===============Notify=============");
            String encoding = request.getParameter(SDKConstants.param_encoding);
            Map<String, String> requestParam = getAllRequestParam(request);
            //在对数据进行验证的时候  不能在这之前对数据进行修改
            if (!AcpService.validate(requestParam, encoding)) {
                //验证失败  需要做出处理
            } else {
                //验证成功  进行处理业务
                //获取请求orderId
                String orderId = requestParam.get(SDKConstants.param_orderId);
                //查询订单是否存在  更改订单状态

                //获取买家的账号加密信息
                String customerInfo = requestParam.get(SDKConstants.param_customerInfo);
                if (customerInfo != null) {
                    Map<String, String> parseCustomerInfo = AcpService.parseCustomerInfo(customerInfo, UnionPayConstantUtil.ENCODING);
                    String accNo = parseCustomerInfo.get(SDKConstants.param_accNo);
                    if (accNo != null) {
                        String accNo1 = AcpService.decryptData(accNo, UnionPayConstantUtil.ENCODING);
                    }
                    //可以对账号进行存储
                }
                String respCode = requestParam.get(SDKConstants.param_respCode);
                //需要保存requestParam 数据  ===》日志 PayResponseLog

                if ("00".equals(respCode)) {
                    //需要保存交易流水号 queryId
                    //交易成功处理订单   paySuccessAliPay参考支付宝交易成功所需要处理的业务逻辑

                    response.getWriter().print("ok");
                }

                String rspMessage = UnionPayConstantUtil.genHtmlResult(requestParam);
                LogUtil.writeLog(rspMessage);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 银联退款接口
     * orderId 不能更原来订单的orderId相同 退款的orderId应该重新定义一个
     * txnTime 时间也是一样不能跟原来的一样
     */
    @RequestMapping("/unionPayRefund.do")
    public @ResponseBody
    Map<String, Object> unionPayRefund(String sessionId, String orderId, String origQryId, String txnTime, HttpServletResponse response) {
        //判断参数是否合法
        //判断当前用户
        //查找订单  判断订单是否存在  判断订单状态
        //获取退款原因
        //需要查找到该订单的流水号

        //封装数据
        Map<String, String> contentData = new HashMap<String, String>();
        //银联全渠道参数
        contentData.put(SDKConstants.param_version, UnionPayConstantUtil.VERSION);               //版本号
        contentData.put(SDKConstants.param_encoding, UnionPayConstantUtil.ENCODING);             //字符集编码 可以使用UTF-8,GBK两种方式
        contentData.put(SDKConstants.param_signMethod, SDKConfig.getConfig().getSignMethod()); //签名方法
        contentData.put(SDKConstants.param_txnType, UnionPayConstantUtil.TXN_TYPE_REFUND);                           //交易类型 04-退货
        contentData.put(SDKConstants.param_txnSubType, UnionPayConstantUtil.TXN_SUBTYPE_DEFAULT);                        //交易子类型  默认00
        contentData.put(SDKConstants.param_bizType, UnionPayConstantUtil.BIZ_TYPE_AUTH);                       //业务类型
        contentData.put(SDKConstants.param_channelType, UnionPayConstantUtil.CHANNEL_TYPE_PC);                       //渠道类型，07-PC，08-手机

        //商户数据封装
        contentData.put(SDKConstants.param_merId, UnionPayConstantUtil.MER_ID);                //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        contentData.put(SDKConstants.param_accessType, UnionPayConstantUtil.ACCESS_TYPE_DEFAULT);                         //接入类型，商户接入固定填0，不需修改
        //订单数据封装
        contentData.put(SDKConstants.param_orderId, orderId);          //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
        contentData.put(SDKConstants.param_txnTime, txnTime);      //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        contentData.put(SDKConstants.param_currencyCode, UnionPayConstantUtil.CURRENCY_CODE);                     //交易币种（境内商户一般是156 人民币）
        contentData.put(SDKConstants.param_txnAmt, "120");                          //****退货金额，单位分，不要带小数点。退货金额小于等于原消费金额，当小于的时候可以多次退货至退货累计金额等于原消费金额
        contentData.put(SDKConstants.param_backUrl, SDKConfig.getConfig().getBackUrl());               //后台通知地址，后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 退货交易 商户通知,其他说明同消费交易的后台通知
        //订单的流水号
        contentData.put(SDKConstants.param_origQryId, origQryId);
        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
        try {
            Map<String, String> reqData = AcpService.sign(contentData, UnionPayConstantUtil.ENCODING);        //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
            String url = SDKConfig.getConfig().getBackRequestUrl();                                    //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
            Map<String, String> rspData = AcpService.post(reqData, url, UnionPayConstantUtil.ENCODING);//这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
            if (!rspData.isEmpty()) {
                if (AcpService.validate(rspData, UnionPayConstantUtil.ENCODING)) {
                    String respCode = rspData.get("respCode");
                    if ("00".equals(respCode)) {
                        //处理业务
                        LogUtil.writeLog("refund ==== > ok");
                    } else if (("03").equals(respCode) ||
                            ("04").equals(respCode) ||
                            ("05").equals(respCode)) {
                        //其他原因
                    }
                } else {
                    //验证失败
                }
            } else {
                //排查问题
            }
            String reqMessage = UnionPayConstantUtil.genHtmlResult(reqData);
            String rspMessage = UnionPayConstantUtil.genHtmlResult(rspData);
            response.getWriter().write("请求报文:<br/>" + reqMessage + "<br/>" + "应答报文:</br>" + rspMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密证书   一天一更新  做定时任务
     */
    public void encryptCerUpdate() {
        Map<String, String> contentData = new HashMap<String, String>();
        contentData.put(SDKConstants.param_version, UnionPayConstantUtil.VERSION);                             //版本号
        contentData.put(SDKConstants.param_encoding, UnionPayConstantUtil.ENCODING);                     //字符集编码 可以使用UTF-8,GBK两种方式
        contentData.put(SDKConstants.param_signMethod, SDKConfig.getConfig().getSignMethod());    //签名方法  01:RSA证书方式  11：支持散列方式验证SHA-256 12：支持散列方式验证SM3
        contentData.put(SDKConstants.param_txnType, UnionPayConstantUtil.TXN_TYPE_ENCRYPTCER_UPDATE_QUERY);                                         //交易类型 95-银联加密公钥更新查询
        contentData.put(SDKConstants.param_txnSubType, UnionPayConstantUtil.TXN_SUBTYPE_DEFAULT);                                     //交易子类型  默认00
        contentData.put(SDKConstants.param_bizType, UnionPayConstantUtil.BIZ_TYPE_DEFAULT);                                     //业务类型  默认
        contentData.put(SDKConstants.param_channelType, UnionPayConstantUtil.CHANNEL_TYPE_PC);                                     //渠道类型

        contentData.put(SDKConstants.param_certType, UnionPayConstantUtil.CERT_TYPE);                                         //01：敏感信息加密公钥(只有01可用)
        contentData.put(SDKConstants.param_merId, UnionPayConstantUtil.MER_ID);                             //商户号码（商户号码777290058110097仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
        contentData.put(SDKConstants.param_accessType, UnionPayConstantUtil.ACCESS_TYPE_DEFAULT);                                         //接入类型，商户接入固定填0，不需修改
        //-----------------------------------------------
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String txnTime = sdf.format(date);
        contentData.put(SDKConstants.param_orderId, txnTime + "123");                         //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        contentData.put(SDKConstants.param_txnTime, txnTime);                     //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效                         //账号类型

        Map<String, String> reqData = AcpService.sign(contentData, UnionPayConstantUtil.ENCODING);               //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();                               //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData, requestBackUrl, UnionPayConstantUtil.ENCODING);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, UnionPayConstantUtil.ENCODING)) {
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode");
                if (("00").equals(respCode)) {
                    int resultCode = AcpService.updateEncryptCert(rspData, UnionPayConstantUtil.ENCODING);
                    if (resultCode == 1) {
                        LogUtil.writeLog("加密公钥更新成功");
                    } else if (resultCode == 0) {
                        LogUtil.writeLog("加密公钥无更新");
                    } else {
                        LogUtil.writeLog("加密公钥更新失败");
                    }

                } else {
                    //其他应答码为失败请排查原因
                    //TODO
                }
            } else {
                LogUtil.writeErrorLog("验证签名失败");
                //TODO 检查验证签名失败的原因
            }
        } else {
            //未返回正确的http状态
            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
        }
    }

    /**
     * 获取请求参数中所有的信息
     * 当商户上送frontUrl或backUrl地址中带有参数信息的时候，
     * 这种方式会将url地址中的参数读到map中，会导多出来这些信息从而致验签失败，这个时候可以自行修改过滤掉url中的参数或者使用getAllRequestParamStream方法。
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(
            final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                // 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                if (res.get(en) == null || "".equals(res.get(en))) {
                    // System.out.println("======为空的字段名===="+en);
                    res.remove(en);
                }
            }
        }
        return res;
    }

    /**
     * 获取请求参数中所有的信息。
     * 非struts可以改用此方法获取，好处是可以过滤掉request.getParameter方法过滤不掉的url中的参数。
     * struts可能对某些content-type会提前读取参数导致从inputstream读不到信息，所以可能用不了这个方法。理论应该可以调整struts配置使不影响，但请自己去研究。
     * 调用本方法之前不能调用req.getParameter("key");这种方法，否则会导致request取不到输入流。
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParamStream(
            final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        try {
            String notifyStr = new String(IOUtils.toByteArray(request.getInputStream()), UnionPayConstantUtil.ENCODING);
            LogUtil.writeLog("收到通知报文：" + notifyStr);
            String[] kvs = notifyStr.split("&");
            for (String kv : kvs) {
                String[] tmp = kv.split("=");
                if (tmp.length >= 2) {
                    String key = tmp[0];
                    String value = URLDecoder.decode(tmp[1], UnionPayConstantUtil.ENCODING);
                    res.put(key, value);
                }
            }
        } catch (UnsupportedEncodingException e) {
            LogUtil.writeLog("getAllRequestParamStream.UnsupportedEncodingException error: " + e.getClass() + ":" + e.getMessage());
        } catch (IOException e) {
            LogUtil.writeLog("getAllRequestParamStream.IOException error: " + e.getClass() + ":" + e.getMessage());
        }
        return res;
    }


    public static void main(String[] arg) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String format = sdf.format(date);
        System.out.println(format);
    }
}