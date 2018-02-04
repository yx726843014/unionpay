package web;

import com.unionpay.acp.sdk.SDKConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class InitServlet implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        SDKConfig.getConfig().loadPropertiesFromSrc();
    }
}
