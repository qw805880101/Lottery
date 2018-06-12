package com.tc.lottery.api;

import com.tc.lottery.bean.BaseBean;
import com.tc.lottery.bean.InitInfo;
import com.tc.lottery.bean.OrderInfo;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by admin on 2017/8/30.
 */

public interface Api {

//    /**
//     * 登录接口
//     *
//     * @param socialUid  第三方登陆ID
//     * @param deviceId   设备ID
//     * @param deviceType 设备类型(1:Android,2:IOS)
//     * @param loginType  登录类型（1、手机号码登录；2、QQ用户；3、微信）
//     * @param nickname   昵称（第三方登录需传）
//     * @param headIcon   头像路径（第三方登录需传）
//     * @param mobile     手机号
//     * @param smsCode    验证码
//     * @return
//     */
//    @POST("api/member/mLogin")
//    Observable<BaseBeanClass<LoginInfo>> login(@Query("social_uid") String socialUid,
//                                               @Query("device_id") String deviceId,
//                                               @Query("device_type") String deviceType,
//                                               @Query("login_type") String loginType,
//                                               @Query("nickname") String nickname,
//                                               @Query("headIcon") String headIcon,
//                                               @Query("mobile") String mobile,
//                                               @Query("smsCode") String smsCode);
//
//    /**
//     * 获取验证码
//     *
//     * @param mobile
//     * @return
//     */
//    @POST("api/member/msgSend")
//    Observable<BaseBean> getSmsCode(@Query("mobile") String mobile);
//
//    /**
//     * 获取首页推荐列表
//     * <p>
//     * //     * @param requestBody
//     *
//     * @param page  第几页,默认第1页
//     * @param limit 每页几条,默认10条
//     * @return
//     */
//    @POST("api/home/newsList")
//    Observable<BaseBeanClass<News>> getNewsList(/*@Body RequestBody requestBody,*/
//                                                @Query("page") int page,
//                                                @Query("limit") int limit);
//
//    /**
//     * 获取首页视频列表
//     *
//     * @param page
//     * @param limit
//     * @return
//     */
//    @POST("api/home/videoList")
//    Observable<BaseBeanClass<Videos>> getVideoList(/*@Body RequestBody requestBody,*/
//                                                   @Query("page") int page,
//                                                   @Query("limit") int limit);
//
//    /**
//     * 编辑用户信息
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param nickname
//     * @param birthday
//     * @param alipay
//     * @param sex
//     * @param headicon
//     * @param intro
//     * @return
//     */
//    @POST("api/member/mEditInfo")
//    Observable<BaseBean> modUserInfo(@Query("app_m_id") String app_m_id,
//                                     @Query("app_p_token") String app_p_token,
//                                     @Query("nickname") String nickname,
//                                     @Query("birthday") String birthday,
//                                     @Query("alipay") String alipay,
//                                     @Query("sex") String sex,
//                                     @Query("headicon") String headicon,
//                                     @Query("intro") String intro);
//
//    /**
//     * 上传头像
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param file
//     * @return
//     */
//    @POST("api/member/mEditHeadIcon ")
//    @Multipart
//    Observable<BaseBeanClass<HeadInfo>> uploadHead(@Query("app_m_id") String app_m_id,
//                                                   @Query("app_p_token") String app_p_token,
//                                                   @Part("headIcon\"; filename=\"pic.png\"") RequestBody file
//    );
//
//    /**
//     * 退出登录
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @return
//     */
//    @POST("api/member/mLogout")
//    Observable<BaseBean> loginOut(@Query("app_m_id") String app_m_id,
//                                  @Query("app_p_token") String app_p_token);
//
//    /**
//     * 关注用户
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param isAttention  是否关注(1:关注,0:取消关注)
//     * @param newsMemberId 发表新闻用户ID或者其他用户ID
//     * @return
//     */
//    @POST("api/relation/mIsAttention")
//    Observable<BaseBean> follow(@Query("app_m_id") String app_m_id,
//                                @Query("app_p_token") String app_p_token,
//                                @Query("isAttention") int isAttention,
//                                @Query("newsMemberId") String newsMemberId);
//
//    /**
//     * 获取发现数据
//     *
//     * @return
//     */
//    @POST("api/found/index")
//    Observable<BaseBeanClass<FindList>> findData();
//
//    /**
//     * 更多热门游戏
//     *
//     * @param page
//     * @param limit
//     * @return
//     */
//    @POST("api/found/moreGames")
//    Observable<BaseBeanClass<GameMoreList>> gameMore(@Query("page") int page,
//                                                     @Query("limit") int limit);
//
//    /**
//     * 更多主播
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param gameCategoryId
//     * @param page
//     * @param limit
//     * @return
//     */
//    @POST("api/found/moreRecAnchor ")
//    Observable<BaseBeanClass<BaseList>> anchorMore(@Query("app_m_id") String app_m_id,
//                                                   @Query("app_p_token") String app_p_token,
//                                                   @Query("gameCategoryId") String gameCategoryId,
//                                                   @Query("page") int page,
//                                                   @Query("limit") int limit);
//
//    /**
//     * 获取热门搜索
//     *
//     * @return
//     */
//    @POST("api/search/mInitSearchHotKey")
//    Observable<BaseBeanClass<SearchHotKeyList>> searchKey();
//
//    /**
//     * 搜索
//     *
//     * @param app_m_id    用户ID
//     * @param app_p_token AppToken
//     * @param keyWord     关键字
//     * @param keyType     类型
//     * @param page        第几页,默认第1页
//     * @param limit       每页几条,默认10条
//     * @return
//     */
//    @POST("api/search/mGetSearch")
//    Observable<BaseBeanClass<SearchList>> search(@Query("app_m_id") String app_m_id,
//                                                 @Query("app_p_token") String app_p_token,
//                                                 @Query("keyWord") String keyWord,
//                                                 @Query("keyType") int keyType,
//                                                 @Query("page") int page,
//                                                 @Query("limit") int limit
//    );
//
//    /**
//     * 意见反馈
//     *
//     * @return
//     */
//    @POST("api/feedback/mAdd ")
//    Observable<BaseBean> feedBack(@Body MultipartBody file);
//
//    /**
//     * 发现-游戏列表-最新，攻略
//     *
//     * @param page             第几页,默认第1页
//     * @param limit            每页几条,默认10条
//     * @param gameCategoryCode 游戏分类Code
//     * @param newsType         视频分类(最新传0,攻略传3)
//     * @return
//     */
//    @POST("api/found/getNewestNews")
//    Observable<BaseBeanClass<News>> gameNews(@Query("page") int page,
//                                             @Query("limit") int limit,
//                                             @Query("gameCategoryCode") String gameCategoryCode,
//                                             @Query("newsType") int newsType
//    );
//
//    /**
//     * 发现-游戏列表-视频
//     *
//     * @param page
//     * @param limit
//     * @return
//     */
//    @POST("api/found/getGameVideoList")
//    Observable<BaseBeanClass<Videos>> getGameVideoList(/*@Body RequestBody requestBody,*/
//                                                       @Query("app_m_id") String app_m_id,
//                                                       @Query("app_p_token") String app_p_token,
//                                                       @Query("page") int page,
//                                                       @Query("limit") int limit,
//                                                       @Query("gameCategoryCode") String gameCategoryCode,
//                                                       @Query("newsType") int newsType);
//
//    /**
//     * 获取评论
//     *
//     * @return
//     */
//    Observable<BaseBean> getComment();
//
//    /**
//     * 添加评论
//     *
//     * @param app_m_id    用户ID
//     * @param app_p_token AppToken
//     * @param newsId      资讯ID
//     * @param newsType    资讯类型
//     * @param content     评论内容
//     * @param toMid       对某人ID评论
//     * @param parentId    评论根级ID
//     * @return
//     */
//    @POST("api/newsDetail/addComment")
//    Observable<BaseBean> addComment(@Query("app_m_id") String app_m_id,
//                                    @Query("app_p_token") String app_p_token,
//                                    @Query("newsId") String newsId,
//                                    @Query("newsType") int newsType,
//                                    @Query("content") String content,
//                                    @Query("toMid") String toMid,
//                                    @Query("parentId") String parentId
//    );
//
//    /**
//     * 获取资讯详情
//     *
//     * @param app_m_id     用户ID
//     * @param app_p_token  AppToken
//     * @param newsId       资讯ID
//     * @param categoryCode 游戏分类
//     * @param newsType     资讯类型
//     * @return
//     */
//    @POST("api/newsDetail/mGetNewsDetail")
//    Observable<BaseBeanClass<NewsDetailInfo>> getNewsDetail(@Query("app_m_id") String app_m_id,
//                                                            @Query("app_p_token") String app_p_token,
//                                                            @Query("newsId") String newsId,
//                                                            @Query("categoryCode") String categoryCode,
//                                                            @Query("newsType") int newsType
//    );
//
//    /**
//     * 获取用户粉丝列表
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param page        第几页,默认第1页
//     * @param limit       每页几条,默认10条
//     * @return
//     */
//    @POST("api/relation/mGetFansList")
//    Observable<BaseBeanClass<BaseList>> getFansList(@Query("app_m_id") String app_m_id,
//                                                    @Query("app_p_token") String app_p_token,
//                                                    @Query("page") int page,
//                                                    @Query("limit") int limit
//    );
//
//    /**
//     * 获取用户收藏列表
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param page        第几页,默认第1页
//     * @param limit       每页几条,默认10条
//     * @return
//     */
//    @POST("api/collect/mGetList")
//    Observable<BaseBeanClass<BaseList>> getCollectionList(@Query("app_m_id") String app_m_id,
//                                                          @Query("app_p_token") String app_p_token,
//                                                          @Query("page") int page,
//                                                          @Query("limit") int limit);
//
//    /**
//     * 获取用户关注列表
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param page        第几页,默认第1页
//     * @param limit       每页几条,默认10条
//     * @return
//     */
//    @POST("api/relation/mGetAttentionList")
//    Observable<BaseBeanClass<BaseList>> getAttentionList(@Query("app_m_id") String app_m_id,
//                                                         @Query("app_p_token") String app_p_token,
//                                                         @Query("page") int page,
//                                                         @Query("limit") int limit
//    );
//
//
//    /**
//     * 用户收藏资讯-收藏/取消收藏
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param isCollect   是否收藏(1:收藏,0:取消收藏)
//     * @param newsId      资讯ID
//     * @return
//     */
//    @POST("api/collect/mIsCollect")
//    Observable<BaseBean> mIsCollect(@Query("app_m_id") String app_m_id,
//                                    @Query("app_p_token") String app_p_token,
//                                    @Query("isCollect") int isCollect,
//                                    @Query("newsId") String newsId
//    );
//
//    /**
//     * 获取用户通知列表
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param page
//     * @param limit
//     * @return
//     */
//    @POST("api/notice/mGetNoticeList")
//    Observable<BaseBeanClass<BaseList>> getNoticeList(@Query("app_m_id") String app_m_id,
//                                                      @Query("app_p_token") String app_p_token,
//                                                      @Query("page") int page,
//                                                      @Query("limit") int limit);
//
//
//    /**
//     * 删除通知
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param noticeId    通知ID
//     * @return
//     */
//    @POST("api/notice/mDelNotice")
//    Observable<BaseBean> delNotice(@Query("app_m_id") String app_m_id,
//                                   @Query("app_p_token") String app_p_token,
//                                   @Query("noticeId") String noticeId);
//
//
//    /**
//     * 获取其他用户粉丝列表
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param page        第几页,默认第1页
//     * @param limit       每页几条,默认10条
//     * @return
//     */
//    @POST("api/relation/mGetOtherMemberFansList")
//    Observable<BaseBeanClass<BaseList>> getOtherFansList(@Query("app_m_id") String app_m_id,
//                                                         @Query("app_p_token") String app_p_token,
//                                                         @Query("page") int page,
//                                                         @Query("limit") int limit,
//                                                         @Query("newsMemberId") String newsMemberId
//    );
//
//    /**
//     * 获取其他用户关注列表
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param page        第几页,默认第1页
//     * @param limit       每页几条,默认10条
//     * @return
//     */
//    @POST("api/relation/mGetOtherMemberAttentionList")
//    Observable<BaseBeanClass<BaseList>> getOtherAttentionList(@Query("app_m_id") String app_m_id,
//                                                              @Query("app_p_token") String app_p_token,
//                                                              @Query("page") int page,
//                                                              @Query("limit") int limit,
//                                                              @Query("newsMemberId") String newsMemberId
//    );
//
//    /**
//     * 获取其他用户的信息，粉丝数、关注数
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @return
//     */
//    @POST("api/relation/mGetOtherMemberInfo")
//    Observable<BaseBeanClass<BaseList>> getOtherMemberInfo(@Query("app_m_id") String app_m_id,
//                                                           @Query("app_p_token") String app_p_token,
//                                                           @Query("newsMemberId") String newsMemberId
//    );
//
//    /**
//     * 获取其他用户的文章视频列表
//     *
//     * @param page
//     * @param limit
//     * @param newsMemberId 其他用户ID
//     * @param type         1：文章或攻略 2视频
//     * @return
//     */
//    @POST("api/relation/mGetOtherMemberNewsList")
//    Observable<BaseBeanClass<News>> getOtherMemberNewsList(@Query("page") int page,
//                                                           @Query("limit") int limit,
//                                                           @Query("newsMemberId") String newsMemberId,
//                                                           @Query("type") int type
//    );
//
//    /**
//     * 获取感兴趣游戏列表(登陆后)
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param page
//     * @param limit
//     * @return
//     */
//    @POST("api/home/getLikeGameList")
//    Observable<BaseBeanClass<BaseList>> getLikeGameList(@Query("app_m_id") String app_m_id,
//                                                        @Query("app_p_token") String app_p_token,
//                                                        @Query("page") int page,
//                                                        @Query("limit") int limit);
//
//    /**
//     * 用户新增或删除喜欢的游戏
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @param type             类型(1添加 0 删除)
//     * @param gameCategoryCode 游戏分类Code
//     * @return
//     */
//    @POST("api/member/mAddOrDelLikeGame")
//    Observable<BaseBean> addOrDelLikeGame(@Query("app_m_id") String app_m_id,
//                                          @Query("app_p_token") String app_p_token,
//                                          @Query("type") int type,
//                                          @Query("gameCategoryCode") String gameCategoryCode);
//
//    /**
//     * 获取用户喜欢的游戏
//     *
//     * @param app_m_id
//     * @param app_p_token
//     * @return
//     */
//    @POST("api/member/mGetLikeGame")
//    Observable<BaseBeanClass<BaseList>> mGetLikeGameList(@Query("app_m_id") String app_m_id,
//                                                         @Query("app_p_token") String app_p_token);
//
//
//    @POST("xxx")
//    Observable<Object> test(@Body RequestBody file);

    @POST("front/")
    Observable<InitInfo> init(@Body RequestBody file);

    /**
     * 下单接口
     *
     * @param file
     * @return
     */
    @POST("front/")
    Observable<OrderInfo> prepOrder(@Body RequestBody file);

    /**
     * 订单查询
     *
     * @param file
     * @return
     */
    @POST("front/")
    Observable<OrderInfo> queryOrder(@Body RequestBody file);

    /**
     * 出票状态更新
     *
     * @param file
     * @return
     */
    @POST("front/")
    Observable<BaseBean> outTicket(@Body RequestBody file);

    /**
     * 终端状态同步
     *
     * @param file
     * @return
     */
    @POST("front/")
    Observable<BaseBean> terminalUpdate(@Body RequestBody file);

}
