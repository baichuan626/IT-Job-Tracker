// 等待DOM加载完成
document.addEventListener('DOMContentLoaded', function() {
    // DOM元素获取 - 添加错误处理
    const navLinks = document.querySelectorAll('.nav a') || [];
    const sections = document.querySelectorAll('.section') || [];
    const jobTableBody = document.getElementById('job-table-body');
    const addJobForm = document.getElementById('add-job-form');
    const editJobForm = document.getElementById('edit-job-form');
    const modal = document.getElementById('modal');
    const confirmModal = document.getElementById('confirm-modal');
    const closeModalBtn = document.getElementById('close-modal');
    const closeConfirmBtn = document.getElementById('close-confirm');
    const confirmCancelBtn = document.getElementById('confirm-cancel');
    const confirmOkBtn = document.getElementById('confirm-ok');
    const deleteJobBtn = document.getElementById('delete-job-btn');
    const refreshBtn = document.getElementById('refresh-btn');
    const searchBtn = document.getElementById('search-btn');
    const clearSearchBtn = document.getElementById('clear-search');
    const searchCompany = document.getElementById('search-company');
    const searchTitle = document.getElementById('search-title');
    const searchStatus = document.getElementById('search-status');
    const messageElement = document.getElementById('message') || createMessageElement();
    const activityList = document.getElementById('activity-list') || createActivityListElement();
    
    // 创建消息元素（如果不存在）
    function createMessageElement() {
        const msg = document.createElement('div');
        msg.id = 'message';
        msg.className = 'message';
        document.body.appendChild(msg);
        return msg;
    }
    
    // 创建活动列表元素（如果不存在）
    function createActivityListElement() {
        const list = document.createElement('ul');
        list.id = 'activity-list';
        return list;
    }
    
    // 全局变量
    let currentJobId = null;
    let deleteJobId = null;
    let statusChart = null;
    
    // 检查必要元素是否存在
    if (!jobTableBody || !addJobForm || !editJobForm || !modal || !confirmModal) {
        console.error('缺少必要的DOM元素，系统可能无法正常工作');
        alert('页面结构不完整，某些功能可能无法使用');
    }
    
    // 初始化数据
    initializeData();
    
    // 初始化导航切换
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href').substring(1);
            
            // 更新导航样式
            navLinks.forEach(navLink => navLink.classList.remove('active'));
            this.classList.add('active');
            
            // 显示对应区域
            sections.forEach(section => section.classList.remove('active-section'));
            document.getElementById(targetId).classList.add('active-section');
            
            // 如果切换到统计页面，更新图表
            if (targetId === 'statistics') {
                try {
                    updateChart();
                } catch (error) {
                    console.error('创建图表失败:', error);
                    showMessage('图表加载失败', 'warning');
                }
            }
        });
    });
    
    // 刷新岗位列表
    refreshBtn.addEventListener('click', function() {
        loadJobList();
        showMessage('刷新成功', 'success');
    });
    
    // 搜索功能
    searchBtn.addEventListener('click', function() {
        const company = searchCompany.value.trim();
        const title = searchTitle.value.trim();
        const status = searchStatus.value;
        searchJobs(company, title, status);
    });
    
    // 清空搜索
    clearSearchBtn.addEventListener('click', function() {
        searchCompany.value = '';
        searchTitle.value = '';
        searchStatus.value = '';
        loadJobList();
    });
    
    // 添加岗位表单提交
    addJobForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const jobData = {
            id: Date.now(), // 使用时间戳作为唯一ID
            companyName: document.getElementById('company-name').value.trim(),
            jobTitle: document.getElementById('job-title').value.trim(),
            salary: document.getElementById('salary').value.trim(),
            requirements: document.getElementById('requirements').value.trim(),
            location: document.getElementById('location').value.trim(),
            postDate: document.getElementById('post-date').value || null,
            status: document.getElementById('status').value,
            notes: document.getElementById('notes').value.trim(),
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
        };
        
        if (addJob(jobData)) {
            addJobForm.reset();
            loadJobList();
            updateDashboardStats();
            showMessage('岗位添加成功！', 'success');
            addActivity(`添加了岗位: ${jobData.companyName} - ${jobData.jobTitle}`);
        } else {
            showMessage('岗位添加失败！', 'error');
        }
    });
    
    // 编辑岗位表单提交
    editJobForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const jobData = {
            id: parseInt(document.getElementById('edit-id').value),
            companyName: document.getElementById('edit-company-name').value.trim(),
            jobTitle: document.getElementById('edit-job-title').value.trim(),
            salary: document.getElementById('edit-salary').value.trim(),
            requirements: document.getElementById('edit-requirements').value.trim(),
            location: document.getElementById('edit-location').value.trim(),
            postDate: document.getElementById('edit-post-date').value || null,
            status: document.getElementById('edit-status').value,
            notes: document.getElementById('edit-notes').value.trim(),
            updatedAt: new Date().toISOString()
        };
        
        if (updateJob(jobData)) {
            closeModal();
            loadJobList();
            updateDashboardStats();
            showMessage('岗位更新成功！', 'success');
            addActivity(`更新了岗位: ${jobData.companyName} - ${jobData.jobTitle}`);
        } else {
            showMessage('岗位更新失败！', 'error');
        }
    });
    
    // 删除岗位按钮
    deleteJobBtn.addEventListener('click', function() {
        deleteJobId = currentJobId;
        const job = getJobById(deleteJobId);
        if (job) {
            document.getElementById('confirm-message').textContent = 
                `确定要删除岗位「${job.companyName} - ${job.jobTitle}」吗？`;
            openConfirmModal();
        }
    });
    
    // 确认删除
    confirmOkBtn.addEventListener('click', function() {
        if (deleteJob(deleteJobId)) {
            closeModal();
            closeConfirmModal();
            loadJobList();
            updateDashboardStats();
            showMessage('岗位删除成功！', 'success');
            addActivity(`删除了岗位ID: ${deleteJobId}`);
        } else {
            showMessage('岗位删除失败！', 'error');
            closeConfirmModal();
        }
    });
    
    // 关闭模态框
    closeModalBtn.addEventListener('click', closeModal);
    closeConfirmBtn.addEventListener('click', closeConfirmModal);
    confirmCancelBtn.addEventListener('click', closeConfirmModal);
    
    // 点击模态框外部关闭
    modal.addEventListener('click', function(e) {
        if (e.target === modal) closeModal();
    });
    
    confirmModal.addEventListener('click', function(e) {
        if (e.target === confirmModal) closeConfirmModal();
    });
    
    // 初始化数据
    function initializeData() {
        try {
            let jobs = localStorage.getItem('jobs');
            if (!jobs) {
                const initialJobs = [
                    {
                        id: 1,
                        companyName: '阿里巴巴',
                        jobTitle: 'Java开发工程师',
                        salary: '25K-40K',
                        requirements: '熟悉Java、Spring Boot、MySQL等技术栈，3年以上工作经验',
                        location: '杭州',
                        postDate: '2024-10-15',
                        status: '未申请',
                        notes: '大厂机会，值得尝试',
                        createdAt: new Date('2024-10-15').toISOString(),
                        updatedAt: new Date('2024-10-15').toISOString()
                    },
                    {
                        id: 2,
                        companyName: '腾讯',
                        jobTitle: '前端开发工程师',
                        salary: '20K-35K',
                        requirements: '精通HTML、CSS、JavaScript，熟悉React或Vue框架',
                        location: '深圳',
                        postDate: '2024-10-18',
                        status: '已申请',
                        notes: '等待面试通知',
                        createdAt: new Date('2024-10-18').toISOString(),
                        updatedAt: new Date('2024-10-18').toISOString()
                    },
                    {
                        id: 3,
                        companyName: '字节跳动',
                        jobTitle: '数据分析师',
                        salary: '22K-38K',
                        requirements: '熟悉SQL、Python，有数据分析经验',
                        location: '北京',
                        postDate: '2024-10-20',
                        status: '面试中',
                        notes: '一面已通过',
                        createdAt: new Date('2024-10-20').toISOString(),
                        updatedAt: new Date('2024-10-20').toISOString()
                    }
                ];
                localStorage.setItem('jobs', JSON.stringify(initialJobs));
            }
            
            let activities = localStorage.getItem('activities');
            if (!activities) {
                localStorage.setItem('activities', JSON.stringify([
                    '系统初始化完成',
                    '导入了初始岗位数据'
                ]));
            }
            
            // 加载初始数据
            loadJobList();
            updateDashboardStats();
            loadActivities();
        } catch (error) {
            console.error('初始化数据失败:', error);
            showMessage('系统初始化失败', 'error');
        }
    }
    
    // 获取所有岗位
    function getAllJobs() {
        try {
            const jobs = localStorage.getItem('jobs');
            // 安全解析JSON，防止数据损坏
            return jobs ? JSON.parse(jobs) : [];
        } catch (error) {
            console.error('读取岗位数据失败:', error);
            showMessage('数据读取失败，已重置数据', 'error');
            // 重置localStorage数据
            localStorage.removeItem('jobs');
            initializeData();
            return [];
        }
    }
    
    // 根据ID获取岗位
    function getJobById(id) {
        try {
            const jobs = getAllJobs();
            return jobs.find(job => job.id === id);
        } catch (error) {
            console.error('获取岗位失败:', error);
            return null;
        }
    }
    
    // 添加岗位
    function addJob(jobData) {
        try {
            const jobs = getAllJobs();
            jobs.push(jobData);
            localStorage.setItem('jobs', JSON.stringify(jobs));
            return true;
        } catch (error) {
            console.error('添加岗位失败:', error);
            return false;
        }
    }
    
    // 更新岗位
    function updateJob(jobData) {
        try {
            let jobs = getAllJobs();
            const index = jobs.findIndex(job => job.id === jobData.id);
            if (index !== -1) {
                // 保留创建时间
                jobData.createdAt = jobs[index].createdAt;
                jobs[index] = jobData;
                localStorage.setItem('jobs', JSON.stringify(jobs));
                return true;
            }
            return false;
        } catch (error) {
            console.error('更新岗位失败:', error);
            return false;
        }
    }
    
    // 删除岗位
    function deleteJob(id) {
        try {
            let jobs = getAllJobs();
            jobs = jobs.filter(job => job.id !== id);
            localStorage.setItem('jobs', JSON.stringify(jobs));
            return true;
        } catch (error) {
            console.error('删除岗位失败:', error);
            return false;
        }
    }
    
    // 搜索岗位
    function searchJobs(company, title, status) {
        try {
            let jobs = getAllJobs();
            
            if (company) {
                jobs = jobs.filter(job => 
                    job.companyName.toLowerCase().includes(company.toLowerCase())
                );
            }
            
            if (title) {
                jobs = jobs.filter(job => 
                    job.jobTitle.toLowerCase().includes(title.toLowerCase())
                );
            }
            
            if (status) {
                jobs = jobs.filter(job => job.status === status);
            }
            
            renderJobList(jobs);
        } catch (error) {
            console.error('搜索岗位失败:', error);
            showMessage('搜索失败', 'error');
        }
    }
    
    // 加载岗位列表
    function loadJobList() {
        try {
            const jobs = getAllJobs();
            // 按创建时间倒序排序
            jobs.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            renderJobList(jobs);
        } catch (error) {
            console.error('加载岗位列表失败:', error);
            showMessage('加载列表失败', 'error');
        }
    }
    
    // 渲染岗位列表
    function renderJobList(jobs) {
        try {
            if (!jobTableBody) return;
            
            jobTableBody.innerHTML = '';
            
            if (jobs.length === 0) {
                const emptyRow = document.createElement('tr');
                emptyRow.innerHTML = `<td colspan="8" class="no-data">暂无数据</td>`;
                jobTableBody.appendChild(emptyRow);
                return;
            }
            
            jobs.forEach(job => {
                const row = document.createElement('tr');
                
                // 根据状态获取样式类
                const statusClass = getStatusClass(job.status);
                
                row.innerHTML = `
                    <td>${job.id}</td>
                    <td>${job.companyName}</td>
                    <td>${job.jobTitle}</td>
                    <td>${job.salary || '-'}</td>
                    <td>${job.location || '-'}</td>
                    <td><span class="status-badge ${statusClass}">${job.status}</span></td>
                    <td>${job.postDate || '-'}</td>
                    <td>
                        <button class="btn btn-secondary btn-sm edit-btn" data-id="${job.id}">编辑</button>
                    </td>
                `;
                
                jobTableBody.appendChild(row);
            });
            
            // 添加编辑按钮事件
            document.querySelectorAll('.edit-btn').forEach(btn => {
                btn.addEventListener('click', function() {
                    const jobId = parseInt(this.getAttribute('data-id'));
                    editJob(jobId);
                });
            });
        } catch (error) {
            console.error('渲染岗位列表失败:', error);
            showMessage('渲染失败', 'error');
        }
    }
    
    // 编辑岗位
    function editJob(id) {
        try {
            const job = getJobById(id);
            if (job) {
                currentJobId = id;
                document.getElementById('edit-id').value = job.id;
                document.getElementById('edit-company-name').value = job.companyName;
                document.getElementById('edit-job-title').value = job.jobTitle;
                document.getElementById('edit-salary').value = job.salary || '';
                document.getElementById('edit-requirements').value = job.requirements || '';
                document.getElementById('edit-location').value = job.location || '';
                document.getElementById('edit-post-date').value = job.postDate || '';
                document.getElementById('edit-status').value = job.status;
                document.getElementById('edit-notes').value = job.notes || '';
                
                openModal();
            }
        } catch (error) {
            console.error('编辑岗位失败:', error);
            showMessage('编辑失败', 'error');
        }
    }
    
    // 获取状态对应的CSS类
    function getStatusClass(status) {
        switch (status) {
            case '未申请': return 'status-pending';
            case '已申请': return 'status-applied';
            case '面试中': return 'status-interview';
            case '已录用': return 'status-offered';
            case '已拒绝': return 'status-rejected';
            default: return '';
        }
    }
    
    // 更新仪表盘统计
    function updateDashboardStats() {
        try {
            const jobs = getAllJobs();
            const stats = {
                total: jobs.length,
                pending: jobs.filter(job => job.status === '未申请').length,
                applied: jobs.filter(job => job.status === '已申请').length,
                interview: jobs.filter(job => job.status === '面试中').length,
                offered: jobs.filter(job => job.status === '已录用').length
            };
            
            document.getElementById('total-jobs').textContent = stats.total;
            document.getElementById('pending-jobs').textContent = stats.pending;
            document.getElementById('applied-jobs').textContent = stats.applied;
            document.getElementById('interview-jobs').textContent = stats.interview;
            document.getElementById('offered-jobs').textContent = stats.offered;
        } catch (error) {
            console.error('更新统计失败:', error);
        }
    }
    
    // 更新图表
    function updateChart() {
        try {
            const jobs = getAllJobs();
            const statusChartCanvas = document.getElementById('status-chart');
            
            // 检查Chart.js是否加载
            if (!statusChartCanvas || typeof Chart === 'undefined') {
                console.warn('Chart.js未加载或图表元素不存在');
                return;
            }
            
            const ctx = statusChartCanvas.getContext('2d');
            
            const statusCount = {
                '未申请': jobs.filter(job => job.status === '未申请').length,
                '已申请': jobs.filter(job => job.status === '已申请').length,
                '面试中': jobs.filter(job => job.status === '面试中').length,
                '已录用': jobs.filter(job => job.status === '已录用').length,
                '已拒绝': jobs.filter(job => job.status === '已拒绝').length
            };
            
            if (statusChart) {
                statusChart.destroy();
            }
            
            statusChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: ['未申请', '已申请', '面试中', '已录用', '已拒绝'],
                    datasets: [{
                        label: '岗位数量',
                        data: [statusCount['未申请'], statusCount['已申请'], statusCount['面试中'], statusCount['已录用'], statusCount['已拒绝']],
                        backgroundColor: [
                            '#ffeaa7',
                            '#d6eaf8',
                            '#d5f5e3',
                            '#e8daef',
                            '#fadbd8'
                        ],
                        borderColor: [
                            '#d68910',
                            '#2980b9',
                            '#16a085',
                            '#8e44ad',
                            '#e74c3c'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                stepSize: 1
                            }
                        }
                    },
                    plugins: {
                        title: {
                            display: true,
                            text: '岗位状态分布'
                        }
                    }
                }
            });
        } catch (error) {
            console.error('创建图表失败:', error);
            showMessage('图表创建失败', 'warning');
        }
    }
    
    // 添加活动记录
    function addActivity(activity) {
        try {
            let activities = localStorage.getItem('activities');
            activities = activities ? JSON.parse(activities) : [];
            const timestamp = new Date().toLocaleString();
            activities.unshift(`[${timestamp}] ${activity}`);
            
            // 保留最近20条记录
            if (activities.length > 20) {
                activities = activities.slice(0, 20);
            }
            
            localStorage.setItem('activities', JSON.stringify(activities));
            loadActivities();
        } catch (error) {
            console.error('添加活动记录失败:', error);
        }
    }
    
    // 加载活动记录
    function loadActivities() {
        try {
            let activities = localStorage.getItem('activities');
            activities = activities ? JSON.parse(activities) : [];
            
            if (!activityList) return;
            
            activityList.innerHTML = '';
            activities.forEach(activity => {
                const li = document.createElement('li');
                li.textContent = activity;
                activityList.appendChild(li);
            });
        } catch (error) {
            console.error('加载活动记录失败:', error);
        }
    }
    
    // 显示消息
    function showMessage(text, type = 'success') {
        try {
            if (!messageElement) return;
            
            messageElement.textContent = text;
            messageElement.className = `message ${type} show`;
            
            setTimeout(() => {
                messageElement.classList.remove('show');
            }, 3000);
        } catch (error) {
            console.error('显示消息失败:', error);
        }
    }
    
    // 打开模态框
    function openModal() {
        try {
            if (!modal) return;
            modal.style.display = 'block';
            // 阻止背景滚动
            document.body.style.overflow = 'hidden';
        } catch (error) {
            console.error('打开模态框失败:', error);
        }
    }
    
    // 关闭模态框
    function closeModal() {
        try {
            if (!modal) return;
            modal.style.display = 'none';
            document.body.style.overflow = '';
            if (editJobForm) editJobForm.reset();
            currentJobId = null;
        } catch (error) {
            console.error('关闭模态框失败:', error);
        }
    }
    
    // 打开确认模态框
    function openConfirmModal() {
        try {
            if (!confirmModal) return;
            confirmModal.style.display = 'block';
        } catch (error) {
            console.error('打开确认模态框失败:', error);
        }
    }
    
    // 关闭确认模态框
    function closeConfirmModal() {
        try {
            if (!confirmModal) return;
            confirmModal.style.display = 'none';
            deleteJobId = null;
        } catch (error) {
            console.error('关闭确认模态框失败:', error);
        }
    }
    
    // 初始化图表
    function initChartIfAvailable() {
        try {
            const statusChartCanvas = document.getElementById('status-chart');
            if (statusChartCanvas && typeof Chart !== 'undefined') {
                // 当用户切换到统计页面时会自动更新图表
            } else if (statusChartCanvas) {
                // 如果没有Chart.js，显示提示信息
                const chartContainer = statusChartCanvas.parentElement;
                if (chartContainer) {
                    chartContainer.innerHTML = '<p class="chart-notice">请引入Chart.js以查看统计图表</p>';
                }
            }
        } catch (error) {
            console.error('初始化图表失败:', error);
        }
    }
    
    // 调用初始化图表函数
    initChartIfAvailable();
    
    // 添加键盘快捷键支持
    document.addEventListener('keydown', function(e) {
        // ESC键关闭模态框
        if (e.key === 'Escape') {
            closeModal();
            closeConfirmModal();
        }
    });
    
    // 增加错误边界处理
    window.addEventListener('error', function(e) {
        console.error('发生错误:', e.error);
        showMessage('系统发生错误，请刷新页面重试', 'error');
    });
});