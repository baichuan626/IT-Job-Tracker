package com.itjobtracker.controller;

import com.itjobtracker.model.JobPosting;
import com.itjobtracker.service.JobPostingService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class JobPostingController {
    private JobPostingService jobPostingService;
    private Scanner scanner;
    private SimpleDateFormat dateFormat;

    public JobPostingController() {
        this.jobPostingService = new JobPostingService();
        this.scanner = new Scanner(System.in);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    // 显示主菜单
    public void showMainMenu() {
        while (true) {
            System.out.println("===== IT岗位求职记录系统 =====");
            System.out.println("1. 添加岗位");
            System.out.println("2. 查看所有岗位");
            System.out.println("3. 搜索岗位");
            System.out.println("4. 更新岗位");
            System.out.println("5. 删除岗位");
            System.out.println("6. 退出系统");
            System.out.print("请选择操作 (1-6): ");
            
            int choice = -1;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // 消费换行符
            } catch (Exception e) {
                scanner.nextLine(); // 清空输入
                System.out.println("输入无效，请重新选择");
                continue;
            }
            
            switch (choice) {
                case 1:
                    addJobPosting();
                    break;
                case 2:
                    showAllJobPostings();
                    break;
                case 3:
                    searchJobPostings();
                    break;
                case 4:
                    updateJobPosting();
                    break;
                case 5:
                    deleteJobPosting();
                    break;
                case 6:
                    System.out.println("感谢使用，再见！");
                    scanner.close();
                    return;
                default:
                    System.out.println("选择无效，请重新输入");
            }
        }
    }

    // 添加岗位
    private void addJobPosting() {
        System.out.println("===== 添加新岗位 =====");
        
        System.out.print("企业名称: ");
        String companyName = scanner.nextLine();
        
        System.out.print("岗位名称: ");
        String jobTitle = scanner.nextLine();
        
        System.out.print("薪资: ");
        String salary = scanner.nextLine();
        
        System.out.print("具体要求: ");
        String requirements = scanner.nextLine();
        
        System.out.print("工作地点: ");
        String location = scanner.nextLine();
        
        Date postDate = null;
        System.out.print("发布日期 (yyyy-MM-dd，留空跳过): ");
        String postDateStr = scanner.nextLine();
        if (!postDateStr.trim().isEmpty()) {
            try {
                postDate = dateFormat.parse(postDateStr);
            } catch (ParseException e) {
                System.out.println("日期格式错误，使用默认值");
            }
        }
        
        System.out.print("状态 (未申请/已申请/面试中/已录用/已拒绝，默认未申请): ");
        String status = scanner.nextLine();
        if (status.trim().isEmpty()) {
            status = "未申请";
        }
        
        System.out.print("备注: ");
        String notes = scanner.nextLine();
        
        JobPosting jobPosting = new JobPosting(companyName, jobTitle, salary, requirements, 
                                             location, postDate, status, notes);
        
        if (jobPostingService.addJobPosting(jobPosting)) {
            System.out.println("岗位添加成功！");
        } else {
            System.out.println("岗位添加失败！");
        }
        
        pause();
    }

    // 查看所有岗位
    private void showAllJobPostings() {
        List<JobPosting> jobPostings = jobPostingService.getAllJobPostings();
        
        if (jobPostings.isEmpty()) {
            System.out.println("暂无岗位记录");
        } else {
            System.out.println("===== 所有岗位列表 =====");
            for (JobPosting job : jobPostings) {
                System.out.println("ID: " + job.getId());
                System.out.println("企业名称: " + job.getCompanyName());
                System.out.println("岗位名称: " + job.getJobTitle());
                System.out.println("薪资: " + (job.getSalary() != null ? job.getSalary() : "未提供"));
                System.out.println("工作地点: " + (job.getLocation() != null ? job.getLocation() : "未提供"));
                System.out.println("状态: " + job.getStatus());
                System.out.println("创建时间: " + job.getCreatedAt());
                System.out.println("----------------------------------------");
            }
        }
        
        pause();
    }

    // 搜索岗位
    private void searchJobPostings() {
        System.out.println("===== 搜索岗位 =====");
        
        System.out.print("企业名称 (留空跳过): ");
        String companyName = scanner.nextLine();
        
        System.out.print("岗位名称 (留空跳过): ");
        String jobTitle = scanner.nextLine();
        
        System.out.print("状态 (未申请/已申请/面试中/已录用/已拒绝，留空跳过): ");
        String status = scanner.nextLine();
        
        List<JobPosting> jobPostings = jobPostingService.searchJobPostings(companyName, jobTitle, status);
        
        if (jobPostings.isEmpty()) {
            System.out.println("未找到符合条件的岗位");
        } else {
            System.out.println("===== 搜索结果 =====");
            for (JobPosting job : jobPostings) {
                System.out.println("ID: " + job.getId());
                System.out.println("企业名称: " + job.getCompanyName());
                System.out.println("岗位名称: " + job.getJobTitle());
                System.out.println("薪资: " + (job.getSalary() != null ? job.getSalary() : "未提供"));
                System.out.println("工作地点: " + (job.getLocation() != null ? job.getLocation() : "未提供"));
                System.out.println("状态: " + job.getStatus());
                System.out.println("----------------------------------------");
            }
        }
        
        pause();
    }

    // 更新岗位
    private void updateJobPosting() {
        System.out.println("===== 更新岗位 =====");
        
        System.out.print("请输入要更新的岗位ID: ");
        int id = -1;
        try {
            id = scanner.nextInt();
            scanner.nextLine(); // 消费换行符
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("ID输入无效");
            pause();
            return;
        }
        
        JobPosting jobPosting = jobPostingService.getJobPostingById(id);
        if (jobPosting == null) {
            System.out.println("未找到该岗位");
            pause();
            return;
        }
        
        System.out.println("当前信息 (直接回车保留原值):");
        
        System.out.print("企业名称 [" + jobPosting.getCompanyName() + "]: ");
        String companyName = scanner.nextLine();
        if (!companyName.trim().isEmpty()) {
            jobPosting.setCompanyName(companyName);
        }
        
        System.out.print("岗位名称 [" + jobPosting.getJobTitle() + "]: ");
        String jobTitle = scanner.nextLine();
        if (!jobTitle.trim().isEmpty()) {
            jobPosting.setJobTitle(jobTitle);
        }
        
        System.out.print("薪资 [" + (jobPosting.getSalary() != null ? jobPosting.getSalary() : "未提供") + "]: ");
        String salary = scanner.nextLine();
        if (!salary.trim().isEmpty()) {
            jobPosting.setSalary(salary);
        }
        
        System.out.print("具体要求 [" + (jobPosting.getRequirements() != null ? jobPosting.getRequirements() : "无") + "]: ");
        String requirements = scanner.nextLine();
        if (!requirements.trim().isEmpty()) {
            jobPosting.setRequirements(requirements);
        }
        
        System.out.print("工作地点 [" + (jobPosting.getLocation() != null ? jobPosting.getLocation() : "未提供") + "]: ");
        String location = scanner.nextLine();
        if (!location.trim().isEmpty()) {
            jobPosting.setLocation(location);
        }
        
        System.out.print("发布日期 (yyyy-MM-dd) [" + (jobPosting.getPostDate() != null ? dateFormat.format(jobPosting.getPostDate()) : "未设置") + "]: ");
        String postDateStr = scanner.nextLine();
        if (!postDateStr.trim().isEmpty()) {
            try {
                jobPosting.setPostDate(dateFormat.parse(postDateStr));
            } catch (ParseException e) {
                System.out.println("日期格式错误，保留原值");
            }
        }
        
        System.out.print("状态 [" + jobPosting.getStatus() + "]: ");
        String status = scanner.nextLine();
        if (!status.trim().isEmpty()) {
            jobPosting.setStatus(status);
        }
        
        System.out.print("备注 [" + (jobPosting.getNotes() != null ? jobPosting.getNotes() : "无") + "]: ");
        String notes = scanner.nextLine();
        if (!notes.trim().isEmpty()) {
            jobPosting.setNotes(notes);
        }
        
        if (jobPostingService.updateJobPosting(jobPosting)) {
            System.out.println("岗位更新成功！");
        } else {
            System.out.println("岗位更新失败！");
        }
        
        pause();
    }

    // 删除岗位
    private void deleteJobPosting() {
        System.out.println("===== 删除岗位 =====");
        
        System.out.print("请输入要删除的岗位ID: ");
        int id = -1;
        try {
            id = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("ID输入无效");
            pause();
            return;
        }
        
        JobPosting jobPosting = jobPostingService.getJobPostingById(id);
        if (jobPosting == null) {
            System.out.println("未找到该岗位");
            pause();
            return;
        }
        
        System.out.println("确认删除以下岗位?");
        System.out.println("企业名称: " + jobPosting.getCompanyName());
        System.out.println("岗位名称: " + jobPosting.getJobTitle());
        System.out.print("输入 Y 确认删除，其他取消: ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("Y")) {
            if (jobPostingService.deleteJobPosting(id)) {
                System.out.println("岗位删除成功！");
            } else {
                System.out.println("岗位删除失败！");
            }
        } else {
            System.out.println("已取消删除");
        }
        
        pause();
    }

    // 暂停并等待用户按回车
    private void pause() {
        System.out.println("按回车键继续...");
        scanner.nextLine();
    }
}