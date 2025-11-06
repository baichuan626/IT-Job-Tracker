package com.itjobtracker.dao;

import com.itjobtracker.model.JobPosting;
import com.itjobtracker.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobPostingDAO {
    // 添加岗位
    public boolean addJobPosting(JobPosting jobPosting) {
        String sql = "INSERT INTO job_postings (company_name, job_title, salary, requirements, location, post_date, status, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, jobPosting.getCompanyName());
            pstmt.setString(2, jobPosting.getJobTitle());
            pstmt.setString(3, jobPosting.getSalary());
            pstmt.setString(4, jobPosting.getRequirements());
            pstmt.setString(5, jobPosting.getLocation());
            if (jobPosting.getPostDate() != null) {
                pstmt.setDate(6, new java.sql.Date(jobPosting.getPostDate().getTime()));
            } else {
                pstmt.setNull(6, Types.DATE);
            }
            pstmt.setString(7, jobPosting.getStatus());
            pstmt.setString(8, jobPosting.getNotes());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 获取所有岗位
    public List<JobPosting> getAllJobPostings() {
        List<JobPosting> jobPostings = new ArrayList<>();
        String sql = "SELECT * FROM job_postings ORDER BY created_at DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                jobPostings.add(mapResultSetToJobPosting(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return jobPostings;
    }

    // 根据ID获取岗位
    public JobPosting getJobPostingById(int id) {
        String sql = "SELECT * FROM job_postings WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToJobPosting(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    // 更新岗位
    public boolean updateJobPosting(JobPosting jobPosting) {
        String sql = "UPDATE job_postings SET company_name = ?, job_title = ?, salary = ?, requirements = ?, " +
                     "location = ?, post_date = ?, status = ?, notes = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, jobPosting.getCompanyName());
            pstmt.setString(2, jobPosting.getJobTitle());
            pstmt.setString(3, jobPosting.getSalary());
            pstmt.setString(4, jobPosting.getRequirements());
            pstmt.setString(5, jobPosting.getLocation());
            if (jobPosting.getPostDate() != null) {
                pstmt.setDate(6, new java.sql.Date(jobPosting.getPostDate().getTime()));
            } else {
                pstmt.setNull(6, Types.DATE);
            }
            pstmt.setString(7, jobPosting.getStatus());
            pstmt.setString(8, jobPosting.getNotes());
            pstmt.setInt(9, jobPosting.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 删除岗位
    public boolean deleteJobPosting(int id) {
        String sql = "DELETE FROM job_postings WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 筛选查询
    public List<JobPosting> searchJobPostings(String companyName, String jobTitle, String status) {
        List<JobPosting> jobPostings = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM job_postings WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (companyName != null && !companyName.trim().isEmpty()) {
            sql.append(" AND company_name LIKE ?");
            params.add("%" + companyName + "%");
        }
        if (jobTitle != null && !jobTitle.trim().isEmpty()) {
            sql.append(" AND job_title LIKE ?");
            params.add("%" + jobTitle + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        
        sql.append(" ORDER BY created_at DESC");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                jobPostings.add(mapResultSetToJobPosting(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return jobPostings;
    }

    // 将ResultSet映射到JobPosting对象
    private JobPosting mapResultSetToJobPosting(ResultSet rs) throws SQLException {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setId(rs.getInt("id"));
        jobPosting.setCompanyName(rs.getString("company_name"));
        jobPosting.setJobTitle(rs.getString("job_title"));
        jobPosting.setSalary(rs.getString("salary"));
        jobPosting.setRequirements(rs.getString("requirements"));
        jobPosting.setLocation(rs.getString("location"));
        jobPosting.setPostDate(rs.getDate("post_date"));
        jobPosting.setStatus(rs.getString("status"));
        jobPosting.setNotes(rs.getString("notes"));
        jobPosting.setCreatedAt(rs.getTimestamp("created_at"));
        jobPosting.setUpdatedAt(rs.getTimestamp("updated_at"));
        return jobPosting;
    }
}