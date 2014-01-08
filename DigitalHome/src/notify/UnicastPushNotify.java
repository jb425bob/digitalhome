package notify;

import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushUnicastMessageRequest;
import com.baidu.yun.channel.model.PushUnicastMessageResponse;

public class UnicastPushNotify extends PushNotify{

	private String channelId;
	private String userId;
	private PushUnicastMessageRequest request;
	private PushUnicastMessageResponse response;
	
	public UnicastPushNotify(String channelId, String userId){
		this.channelId = channelId;
		this.userId = userId;
		request = new PushUnicastMessageRequest();
		
		request.setDeviceType(3);
		request.setChannelId((Long.parseLong(channelId)));
		request.setUserId(userId);
	}
	@Override
	public int sendNotify(String content) {
		// TODO Auto-generated method stub
		
		request.setMessage(content);
		try {
			response = channelClient.pushUnicastMessage(request);
			System.out.println("push amount: " + response.getSuccessAmount());
		} catch (ChannelClientException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("UnicastPush Excetption : " + e);
			return -1;
		} catch (ChannelServerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println(String.format("request_id: %d,error_code: %d,error_message: %s", e.getRequestId(), e.getErrorCode(), e.getErrorMsg() ));
		    return -1;
		}
		return 0;
	}

	public static void main(String[] args) {
		UnicastPushNotify unicast = new UnicastPushNotify("3585936215855423926", "812781237630002383");
		unicast.sendNotify("hell0");
	}
	@Override
	public int setDeviceType(int deviceType) {
		// TODO Auto-generated method stub
		if(request != null)
			request.setDeviceType(deviceType);
		return 0;
	}
}
