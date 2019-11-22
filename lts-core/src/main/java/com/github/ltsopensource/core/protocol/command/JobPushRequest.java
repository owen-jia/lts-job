package com.github.ltsopensource.core.protocol.command;

import com.github.ltsopensource.core.domain.JobMeta;
import com.github.ltsopensource.remoting.annotation.NotNull;

import java.util.List;

/**
 * @author Robert HG (254963746@qq.com) on 8/14/14.
 */
public class JobPushRequest extends AbstractRemotingCommandBody {

	private static final long serialVersionUID = 2986743693237022215L;
	
	@NotNull
    private JobMeta jobMeta;

	//兼容1.7.0 server, at 2019-11-21
    private List<JobMeta> jobMetaList;

    public JobMeta getJobMeta() {
        return jobMeta;
    }

    public void setJobMeta(JobMeta jobMeta) {
        this.jobMeta = jobMeta;
    }

    public List<JobMeta> getJobMetaList() {
        return jobMetaList;
    }

    public void setJobMetaList(List<JobMeta> jobMetaList) {
        this.jobMetaList = jobMetaList;
    }
}
