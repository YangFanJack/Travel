# Travel旅游网
### 技术选型
1. web层
    * Servlet：控制
    * html：视图
    * ajax：异步交互
    * Filter：过滤
    * BeanUtils：封装
    * jackson：序列化工具
2. Service层
    * Javamail：java发送邮件工具
    * Redis：nosql内存数据库
    * Jedis：java的redis客户端
3. Dao层
    * MuSQL：数据库
    * Druid：数据库连接池
    * JDBCTemplate：jdbc操作封装工具
    
    
### 开发步骤
1. 注册
    * 前台ajax发送数据
    * 后台根据用户名查询是否已存在
    * 存在则返回提示信息，不存在则insert
2. 邮件激活
    * 发送邮件(发送方需要设置授权码)
    * 用户点击邮件激活
        * 用户激活就是修改status为Y
        * id太好猜，所以用激活码作为唯一标识
3. 登录
    * 前台ajax发送数据
    * 后台根据用户名和密码查询用户是否存在
    * 如果存在后台判断用户是否激活
    * 如果正确则跳转到登陆成功页面,显示用户名（只能异步交互，html没有session）
    * 登陆失败则返回前台错误信息
4. 退出
    * 删除session
    * 跳转到登录页面
5. 优化servlet
    * 减少servlet的数量
    * 一个功能一个servlet——>一个模块一个servlet，相当于一个表一个servlet
    * 在servlet中提供不同方法来完成不同功能
    * 把userservice的创建代码继续抽取出来
    * 把序列化为json的操作也抽取出来
6. 分类数据展示
    * 前台发送ajax请求
    * 服务器里调用service查询
    * 将查到的结果封装为list
    * 将list序列化为json返回前台
7. 对分类数据进行缓存优化
    * 分类数据不经常变化
    * 减小对数据库的压力
    * 步骤：
        * findAll中先从redis中查询
        * 判断redis中是否有（是不是第一次访问）
        * 没有则查询数据库并存入redis
        * 若有则直接返回集合
8. 旅游线路的分页展示功能
    * 在redis中用jedis.zrangeWithScores查询cid
    * 页面传递cid到route_list.html
9. 分页展示旅游线路数据
    * 客户端
        * 前台接受json数据显示数据
        * 数据内容展示
        * 分页条展示，全部是异步刷新
        * 分页条的隐藏，只显示10个（前5后4，前不足后补足，后不足前补齐）
    * 服务端
        * 创建pageBean类
        * 然后保存totalCount，totalPage，list，currentPage，pageSize到pageBean对象
        * 返回pageBean的json格式给前台
10. 搜索
    * 用getparamter替换获取url参数的代码
    * 拼接查询数据的url
    * 后台根据拿到的数据查村
11. 详情页面的显示
    * 点击route_list的显示详情，跳转到详情页面
    * 通过查询数据库来填充完整页面
    * 重点是多表查询
12. 旅游线路收藏
    * 查数据库判断是否收藏过
13. 点击收藏
    * 前台：判断用户是否登录（用ajax）
    * 后天：