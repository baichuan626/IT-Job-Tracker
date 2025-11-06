package com.itjobtracker;

import com.itjobtracker.controller.JobPostingController;

public class Main {
    public static void main(String[] args) {
        System.out.println("欢迎使用IT岗位求职记录系统！");
        JobPostingController controller = new JobPostingController();
        controller.showMainMenu();
    }
}