/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.entity.bo;

import com.google.gson.JsonObject;
import com.situ.tools.DataSwitch;
import com.situ.tools.ObjectUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息
 *
 * @author 司徒彬
 * @date 2020/7/24 10:58
 */
@Getter
@Setter
public class UserInfo {
    private String uKey;
    private Long userId;
    private String name;
    private String userName;
    private String type;
    private String phone;
    private String sex;
    private Boolean focusGift;
    //老用户调研
    private Boolean customerResearch1;
    private String openId;
    private String userCode;
    private String headerUrl;
    private Boolean focusOfficialAccounts;
    private Integer taskCount;
    private Integer rechargeCount;
    private Boolean hiddenSpread;

    private String permissionCodes;
    private String requestIp;

    private String requestUrl;

    private PayloadEntity payloadEntity;
    private String unionId;

    private Long loginTime = 0L;
    private Long lastOperationTime = 0L;
    private Boolean register = false;
    private Boolean showExample = false;
    private Boolean showGuideInDesktop = false;
    private Boolean showGuideInViews = false;
    private Boolean showGuideInAdd = false;

    @Override
    public String toString() {
        if (ObjectUtils.isNull(this)) {
            return "";
        }
        return "UserInfo{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", type='" + type + '\'' +
                ", permissionCodes='" + permissionCodes + '\'' +
                ", requestIp='" + requestIp + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", payloadEntity=" + payloadEntity +
                ", loginTime=" + loginTime +
                ", lastOperationTime=" + lastOperationTime +
                '}';
    }

    private String invitationCode;

    public JsonObject getLoginInfo() {
        JsonObject object = new JsonObject();
        object.addProperty("phone", this.getPhone());
        object.addProperty("loginName", this.getPhone());
        object.addProperty("sex", this.getSex());
        object.addProperty("type", this.getType());
        object.addProperty("name", this.getName());
        object.addProperty("userId", DataSwitch.convertObjectToString(this.getUserId()));
        object.addProperty("userCode", this.getUserCode());
        object.addProperty("focusGift", ObjectUtils.isNull(this.getFocusGift()) ? false : this.getFocusGift());
        object.addProperty("customerResearch1", ObjectUtils.isNull(this.getCustomerResearch1()) ?
                false : this.getCustomerResearch1());
        object.addProperty("focusOfficialAccounts", this.getFocusOfficialAccounts());
        object.addProperty("openId", this.getOpenId());
        object.addProperty("ukey", this.getUKey());
        object.addProperty("register", this.getRegister());
        object.addProperty("headerUrl", headerUrl);
        object.addProperty("showExample", showExample);
        object.addProperty("showGuideInDesktop", showGuideInDesktop);
        object.addProperty("showGuideInViews", showGuideInViews);
        object.addProperty("showGuideInAdd", showGuideInAdd);
        object.addProperty("taskCount", taskCount);
        object.addProperty("rechargeCount", rechargeCount);
        object.addProperty("unionId", unionId);
        object.addProperty("hiddenSpread", hiddenSpread);
        object.addProperty("invitationCode",invitationCode);
        return object;
    }

    public void addTaskCount() {
        if (taskCount == null) {
            taskCount = 0;
        }
        taskCount = taskCount + 1;
    }
}
