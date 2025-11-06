-- 创建数据库
CREATE DATABASE IF NOT EXISTS it_job_tracker;
USE it_job_tracker;

-- 创建岗位表
CREATE TABLE IF NOT EXISTS job_postings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL COMMENT '企业名称',
    job_title VARCHAR(255) NOT NULL COMMENT '岗位名称',
    salary VARCHAR(100) COMMENT '薪资',
    requirements TEXT COMMENT '具体要求',
    location VARCHAR(255) COMMENT '工作地点',
    post_date DATE COMMENT '发布日期',
    status VARCHAR(50) DEFAULT '未申请' COMMENT '状态：未申请、已申请、面试中、已录用、已拒绝',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 创建索引以提高查询性能
CREATE INDEX idx_company_name ON job_postings(company_name);
CREATE INDEX idx_job_title ON job_postings(job_title);
CREATE INDEX idx_status ON job_postings(status);
CREATE INDEX idx_post_date ON job_postings(post_date);

-- 插入示例数据
INSERT INTO job_postings (company_name, job_title, salary, requirements, location, post_date, status, notes) VALUES
('阿里巴巴', 'Java开发工程师', '25K-40K', '熟悉Java、Spring Boot、MySQL等技术栈，3年以上工作经验', '杭州', '2024-10-15', '未申请', '大厂机会，值得尝试'),
('腾讯', '前端开发工程师', '20K-35K', '精通HTML、CSS、JavaScript，熟悉React或Vue框架', '深圳', '2024-10-18', '已申请', '等待面试通知'),
('字节跳动', '数据分析师', '22K-38K', '熟悉SQL、Python，有数据分析经验', '北京', '2024-10-20', '面试中', '一面已通过'),
('百度', '后端开发工程师', '23K-36K', '熟悉Java、Go等语言，了解分布式系统', '北京', '2024-10-22', '未申请', '技术氛围好'),
('美团', '测试工程师', '18K-30K', '熟悉自动化测试，有性能测试经验', '北京', '2024-10-25', '未申请', '业务发展快');