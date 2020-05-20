package cn.zy.charg.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;


public class SmsUtil {

    public static boolean sendLoginSms(String number,String captcha){
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4FmS8YQPMcWMtYAnwVwa", "8ie1wELZI1Ai1S8sgQqHzjTUT4zaKq");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", number);
        request.putQueryParameter("SignName", "煦飞充电");
        request.putQueryParameter("TemplateCode", "SMS_183793398");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+captcha+"\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
       return true;
    }
}
