keycloak AccessToken验证过程

1、解码 token（注意是解码，不是解密，因为token是不加密的，只是按照一定规则进行编码，并签名）。

2、取得配置的 publickey（含 kid），或根据配置的keycloak地址和realm信息，调用keycloak的Rest接口（ /realms/{realm-name}/protocol/openid-connect/certs）查询publicKey(含kid)。

3、从步骤2中得到的publicKey中，查找与步骤1中得到的kid匹配的publicKey。

4、如果找不到对应的publicKey，则报异常：Didn't find publicKey for specified kid。

5、使用publicKey验证签名

6、检查Token中的subject属性是否为空，为空则报异常：Subject missing in token

7、检查配置realm url 与 token中的issuer是否匹配，不匹配则报异常：Invalid token issuer. Expected {realm url}, but was {issuer}

7、检查token是否已过期，已过期，则报异常：Token is not active