package com.computer.mazhihuapp.utils.net;

import com.computer.mazhihuapp.model.AppInfo;

import java.io.File;

public class DownloadInfo {

	private long id;
	private String appName;
	private long appSize = 0;
	private long currentSize = 0;
	private int downloadState = 0;
	private String url;
	private String path;
	private boolean  hasFinished=true;
	public static DownloadInfo clone(AppInfo info) {
		DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.id = info.getId();
		downloadInfo.appName = info.getName();
		downloadInfo.appSize = info.getSize();
		downloadInfo.currentSize = 0;
		downloadInfo.downloadState = DownloadManager.STATE_NONE;
		downloadInfo.url = info.getDown_url();
		downloadInfo.path = FileUtil.getDownloadDir(AppUtil.getContext()) + File.separator + downloadInfo.appName + ".apk";
		return downloadInfo;
	}
	
	

	public boolean isHasFinished() {
		return hasFinished;
	}



	public void setHasFinished(boolean hasFinished) {
		this.hasFinished = hasFinished;
	}



	public String getPath() {
		return path;
	}

	public float getProgress() {
		if (getAppSize() == 0) {
			return 0;
		}
		return (getCurrentSize() + 0.0f) / getAppSize();
	}

	public synchronized String getUrl() {
		return url;
	}

	public synchronized void setUrl(String url) {
		this.url = url;
	}

	public synchronized long getId() {
		return id;
	}

	public synchronized void setId(long id) {
		this.id = id;
	}

	public synchronized String getAppName() {
		return appName;
	}

	public synchronized void setAppName(String appName) {
		this.appName = appName;
	}

	public synchronized long getAppSize() {
		return appSize;
	}

	public synchronized void setAppSize(long appSize) {
		this.appSize = appSize;
	}

	public synchronized long getCurrentSize() {
		return currentSize;
	}

	public synchronized void setCurrentSize(long currentSize) {
		this.currentSize = currentSize;
	}

	public synchronized int getDownloadState() {
		return downloadState;
	}

	public void setDownloadState(int downloadState) {
		this.downloadState = downloadState;
	}
}
