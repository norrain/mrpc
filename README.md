# mrpc

##########################
**简单rpc框架**
##########################
1. mrpc-client-customer 模拟客户端 测试 （依赖 mrpc-test-client）
2. mrpc-test-provider   模拟服务端 测试
    * mrpc-test-client  定义接口PO等（依赖 mrpc-core）
    * mrpc-test-server  为服务化 做中间层调用（依赖 mrpc-test-client、mrpc-test-service）
    * mrpc-test-service 具体的业务实现及数据库操作
3. mrpc-core      核心模块
4. mrpc-register  服务注册中心及管理 
