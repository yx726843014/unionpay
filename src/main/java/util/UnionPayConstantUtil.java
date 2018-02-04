package util;

import com.unionpay.acp.sdk.SDKConstants;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class UnionPayConstantUtil {

    /**
     * 版本 5.1.0
     */
    public static final String VERSION = "5.1.0";

    /**
     * 默认编码 UTF-8
     */
    public static final String ENCODING = "UTF-8";


    /**
     * 商户号
     */
    public static final String MER_ID = "777290058156102";

    /**
     * 商户名称
     */
    public static final String MER_NAME = "";

    /**
     * 商户简称
     */
    public static final String MER_ABBR = "";

    /**
     * 交易类型 01-消费
     */
    public static final String TXN_TYPE_CONSUMER = "01";

    /**
     * 交易类型 04-退货
     */
    public static final String TXN_TYPE_REFUND = "04";

    /**
     * 交易类型 31-消费撤销
     */
    public static final String TXN_TYPE_CONSUMER_UNDO = "31";

    /**
     * 交易类型 77-发送短信
     */
    public static final String TXN_TYPE_SMS = "77";

    /**
     * 交易类型  78-交易查询
     */
    public static final String TXN_TYPE_QUERY = "78";

    /**
     * 首次开通
     */
    public static final String TXN_TYPE_OPEN_CARD = "79";

    /**
     * 交易类型  95 - 银联加密公钥更新查询
     */
    public static final String TXN_TYPE_ENCRYPTCER_UPDATE_QUERY = "95";

    /**
     * 交易子类型  00-默认
     */

    public static final String TXN_SUBTYPE_DEFAULT = "00";

    /**
     * 交易子类型 01-消费
     */
    public static final String TXN_SUBTYPE_CONSUMER = "01";

    /**
     * 交易子类型 02-消费短信
     */
    public static final String TXN_SUBTYPE_CONSUMER_SMS = "02";

    /**
     * 交易子类型  03-分期
     */
    public static final String TXN_SUBTYPE_FQ = "03";

    /**
     * 业务类型  000000-默认
     */
    public static final String BIZ_TYPE_DEFAULT = "000000";
    /**
     * 业务类型  000201-B2C网关支付
     */
    public static final String BIZ_TYPE_B2C = "000201";

    /**
     * 业务类型  000301-认证支付2.0
     */
    public static final String BIZ_TYPE_AUTH = "000301";

    /**
     * 业务类型  000902-TOKEN支付
     */
    public static final String BIZ_TYPE_TOKEN = "000902";

    /**
     * 渠道类型  07-互联网
     */
    public static final String CHANNEL_TYPE_PC = "07";

    /**
     * 渠道类型  08-移动
     */
    public static final String CHANNEL_TYPE_MOBILE = "08";

    /**
     * 商户接入类型  0-商户直接接入 默认为0    1-机构接入   2-平台接入
     */
    public static final String ACCESS_TYPE_DEFAULT = "0";

    /**
     * 交易币种   默认人民币
     */
    public static final String CURRENCY_CODE = "156";

    /**
     * 账号类型   01-银行卡
     */
    public static final String ACC_TYPE_BANK_CARD = "01";

    /**
     * 账号类     03：IC卡
     */
    public static final String ACC_TYPE_IC_CARD = "03";

    /**
     * 敏感信息加密公钥  01
     */
    public static final String CERT_TYPE = "01";


    /**
     * 封装数据拆解
     * @param data
     * @return
     */
    public static String genHtmlResult(Map<String, String> data){

        TreeMap<String, String> tree = new TreeMap<String, String>();
        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            tree.put(en.getKey(), en.getValue());
        }
        it = tree.entrySet().iterator();
        StringBuffer sf = new StringBuffer();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            String key = en.getKey();
            String value =  en.getValue();
            if("respCode".equals(key)){
                sf.append("<b>"+key + SDKConstants.EQUAL + value+"</br></b>");
            }else
                sf.append(key + SDKConstants.EQUAL + value+"</br>");
        }
        return sf.toString();
    }
}
