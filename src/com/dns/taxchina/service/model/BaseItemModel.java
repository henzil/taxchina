package com.dns.taxchina.service.model;import netlib.model.BaseModel;/** * @author henzil * @version create time:2014-4-24_下午2:39:33 * @Description 所有item的base model */public class BaseItemModel extends BaseModel {	private static final long serialVersionUID = 2364920285202253011L;	/**	 * "id": "ID", "title": "标题", "info": "副标题", "image": "图片", "isVideo":	 * "true" // true有视频 false 无视频	 */	private String id;	private String title;	private String info;	private String image;	private boolean isVideo;	public String getId() {		return id;	}	public void setId(String id) {		this.id = id;	}	public String getTitle() {		return title;	}	public void setTitle(String title) {		this.title = title;	}	public String getInfo() {		return info;	}	public void setInfo(String info) {		this.info = info;	}	public String getImage() {		return image;	}	public void setImage(String image) {		this.image = image;	}	public boolean isVideo() {		return isVideo;	}	public void setVideo(boolean isVideo) {		this.isVideo = isVideo;	}}