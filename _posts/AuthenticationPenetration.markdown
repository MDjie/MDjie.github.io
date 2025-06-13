# Authentication Penetration Learning Record

## Tool
### Burp-Intruder
#### Basically Using
![基本使用1](../assets/Burp_Intruder_1.png)
在代理的历史记录里找到需要攻击的地址，右键点击`send to intruder`
![基本使用2](../assets/Burp_Intruder_2.png)
操作修改参数的位置，选择攻击类型：
![基本使用3](../assets/Burp_Intruder_3.png)
***Sniper***支持一个参数，一组值(n个),攻击次数：n

***Battering_ram***支持多个参数，一组值，将一个值填到每个参数进行攻击，攻击次数：n

***pitchfork***支持多个参数，多组值，每次攻击各个位置都会取到自己对应组的下一个位置，攻击次数：min(Ni)

***Cluster_bomb***支持多个参数，多组值，穷举出每个参数位置的值域的所有组合。攻击次数：n1*n2*n3.....

