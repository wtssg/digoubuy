package wtssg.xdly.digoubuyuserservice.sms;

public interface SmsSender {
    void sendSms(String tel, String tplId, String params);
}
