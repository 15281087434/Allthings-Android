package songqiu.allthings.api;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import songqiu.allthings.bean.BaseBean;


/**
 * 观察者模式管理接口
 * Created by cc on 2018/5/3.
 */

public interface ApiService {
    @POST("teamTalk/action/group/getMyGroup")
    Observable<BaseBean> getMyGroup(@QueryMap Map<String, String> req);


    @GET("tjkInfo/serviceInfo/queryWarningRecordDetail")
    Observable<BaseBean> getCompleteServiceInfoDetails(@QueryMap Map<String, Object> req);


    @POST("tjuser/personalInfo/addOrUpdatePersonInfo")
    @FormUrlEncoded
    Observable<BaseBean> addOrUpdatePersonInfo(
            @Field("nick") String nick, @Field("advantage") String advantage,
            @Field("experience") Integer experience, @Field("experienceWork") List<Integer> experienceWork,
            @Field("industry") List<Integer> industry, @Field("expectedWork") Integer expectedWork,
            @Field("language") List<Integer> language, @Field("occupation") Integer occupation,
            @Field("lowestSalary") Integer lowestSalary, @Field("education") Integer education,
            @Field("workHours") Integer workHours, @Field("haveChildren") Integer haveChildren,
            @Field("acceptShortProject") Integer acceptShortProject, @Field("channel") Integer channel,
            @Field("officeSkill") Integer officeSkill, @Field("officeSkillDescription") String officeSkillDescription,
            @Field("expectWorkload") Integer expectWorkload);



    @POST("/tjkInfo/appLog/checkUpdateStatus")
    Observable<BaseBean> getAppLogIsNeedUploadStatus(@QueryMap HashMap<String, Object> hashMap);



    @POST("/tjkInfo/activity/updateTjkHaveReadActivity")
    Observable<BaseBean> updataHaveReadActivity(@QueryMap Map<String, Object> map);



}
