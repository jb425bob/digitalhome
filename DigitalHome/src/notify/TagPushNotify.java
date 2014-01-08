package notify;

import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushTagMessageRequest;
import com.baidu.yun.channel.model.PushTagMessageResponse;

public class TagPushNotify extends PushNotify{

	private String tag;
	private PushTagMessageRequest request;
	private PushTagMessageResponse response;
	
	public TagPushNotify(String tag){
		this.tag = tag;
		request = new PushTagMessageRequest();
		request.setTagName(tag);
		request.setDeviceType(3);
	}
	@Override
	public int sendNotify(String content) {
		// TODO Auto-generated method stub
		request.setMessage(content);
		try {
			response = channelClient.pushTagMessage(request);
			System.out.println("push amount : " + response.getSuccessAmount());
			
		} catch (ChannelClientException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("TagPush Exception : " + e);
			return -1;
		} catch (ChannelServerException e) {
			// TODO Auto-generated catch block
			System.out.println(String.format("request_id: %d,error_code: %d,error_message: %s", e.getRequestId(), e.getErrorCode(), e.getErrorMsg() ));
			return -1;
		}
		return 0;
	}
	@Override
	public int setDeviceType(int deviceType) {
		// TODO Auto-generated method stub
		if(request != null)
			request.setDeviceType(deviceType);
		return 0;
	}

}
