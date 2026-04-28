# 微信支付证书目录

请将微信支付商户证书文件放置在此目录：

## 必需文件

| 文件名 | 说明 |
|--------|------|
| `apiclient_key.pem` | 商户API私钥 |
| `apiclient_cert.pem` | 商户API证书 |

## 获取方式

1. 登录 [微信支付商户平台](https://pay.weixin.qq.com)
2. 进入 **账户中心** → **API安全**
3. 申请API证书，下载证书文件

## 注意事项

- 证书文件请勿提交到Git仓库
- 生产环境建议使用环境变量配置证书路径
- 证书有效期为5年，请注意续期

## 环境变量配置示例

```bash
export WECHAT_PAY_PRIVATE_KEY_PATH=/path/to/apiclient_key.pem
export WECHAT_PAY_PRIVATE_CERT_PATH=/path/to/apiclient_cert.pem
```
