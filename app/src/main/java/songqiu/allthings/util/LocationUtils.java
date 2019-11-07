/**
 * 
 */
package songqiu.allthings.util;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;


public class LocationUtils {
	AMapLocationClient locationClient;
	public void initLocation(Context context, AMapLocationListener locationListener) {
//        if (SettingUtil.isLogin()) {
		locationClient = new AMapLocationClient(context.getApplicationContext());
		locationClient.setLocationOption(getDefaultOption());
		locationClient.setLocationListener(locationListener);
		locationClient.startLocation();
//        }
	}
	/**
	 * 默认的定位参数
	 *
	 * @since 2.8.0
	 */
	private AMapLocationClientOption getDefaultOption() {
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
		mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
		mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
		AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
		return mOption;
	}

	public void destroyLocation() {
		if (locationClient != null) {
			locationClient.onDestroy();
			locationClient = null;
		}
	}

	public String getLocationCity(AMapLocation location){
		if (location == null) {
			return null;
		}
//		LogUtil.i("**************定位错误码:"+location.getErrorCode());

		String city = location.getCity();
//		UserLocation userLocation = new UserLocation();
//		userLocation.device_id = SettingUtil.getDeviceId();
//		userLocation.name = SettingUtil.getUserName();
//		userLocation.cert_no = SettingUtil.getUserCertNo();
//		userLocation.mobile = SettingUtil.getUserLoginPhone();
//
//		UserLocation.Location loc = new UserLocation.Location();
//		loc.ad_code = location.getAdCode();
//		loc.address = location.getAddress();
//		loc.city = location.getCity();
//		loc.city_code = location.getCityCode();
//		loc.country = location.getCountry();
//		loc.district = location.getDistrict();
//		loc.latitude = location.getLatitude();
//		loc.longitude = location.getLongitude();
//		loc.location_time = TimeUtil.formatUTC(location.getTime(),"");
//
//		switch (location.getLocationType()) {
//			case AMapLocation.LOCATION_TYPE_WIFI:
//				loc.location_type = "WIFI";
//				break;
//			case AMapLocation.LOCATION_TYPE_CELL:
//				loc.location_type = "CELL";
//				break;
//			case AMapLocation.LOCATION_TYPE_GPS:
//				loc.location_type = "GPS";
//				break;
//			case AMapLocation.LOCATION_TYPE_OFFLINE:
//				loc.location_type = "OFFLINE";
//				break;
//			case AMapLocation.LOCATION_TYPE_AMAP:
//				loc.location_type = "AMAP";
//				break;
//			case AMapLocation.LOCATION_TYPE_FIX_CACHE:
//				loc.location_type = "FIX_CACHE";
//				break;
//			case AMapLocation.LOCATION_TYPE_SAME_REQ:
//				loc.location_type = "SAME_REQ";
//				break;
//			default:
//				loc.location_type = "OTHER";
//				break;
//		}
//
//		loc.province = location.getProvince();
//		loc.street = location.getStreet();
//		loc.street_num = location.getStreetNum();
//		userLocation.geo_history = loc;
		return city;
	}
}
