# IT岗位求职记录系统

这是一个使用Java语言和MySQL数据库开发的IT岗位求职记录系统，可以帮助用户记录、管理和跟踪适合自己的IT岗位信息。

## 功能特性

- **添加岗位**：记录企业名称、岗位名称、薪资、具体要求、工作地点等信息
- **查看岗位列表**：展示所有记录的岗位信息
- **搜索岗位**：根据企业名称、岗位名称、状态等条件筛选查询
- **更新岗位**：修改现有岗位的详细信息
- **删除岗位**：移除不需要的岗位记录

## 技术栈

- **语言**：Java
- **数据库**：MySQL
- **JDBC**：用于数据库连接和操作

## 项目结构

```
├── src/main/java/com/itjobtracker/
│   ├── model/          # 数据模型
│   ├── dao/            # 数据访问对象
│   ├── service/        # 业务逻辑层
│   ├── controller/     # 控制器层
│   ├── util/           # 工具类
│   └── Main.java       # 程序入口
├── database.sql        # 数据库初始化脚本
└── README.md           # 项目说明
```

## 环境要求

- JDK 8或更高版本
- MySQL 5.7或更高版本
- MySQL Connector/J驱动

## 使用说明

### 1. 数据库准备

1. 安装并启动MySQL数据库服务
2. 使用`database.sql`脚本创建数据库和表：

```sql
-- 在MySQL命令行或工具中执行以下命令
SOURCE database.sql;
```

### 2. 配置数据库连接

修改`src/main/java/com/itjobtracker/util/DatabaseUtil.java`文件中的数据库连接信息：

```java
private static final String URL = "jdbc:mysql://localhost:3306/it_job_tracker?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8";
private static final String USER = "root";  // 替换为您的MySQL用户名
private static final String PASSWORD = "password";  // 替换为您的MySQL密码
```

### 3. 编译和运行

**使用命令行编译和运行**：

1. 确保已将MySQL Connector/J的JAR文件添加到classpath
2. 编译Java文件：

```bash
javac -cp .;mysql-connector-java-x.x.xx.jar src/main/java/com/itjobtracker/*.java src/main/java/com/itjobtracker/**/*.java
```

3. 运行程序：

```bash
java -cp .;mysql-connector-java-x.x.xx.jar com.itjobtracker.Main
```

### 4. 系统操作

运行程序后，按照控制台菜单提示进行操作：
- 输入1：添加新岗位
- 输入2：查看所有岗位
- 输入3：搜索岗位
- 输入4：更新岗位
- 输入5：删除岗位
- 输入6：退出系统

## 注意事项

1. 确保MySQL服务正在运行
2. 正确配置数据库连接信息
3. 添加MySQL Connector/J驱动到项目中
4. 输入日期时请使用yyyy-MM-dd格式