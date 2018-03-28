package com.example.think.baidumaptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {

    LocationClient locationClient;
    TextView locationText;
    MapView mapView;
    BaiduMap baiduMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationClient=new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        mapView=(MapView)findViewById(R.id.map_view);
        locationText=(TextView)findViewById(R.id.location_text);

        baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        autoGetLocation(true);//自动获取位置信息
        goToLocationOnMap(true);//切换回主线程对UI进行操作
        displayMeOnMap(true);////在地图中展示我的位置
    }

    public class MyLocationListener implements BDLocationListener {  //为LocationClient对象绑定监听器

        @Override
        public void onReceiveLocation(final BDLocation bdLocation) { //展示位置信息
            new Thread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder currentPosition=new StringBuilder();
                    currentPosition.append("经度：").append(bdLocation.getLongitude()).append("\n");
                    currentPosition.append("维度：").append(bdLocation.getLatitude()).append("\n");
                    currentPosition.append("国家：").append(bdLocation.getCountry()).append("\n");
                    currentPosition.append("省：").append(bdLocation.getProvince()).append("\n");
                    currentPosition.append("市：").append(bdLocation.getCity()).append("\n");
                    currentPosition.append("区：").append(bdLocation.getDistrict()).append("\n");
                    currentPosition.append("街道：").append(bdLocation.getStreet()).append("\n");
                    currentPosition.append("定位方式：");
                    if(bdLocation.getLocType()==BDLocation.TypeGpsLocation)
                        currentPosition.append("GPS");
                    else if(bdLocation.getLocType()==BDLocation.TypeNetWorkLocation)
                        currentPosition.append("网络");
                    else
                        currentPosition.append("基站");
                    //调用在主线程上获取currentPosition的方法
                    setUiOnUIThread(currentPosition.toString());
                }
            }).start();
        }
    }

    public void autoGetLocation(boolean flag) {  //自动获取位置信息
        if(flag) {
            LocationClientOption option=new LocationClientOption();
            option.setIsNeedAddress(true);
            locationClient.setLocOption(option);
            locationClient.start();
        }
    }

    public void setUiOnUIThread(final String response) { //切换回主线程对UI进行操作
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                locationText.setText(response);
            }
        });
    }

    public  void goToLocationOnMap(boolean flag) { //在地图上快速转移到指定位置
        if(flag) {
            LatLng lng=new LatLng(23.117055306224895,113.2759952545166);
            MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(lng);
            baiduMap.animateMapStatus(update);
            update=MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
        }
    }

    public void displayMeOnMap(boolean flag) {  //在地图中展示我的位置
        if(flag) {
            MyLocationData myLocationData=new MyLocationData.Builder()
                    .latitude(23.117055306224895)
                    .longitude(113.2759952545166)
                    .build();
            baiduMap.setMyLocationData(myLocationData);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }
}
