
### 一、整体规则（核心约束）
1. 仅实现基础票务买卖和管理功能，不涉及支付、人脸识别、退款、优惠券等复杂逻辑；
2. 数据流转：前端通过Axios调用后端RESTful接口 → 后端统一返回格式{code:数字, msg:字符串, data:对象/数组} → 前端根据返回码处理交互（成功提示/失败弹窗）；
3. 核心规则：
   - 票种库存扣减使用乐观锁（version字段）防止超卖，取消订单时恢复对应库存；
   - 订单编号生成规则：yyyyMMddHHmmss + 6位随机数（保证唯一性）；
   - 订单状态：1-已下单（默认）、2-已核销、3-已取消（仅“已下单”状态可核销/取消，“已核销/已取消”状态不可修改）；
   - 管理员角色：1-超级管理员（可操作所有功能）、2-普通管理员（不可删除景区/票种/用户）。

### 二、后端开发要求（SpringBoot）
#### 1. 数据库设计（需输出完整建表SQL）
- 景区表（scenic_spot）：id、name（非空）、intro、area、create_time、update_time；
- 票种表（ticket_type）：id、scenic_id（关联景区）、type_name（非空）、price（非空，正数）、stock（非空，≥0）、valid_start、valid_end、version（乐观锁）、create_time、update_time；
- 订单表（ticket_order）：id、order_no（唯一）、scenic_id、ticket_type_id、user_name（非空）、user_phone（非空）、ticket_num（非空，≥1）、total_amount（非空）、order_status（非空）、create_time、update_time；
- 用户表（sys_user）：id、username（唯一）、password（加密存储）、role（1/2，非空）、create_time、update_time；

#### 2. 核心接口（需输出接口文档，包含请求方式/路径/参数/返回示例）
##### （1）用户模块（完整CRUD）
- 分页查询用户：GET /api/user/list，参数{pageNum,pageSize,username（模糊）,role}，返回分页数据；
- 新增用户：POST /api/user/add，参数{username,password,role}，校验username唯一、password非空、role为1/2，密码BCrypt加密存储；
- 修改用户：PUT /api/user/update，参数{id,username,role}，校验username唯一、role为1/2，禁止修改超级管理员自身角色；
- 删除用户：DELETE /api/user/delete/{id}，校验非超级管理员可删除（禁止删除超级管理员账号）；
- 登录接口：POST /api/user/login，参数{username,password}，返回{code,msg,data:{token,userInfo}}；
- 获取当前用户信息：GET /api/user/current，请求头带token，返回{code,msg,data:{id,username,role}}；

##### （2）景区模块（完整CRUD）
- 分页查询景区：GET /api/scenic/list，参数{pageNum,pageSize,name（模糊）,area}，返回分页数据；
- 新增景区：POST /api/scenic/add，参数{name,intro,area}，校验name非空；
- 修改景区：PUT /api/scenic/update，参数{id,name,intro,area}，校验name非空；
- 删除景区：DELETE /api/scenic/delete/{id}，校验是否关联票种（有关联则禁止删除）；

##### （3）票种模块（完整CRUD）
- 分页查询票种：GET /api/ticket/list，参数{pageNum,pageSize,scenicId,typeName（模糊）,priceMin,priceMax}，返回分页数据；
- 新增票种：POST /api/ticket/add，参数{scenicId,typeName,price,stock,validStart,validEnd}，校验price>0、stock≥0、scenicId存在；
- 修改票种：PUT /api/ticket/update，参数{id,scenicId,typeName,price,stock,validStart,validEnd}，校验规则同新增；
- 删除票种：DELETE /api/ticket/delete/{id}，校验是否关联订单（有关联则禁止删除）；
- 获取票种库存：GET /api/ticket/stock/{id}，返回当前库存；

##### （4）订单模块（完整CRUD+业务逻辑）
- 生成订单：POST /api/order/create，参数{scenicId,ticketTypeId,userName,userPhone,ticketNum}，逻辑：①校验票种库存≥ticketNum；②扣减库存（乐观锁）；③生成order_no；④计算total_amount=price*ticketNum；⑤初始化状态为1；
- 分页查询订单：GET /api/order/list，参数{pageNum,pageSize,orderNo,userPhone,startTime,endTime,orderStatus,scenicId,ticketTypeId}，返回分页数据；
- 修改订单：PUT /api/order/update，参数{id,userName,userPhone}（仅允许修改购票人信息，禁止修改数量/金额/状态）；
- 删除订单：DELETE /api/order/delete/{id}，仅允许删除“已取消”状态的订单；
- 修改订单状态：PUT /api/order/updateStatus，参数{id,orderStatus}，逻辑：①仅状态1可修改为2/3；②修改为3时恢复票种库存；
- 订单统计：GET /api/order/statistics，参数{startTime,endTime}，返回{totalSales（总销售额）,totalNum（总销量）,ticketTypeRank（票种销量排行）}；

#### 3. 后端通用配置
- 全局异常处理：捕获参数校验、数据库操作、业务逻辑异常，返回统一错误格式；
- JWT配置：有效期2小时，密钥自定义，请求头携带token（Key：Authorization，Value：Bearer + 令牌）；
- 跨域配置：允许前端域名跨域请求；
- 密码加密：使用BCrypt加密存储管理员密码；
- 权限控制：接口层添加角色鉴权注解（如@PreAuthorize("hasRole('ADMIN')")），区分超级/普通管理员权限。

### 三、前端开发要求（Vue 3 + Element Plus）
#### 1. 项目结构
- 路由划分：
  - /login（登录）；
  - /admin（管理员首页，子路由：/admin/user、/admin/scenic、/admin/ticket、/admin/order）；
  - /visitor（游客端，核心为购票入口：/visitor/buy-ticket）；
- 组件划分：登录组件、用户管理组件、景区管理组件、票种管理组件、订单管理组件、票务购买组件、统计组件；
- 请求封装：统一封装Axios，请求拦截器添加token，响应拦截器统一处理返回码（如code=401跳转登录页，code=500弹窗提示错误）。

#### 2. 页面交互细节（核心）
##### （1）登录页
- 交互：①账号/密码为空时点击登录，弹窗提示“请输入账号/密码”；②登录失败（账号错误/密码错误），弹窗提示具体原因；③登录成功，存储token到localStorage，跳转/admin首页；④记住密码功能（存储到localStorage）。

##### （2）管理员首页（布局：侧边栏+主内容区）
- 侧边栏菜单：用户管理、景区管理、票种管理、订单管理、统计分析（超级管理员显示所有删除按钮，普通管理员隐藏删除按钮）；
- 路由守卫：未登录访问/admin跳转/login，token过期跳转/login，普通管理员访问删除接口时弹窗提示“无操作权限”。

##### （3）用户管理页（完整CRUD）
- 列表：分页展示，支持按用户名模糊搜索、按角色筛选，每行显示“编辑/删除”按钮（普通管理员隐藏）；
- 新增：弹窗表单，校验用户名非空/唯一、角色必选，密码默认123456（BCrypt加密），提交成功刷新列表并弹窗提示“新增用户成功”；
- 编辑：弹窗回显当前数据，仅允许修改角色（禁止修改超级管理员自身角色），提交成功刷新列表并提示“修改用户成功”；
- 删除：二次确认“是否删除”，删除超级管理员时提示“禁止删除”，删除成功刷新列表并提示。

##### （4）景区管理页（完整CRUD）
- 列表：分页展示，支持按景区名称/区域模糊搜索，每行显示“编辑/删除”按钮（普通管理员隐藏删除）；
- 新增：弹窗表单，校验景区名称非空，提交成功刷新列表并弹窗提示“新增景区成功”；
- 编辑：弹窗回显当前数据，提交成功刷新列表并弹窗提示“修改景区成功”；
- 删除：二次确认“是否删除”，关联票种时弹窗提示“该景区关联票种，禁止删除”，删除成功刷新列表并提示。

##### （5）票种管理页（完整CRUD）
- 列表：先选择景区，再展示该景区下的票种，分页+按票种名称/价格区间搜索，每行显示“编辑/删除”按钮（普通管理员隐藏删除）；
- 新增：弹窗表单，关联景区必选，价格校验正数，库存校验≥0，提交成功刷新列表并提示“新增票种成功”；
- 编辑：同新增，回显数据，校验规则一致，提交成功提示“修改票种成功”；
- 删除：二次确认，关联订单时提示“该票种关联订单，禁止删除”，删除成功提示并刷新。

##### （6）订单管理页（完整CRUD+状态修改）
- 列表：多条件筛选（订单号/手机号/时间范围/订单状态/景区/票种），分页展示，每行显示“编辑/删除/核销/取消”按钮（仅状态1显示核销/取消，仅已取消状态显示删除）；
- 编辑：仅允许修改购票人姓名/电话，提交成功提示“修改订单信息成功”；
- 删除：二次确认“是否删除”，仅已取消订单可删除，删除成功提示并刷新；
- 核销：二次确认“是否核销”，提交成功更新列表状态并提示“核销成功”；
- 取消：二次确认“是否取消”，提交成功更新列表状态、恢复库存并提示“取消成功”；
- 统计：展示时间范围内的总销售额、总销量，票种销量排行（柱状图/列表）。

##### （7）游客端-票务购买入口（核心）
- 入口位置：前端首页（/visitor）显著位置设置“购票”按钮，点击进入购票页（/visitor/buy-ticket）；
- 购票页交互：
  ① 景区列表：展示所有可用景区（按名称排序），每个景区卡片显示名称、简介、区域；
  ② 票种选择：点击景区卡片，展开该景区下所有票种（显示名称、价格、剩余库存、有效期），库存为0时标注“已售罄”并禁用选择；
  ③ 数量选择：选择票种后，输入购买数量（默认1，校验≥1且≤剩余库存），库存不足时弹窗提示“库存不足，当前剩余XX张”；
  ④ 信息填写：点击“立即购票”，弹窗填写购票人姓名、手机号（校验手机号格式）；
  ⑤ 提交下单：点击“确认购票”，调用后端生成订单接口，成功后弹窗显示“购票成功，订单编号：XXX”，并提供“返回首页”/“查看订单”按钮；
  ⑥ 异常处理：下单失败（如库存被抢空），弹窗提示“下单失败：XXX”。

### 四、输出要求（必须完整且可直接运行）
1. 后端输出：
   - 完整项目代码（按包划分：entity/mapper/service/controller/config/exception）；
   - application.yml（配置数据库连接、JWT、端口等）；
   - 数据库建表SQL（包含测试数据：
     - 超级管理员：账号admin/密码123456（BCrypt加密）；
     - 普通管理员：账号user/密码123456；
     - 测试景区：ID=1，名称“测试景区”，简介“测试用”，区域“测试区”；
     - 测试票种：ID=1，关联景区1，名称“成人票”，价格99.00，库存100，有效期2026-01-01至2026-12-31；
   ）；
   - 启动步骤：Maven打包、启动命令、接口测试示例（Postman/Curl）。

2. 前端输出：
   - 完整项目代码（按目录划分：src/components/src/router/src/utils/request.js）；
   - package.json（依赖完整）；
   - 启动步骤：npm install、npm run dev；
   - 接口调用示例：所有接口的Axios调用代码；
   - 页面路由配置：明确/login、/admin/*、/visitor、/visitor/buy-ticket的路由规则。

3. 联调说明：
   - 前后端联调配置（前端修改请求基地址为后端IP:端口）；
   - 完整测试流程：
     ① 超级管理员登录→新增用户/景区/票种；
     ② 游客访问/visitor→点击购票入口→选择景区/票种→填写信息→下单；
     ③ 管理员登录→查询订单→核销/取消订单→删除已取消订单；
   - 常见问题排查（如跨域、token失效、库存扣减失败、权限不足）。

4. 代码规范：
   - 所有代码添加清晰注释（类/方法/核心逻辑）；
   - 变量/方法命名符合驼峰命名法；
   - 前端样式适配1920*1080分辨率，布局美观，购票入口按钮突出显示；
   - 后端接口参数校验、异常捕获完整，避免空指针。