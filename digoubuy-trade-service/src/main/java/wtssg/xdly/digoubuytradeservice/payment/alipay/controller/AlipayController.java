package wtssg.xdly.digoubuytradeservice.payment.alipay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wtssg.xdly.digoubuytradeservice.common.constants.Constants;
import wtssg.xdly.digoubuytradeservice.common.constants.Parameters;
import wtssg.xdly.digoubuytradeservice.common.resp.ApiResult;
import wtssg.xdly.digoubuytradeservice.payment.alipay.config.AlipayConfig;
import wtssg.xdly.digoubuytradeservice.trade.dao.TradeMapper;
import wtssg.xdly.digoubuytradeservice.trade.entity.Trade;
import wtssg.xdly.digoubuytradeservice.trade.entity.TradeItem;
import wtssg.xdly.digoubuytradeservice.trade.service.TradeService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("alipay")
public class AlipayController {

    @Autowired
    @Qualifier("tradeServiceImpl")
    private TradeService tradeService;

    @Autowired
    private Parameters parameters;

    @Autowired
    private TradeMapper tradeMapper;

    @RequestMapping("/pay/{tradeNo}")
    public ApiResult<String> pay(@PathVariable("tradeNo") Long tradeNo, HttpSession session
            , HttpServletResponse response) throws AlipayApiException, IOException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, parameters.getMerchantPrivateRSA(),
                "json", AlipayConfig.charset, parameters.getPublicRSA(), AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
        String out_trade_no = String.valueOf(tradeNo);
        Long uuid = (Long) session.getAttribute(Constants.REQUEST_USER_SESSION);
        Trade trade = tradeService.getTrade(tradeNo, uuid);
        StringBuilder bodyString = new StringBuilder();
        String total_amount = String.valueOf(trade.getTotalPrice());
        List<TradeItem> tradeItemList = trade.getTradeItemList();
        for (TradeItem item : tradeItemList) {
            bodyString.append(item.getSkuName());
        }
        String body = bodyString.toString();
        String subject = "";
        if (body.length() > 30) {
            subject = body.substring(0, 30) + "...";
        } else {
            subject = body;
        }
        //商户订单号，商户网站订单系统中唯一订单号，必填
//        String out_trade_no = new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
//        //付款金额，必填
//        String total_amount = new String(request.getParameter("WIDtotal_amount").getBytes("ISO-8859-1"),"UTF-8");
//        //订单名称，必填
//        String subject = new String(request.getParameter("WIDsubject").getBytes("ISO-8859-1"),"UTF-8");
//        //商品描述，可空
//        String body = new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        result = new String(result.getBytes("UTF-8"), "UTF-8");
        ApiResult<String> apiResult = new ApiResult<>(Constants.RESP_STATUS_OK, "成功");
        apiResult.setData(result);

        //输出
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(result);
//        response.getWriter().flush();
        return apiResult;
    }


    /**
     *@Author JackWang [www.coder520.com]
     *@Date 2018/5/10 17:19
     *@Description 支付宝异步通知
     */
    @RequestMapping("/notifyUrl")
    public void notifyUrl(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {

        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]: valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, parameters.getPublicRSA(), AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

        if(signVerified){
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

            String receipt_amount = new String(request.getParameter("receipt_amount").getBytes("ISO-8859-1"),"UTF-8");

            System.out.println("订单号："+out_trade_no+"--支付宝交易号码："+trade_no+"--交易状态："+trade_status);

            if(trade_status.equals("TRADE_FINISHED")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                //注意：
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            }else if (trade_status.equals("TRADE_SUCCESS")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
                tradeService.payOrder(Long.valueOf(out_trade_no), new BigDecimal(receipt_amount));
            }
            response.getOutputStream().println("success");
        }else {//验证失败
            response.getOutputStream().println("fail");
        }

    }



}
