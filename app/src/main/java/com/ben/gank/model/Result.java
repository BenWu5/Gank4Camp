
package com.ben.gank.model;

import com.ben.gank.utils.MyDateUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;

public class Result implements Serializable{

    @SerializedName("who")
    @Expose
    private String who;
    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("used")
    @Expose
    private boolean used;
    @SerializedName("objectId")
    @Expose
    private String objectId;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    private DateTime publishedDateTime;
    /**
     * 
     * @return
     *     The who
     */
    public String getWho() {
        return who;
    }

    /**
     * 
     * @param who
     *     The who
     */
    public void setWho(String who) {
        this.who = who;
    }

    /**
     * 
     * @return
     *     The publishedAt
     */
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * 
     * @param publishedAt
     *     The publishedAt
     */
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * 
     * @return
     *     The desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 
     * @param desc
     *     The desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return
     *     The used
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * 
     * @param used
     *     The used
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * 
     * @return
     *     The objectId
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * 
     * @param objectId
     *     The objectId
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The createdAt
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 
     * @param updatedAt
     *     The updatedAt
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DateTime getPublishedDataTime(){
        if(publishedDateTime==null){
            publishedDateTime = MyDateUtils.formatDateFromStr(publishedAt);
        }
        return publishedDateTime;
    }

}
