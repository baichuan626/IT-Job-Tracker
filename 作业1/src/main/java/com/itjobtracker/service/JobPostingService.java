package com.itjobtracker.service;

import com.itjobtracker.dao.JobPostingDAO;
import com.itjobtracker.model.JobPosting;

import java.util.List;

public class JobPostingService {
    private JobPostingDAO jobPostingDAO;

    public JobPostingService() {
        this.jobPostingDAO = new JobPostingDAO();
    }

    // 添加岗位
    public boolean addJobPosting(JobPosting jobPosting) {
        // 验证必填字段
        if (jobPosting.getCompanyName() == null || jobPosting.getCompanyName().trim().isEmpty()) {
            System.out.println("企业名称不能为空");
            return false;
        }
        if (jobPosting.getJobTitle() == null || jobPosting.getJobTitle().trim().isEmpty()) {
            System.out.println("岗位名称不能为空");
            return false;
        }
        // 设置默认状态
        if (jobPosting.getStatus() == null || jobPosting.getStatus().trim().isEmpty()) {
            jobPosting.setStatus("未申请");
        }
        return jobPostingDAO.addJobPosting(jobPosting);
    }

    // 获取所有岗位
    public List<JobPosting> getAllJobPostings() {
        return jobPostingDAO.getAllJobPostings();
    }

    // 根据ID获取岗位
    public JobPosting getJobPostingById(int id) {
        return jobPostingDAO.getJobPostingById(id);
    }

    // 更新岗位
    public boolean updateJobPosting(JobPosting jobPosting) {
        // 验证岗位是否存在
        JobPosting existingJob = getJobPostingById(jobPosting.getId());
        if (existingJob == null) {
            System.out.println("岗位不存在");
            return false;
        }
        // 验证必填字段
        if (jobPosting.getCompanyName() == null || jobPosting.getCompanyName().trim().isEmpty()) {
            System.out.println("企业名称不能为空");
            return false;
        }
        if (jobPosting.getJobTitle() == null || jobPosting.getJobTitle().trim().isEmpty()) {
            System.out.println("岗位名称不能为空");
            return false;
        }
        return jobPostingDAO.updateJobPosting(jobPosting);
    }

    // 删除岗位
    public boolean deleteJobPosting(int id) {
        // 验证岗位是否存在
        JobPosting existingJob = getJobPostingById(id);
        if (existingJob == null) {
            System.out.println("岗位不存在");
            return false;
        }
        return jobPostingDAO.deleteJobPosting(id);
    }

    // 筛选查询岗位
    public List<JobPosting> searchJobPostings(String companyName, String jobTitle, String status) {
        return jobPostingDAO.searchJobPostings(companyName, jobTitle, status);
    }
}