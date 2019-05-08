package wtssg.xdly.digoubuyschedulerservice.sms;

public interface SmsSender {
    void sendSms(String tel, String tplId, String params);
}
