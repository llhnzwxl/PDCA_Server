# PDCA 项目管理系统

PDCA（Plan-Do-Check-Act）项目管理系统是一个基于 Spring Boot 的后端服务，用于支持团队进行项目的计划、执行、检查和改进的完整闭环管理。

## 项目架构

### 技术栈
- Spring Boot 2.x
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Swagger API 文档
- Maven

### 系统架构 
src/main/java/com/example/pdca/
├── config/ # 配置类
├── controller/ # REST API 控制器
├── dto/ # 数据传输对象
├── model/ # 实体类
├── repository/ # 数据访问层
├── service/ # 业务逻辑层
├── security/ # 安全相关
└── util/ # 工具类

### 核心模块
1. **用户管理**：用户注册、登录、角色管理（ADMIN/MANAGER/USER）
2. **计划管理**：创建、更新、查询计划，任务分配
3. **执行管理**：任务执行、进度跟踪、执行记录
4. **检查管理**：检查记录、问题发现、结果评估
5. **行动管理**：改进措施、执行跟踪、标准化
6. **报告管理**：PDCA 循环报告生成与导出（PDF/Excel）

## PDCA 循环流程

### 1. 计划阶段 (Plan)
- 创建新计划，设置标题、描述、时间范围
- 定义关键指标（KPI）
- 创建任务清单并分配负责人
- 设置任务优先级和时间线

### 2. 执行阶段 (Do)
- 团队成员执行分配的任务
- 记录执行进展和问题
- 更新任务状态
- 记录解决方案和里程碑

### 3. 检查阶段 (Check)
- 记录检查结果（成就/问题/建议/偏差）
- 评估执行效果
- 分析问题原因
- 总结经验教训

### 4. 行动阶段 (Act)
- 制定改进措施
- 实施纠正行动
- 建立预防机制
- 标准化最佳实践
- 启动新的 PDCA 循环

## API 接口说明

### 用户认证
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 计划管理
- `POST /api/plans` - 创建计划
- `GET /api/plans` - 获取计划列表
- `GET /api/plans/{planId}` - 获取计划详情
- `PUT /api/plans/{planId}` - 更新计划
- `GET /api/plans/my-plans` - 获取我的计划

### 任务管理
- `POST /api/tasks` - 创建任务
- `GET /api/tasks/my-tasks` - 获取我的任务
- `PATCH /api/tasks/{taskId}/assignee` - 分配任务负责人
- `PATCH /api/tasks/{taskId}/status` - 更新任务状态
- `PATCH /api/tasks/{taskId}/evaluate` - 评价任务（仅计划创建者可操作）

### PDCA 阶段管理
- `GET /api/do-phases/my-do-phases` - 获取我的执行阶段
- `GET /api/check-phases/my-check-phases` - 获取我的检查阶段
- `GET /api/act-phases/my-act-phases` - 获取我的行动阶段

### 报告管理
- `POST /api/reports/generate/{planId}` - 生成 PDCA 报告
- `GET /api/reports/{reportId}` - 获取报告详情
- `GET /api/reports/export/pdf/{reportId}` - 导出 PDF 报告
- `POST /api/reports/{reportId}/submit` - 提交报告

## 数据模型关系

User ──┬── Plan ──┬── Task
│ └── DoPhase ── CheckPhase ── ActPhase
└── Report

## 权限控制

### 角色权限
1. **管理员 (ADMIN)**
   - 用户管理权限
   - 所有数据的读写权限
   - 系统配置权限

2. **项目经理 (MANAGER)**
   - 计划的创建和管理
   - 任务分配权限
   - 报告管理权限
   - 团队管理权限

3. **普通用户 (USER)**
   - 任务执行权限
   - 个人数据的读写权限
   - 相关报告的查看权限

## 部署说明

### 环境要求
- JDK 8+
- MySQL 5.7+
- Maven 3.6+

### 配置文件
```yaml
配置文件
yaml
server:
port: 8080
servlet:
context-path: /pdca
spring:
datasource:
url: jdbc:mysql://localhost:3306/pdca?useSSL=false
username: your_username
password: your_password
jwt:
secret: pdcaProjectSecretKey
expiration: 86400000 # 24小时
```

克隆项目
bash
git clone https://github.com/your-username/pdca-project.git
配置数据库
bash
mysql -u root -p < schema.sql
运行项目
bash
mvn spring-boot:run


## 前端开发建议

### 技术选择
- Vue.js 3 + TypeScript
- Ant Design Vue
- Axios
- ECharts

### 核心功能模块
1. **登录注册模块**
   - 用户认证
   - 角色选择
   - 权限管理

2. **仪表盘模块**
   - PDCA 概览
   - 任务统计
   - 进度展示

3. **计划管理模块**
   - 计划创建
   - 任务分配
   - 进度跟踪

4. **PDCA 循环模块**
   - 执行记录
   - 检查评估
   - 改进措施

5. **报告中心模块**
   - 报告生成
   - 数据导出
   - 统计分析

### UI/UX 建议
- 响应式设计
- 简洁清晰的界面
- 直观的数据可视化
- 便捷的操作流程
- 实时状态更新

## 联系方式

如有问题，请联系：
- Email: your.email@example.com
- GitHub Issues: [项目问题反馈](https://github.com/your-username/pdca-project/issues)

## 前端开发流程示例

### 1. 创建并完成一个完整的 PDCA 循环

#### 1.1 计划创建流程
1. **用户登录**
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "manager",
    "password": "password"
}
```

2. **创建新计划**
```http
POST /api/plans
Content-Type: application/json

{
    "title": "Q1系统优化计划",
    "description": "优化系统性能和用户体验",
    "startTime": "2024-01-01",
    "endTime": "2024-03-31",
    "priority": "HIGH",
    "keyIndicators": "系统响应时间提升30%"
}
```

3. **创建任务**
```http
POST /api/tasks
Content-Type: application/json

{
    "planId": 1,
    "title": "数据库优化",
    "description": "优化数据库查询性能",
    "priority": "HIGH",
    "deadline": "2024-01-15"
}
```

4. **分配任务**
```http
PATCH /api/tasks/1/assignee
Content-Type: application/json

{
    "assigneeId": 2
}
```

#### 1.2 执行阶段记录
1. **创建执行阶段**
```http
POST /api/do-phases
Content-Type: application/json

{
    "planId": 1,
    "title": "数据库优化执行",
    "description": "执行数据库优化方案",
    "executorId": 2
}
```

2. **添加执行记录**
```http
POST /api/do-phases/1/records
Content-Type: application/json

{
    "content": "完成索引优化，查询性能提升40%",
    "type": "PROGRESS"
}
```

#### 1.3 检查阶段评估
1. **创建检查阶段**
```http
POST /api/check-phases
Content-Type: application/json

{
    "doPhaseId": 1,
    "title": "性能优化检查",
    "description": "检查优化效果"
}
```

2. **记录检查结果**
```http
POST /api/check-phases/1/results
Content-Type: application/json

{
    "content": "系统响应时间平均提升35%，超出预期目标",
    "type": "ACHIEVEMENT"
}
```

#### 1.4 改进阶段实施
1. **创建行动阶段**
```http
POST /api/act-phases
Content-Type: application/json

{
    "checkPhaseId": 1,
    "title": "性能优化标准化",
    "description": "将成功的优化方案形成标准"
}
```

2. **记录改进措施**
```http
POST /api/act-phases/1/records
Content-Type: application/json

{
    "content": "编写数据库优化规范文档",
    "type": "IMPROVEMENT"
}
```

#### 1.5 生成总结报告
1. **生成 PDCA 报告**
```http
POST /api/reports/generate/1
```

2. **提交报告**
```http
POST /api/reports/1/submit
```

### 2. 前端页面流程建议

#### 2.1 计划管理页面
1. **计划列表页**
   - 调用 `GET /api/plans` 获取计划列表
   - 显示计划基本信息和状态
   - 提供创建新计划的入口

2. **计划详情页**
   - 调用 `GET /api/plans/{planId}` 获取详情
   - 显示计划详细信息和相关任务
   - 提供任务管理功能

#### 2.2 任务看板页面
1. **看板视图**
   - 调用 `GET /api/tasks/my-tasks` 获取任务
   - 按状态分列显示（待处理/进行中/已完成）
   - 支持拖拽更新状态

2. **任务详情弹窗**
   - 显示任务详细信息
   - 提供更新状态和添加评论功能

#### 2.3 PDCA 循环管理页面
1. **循环概览**
   - 显示当前计划的 PDCA 各阶段状态
   - 提供阶段切换导航

2. **阶段详情**
   - 根据阶段调用相应接口获取数据
   - 提供记录添加和更新功能

#### 2.4 报告中心页面
1. **报告列表**
   - 显示所有相关报告
   - 提供报告生成和导出功能

2. **报告详情**
   - 显示完整的 PDCA 循环总结
   - 支持 PDF/Excel 导出

### 3. 状态管理建议
- 使用 Vuex/Pinia 管理全局状态
- 缓存常用数据减少请求
- 实现乐观更新提升体验

### 4. 用户体验建议
- 添加加载状态提示
- 实现错误处理和提示
- 添加操作确认弹窗
- 支持批量操作功能
- 实现数据自动保存

## 操作日志管理

- 记录计划和任务的关键节点日志
- 支持多种日志类型（里程碑、进展、风险、问题、决策、变更）
- 支持附件上传
- 按计划查询相关日志（包括计划直接关联的日志和任务日志）

### 任务相关
- `GET /api/tasks/{taskId}` - 获取任务详情
  - 返回任务的基本信息、状态、负责人以及评价信息（分数、评语、评价时间）
- `PATCH /api/tasks/{taskId}/assignee` - 分配任务负责人
- `PATCH /api/tasks/{taskId}/status` - 更新任务状态
- `PATCH /api/tasks/{taskId}/evaluate` - 评价任务（仅计划创建者可操作）