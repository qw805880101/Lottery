package com.psylife.wrmvplibrary.utils.timeutils;

/**
 * Created by tc on 2017/7/21.
 */

public interface Auth {

    /**
     * 授权结果回调接口
     * @param authType   授权类型  （身份证 ID_CARD， 活体认证 LIVING_BODY）
     * @param isSuccess  是否成功
     */
    void isSuccess(AuthType authType, boolean isSuccess);

}
