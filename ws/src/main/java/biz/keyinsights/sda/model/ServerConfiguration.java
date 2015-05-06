package biz.keyinsights.sda.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServerConfiguration implements Configuration {
	private Integer numberOfGpus;
	private Integer numberOfProcessors;
	private String accessLogDirectory;
	private String dataDirectory;
	
	public Integer getNumberOfGpus() {
		return numberOfGpus;
	}
	public void setNumberOfGpus(Integer numberOfGpus) {
		this.numberOfGpus = numberOfGpus;
	}
	public Integer getNumberOfProcessors() {
		return numberOfProcessors;
	}
	public void setNumberOfProcessors(Integer numberOfProcessors) {
		this.numberOfProcessors = numberOfProcessors;
	}
	public String getAccessLogDirectory() {
		return accessLogDirectory;
	}
	public void setAccessLogDirectory(String accessLogDirectory) {
		this.accessLogDirectory = accessLogDirectory;
	}
	public String getDataDirectory() {
		return dataDirectory;
	}
	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}
}
