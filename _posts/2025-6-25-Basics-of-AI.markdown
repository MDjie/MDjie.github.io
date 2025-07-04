---
layout: post
title: "Basics of AI"
date: 2025-06-05 17:26:02 +0800
category: jekyll update
---
# Math Basics

## Criteria & loss
### Regression 
- MSE(mean squared error)均方误差:
![均方误差数学公式](../assets/均方误差.png)
特点：对大误差敏感，可导性良好，适合低outliers情况，但不适合高outliers
- MAE(mean absolute error):
![img_1.png](../assets/平均绝对误差.png)
对所有误差衡量为线性，鲁棒性较好，但是在零点不可导
- Huber loss:
![img_1.png](../assets/huber_loss.png)
 结合了MSE和MAE的优点，对小误差，采用均方误差提升保证联系可导；对大误差，采用平均绝对误差，增强鲁棒性。
### Binary Classification
####  ROC:Receiver Operating Characteristic

#### precision-recall-curve

#### class likelihood ratios


# Models

# Coding Basics
## Python
### Python Input And Output
#### 文件输出输出

#### 标准输入输出
- print函数
`print(*objects, sep=' ', end='\n', file=sys.stdout, flush=False)`
参数：
- sep,分隔符，指定多个对象之间的分隔符，默认为空格
```aiexclude
print("a",1,sep='-')
----------------------
a-1
```
- end,结束符，默认为换行
```aiexclude
print("Hello", end=" ")
print("World") 
-------------------
Hello World
```
- file,可以输出到文件
```aiexclude
with open("output.txt", "w") as f:
    print("This goes to a file", file=f)
```
- flush,控制是否立即刷新输出到缓冲区，默认为false
- 特殊用法
1. 打印列表，字典等数据结构
```aiexclude
numbers = [1, 2, 3, 4, 5]
print(numbers)  # 输出: [1, 2, 3, 4, 5]

person = {"name": "Bob", "age": 30}
print(person)   # 输出: {'name': 'Bob', 'age': 30}
```
2. 重定向输出
```aiexclude
from contextlib import redirect_stdout

with open('output.txt', 'w') as f:
    with redirect_stdout(f):
        print('This goes to the file')
```
3. 打印彩色文本
`print("\033[显示方式;前景色;背景色m文本内容\033[0m")`

| 类别   | 代码  |效果|
|------|-----|---|
| 文本样式 | 0   |重置所有样式|
|      | 1   |粗体高亮|
|      | 2   |暗淡
|      | 3   |斜体
|      | 4   |下划线
|      | 5   |闪烁
|      | 7   |反显(前景背景互换)
|      | 8   |隐藏
| 前景色	 | 30  |	黑色
|      | 31  |	红色
|      | 32  |	绿色
|      | 33  |	黄色
 |      | 34  | 	蓝色 
 |      | 35  | 	紫色 
 |      | 36  | 	青色 
 |      | 37  | 	白色 
 | 背景色  | 	40 | 	黑色 
 |      | 41  | 	红色 
 |      | 42  | 	绿色 
 |      | 43  | 	黄色 
 |      | 44  | 	蓝色 
 |      | 45  | 	紫色 
|      | 46  | 	青色 
|      | 47  |	白色
```Python
print("\033[31mThis is red text\033[0m")
print("\033[1;32;40mBright green text\033[0m")
```
4. 打印进度条
### data structure and usage
列表是 Python 中最常用的数据结构之一，它是一个有序、可变的集合。
```python
#List
my_list=[1,2,"231",0.123,True,False,{"a":1,"b":2}]
empty_list=[]
list_fron_range=list(range(5))

#访问列表
ele=my_list[0:-2] #左闭右开
for e in ele:
    print (e)
ele_1=my_list[3]
print(ele_1)
my_list.append("last one")
print(my_list[-1])
my_list.insert(0,"the first one")
print(my_list[0])
my_list.remove(True)
last_one=my_list.pop()
print(last_one)
print("列表长度：",len(my_list))
# 怎么移除指定位置的元素？

# 二维列表

--------------------------------output
1
2
231
0.123
True
0.123
last one
the first one
last one
列表长度： 7
```

## Tuple 
元组与列表类似，但是不可变（immutable）。

```python
# Tuple
my_tuple=(1,2,"231",0.123,True,False,{"a":1,"b":2})
#元组解包
a,b,c,d,e,f,g=my_tuple
print(a)
print(b)
print(c)
print(d)
print(e)
print(f)
print(g)
my_tuple[0]=-1
# 解包机制
--------------------------------output
1
2
231
0.123
True
False
{'a': 1, 'b': 2}
Traceback (most recent call last):
  File "D:\Python\pythonProject1\python.py", line 37, in <module>
    my_tuple[0]=-1
TypeError: 'tuple' object does not support item assignment

```

## Dictionary
字典是键值对的集合，基于哈希表实现，查找速度快。

```Python
#Dictionary
my_dict={"key1":1,"key2":"value2",1:"34"}
#访问元素
value1=my_dict["key1"]
value3=my_dict[1]
print(value1)
print(value3)
#修改元素
my_dict[1]=2
print(my_dict[1])
del my_dict[1]
#遍历字典
for key in my_dict.keys():
    print(key)
for key,value in my_dict.items():
    print(key," ",value)
----------------------------------output
1
34
2
key1
key2
key1   1
key2   value2
```
## Set
集合是无序、不重复元素的集合，支持数学集合运算。Set元素类型支持可被hash化的类型，dict
```python
my_set={1,2.3,"232"}
my_set1={34,23,"23",True,None,False,{"a":1,"b":2}}
#集合操作
my_set.add("new ele")
my_set.remove(0) #不存在报错
my_set.discard(0) #不存在不报错
#集合运算

union=my_set|my_set1
print(union)
intersection=my_set&my_set1
print(intersection)
difference=my_set-my_set1
print(difference)
symmetric_diff=my_set^my_set1
print(symmetric_diff)
-----------------------------------output
{False, 1, 2.3, 34, None, '232', 23, '23', 'new ele'}
{1}
{2.3, '232', 'new ele'}
{False, 'new ele', 34, 2.3, None, 23, '232', '23'}
```

## String
字符串是不可变的字符序列，支持多种操作。
```Python
my_str="hello, World"
my_multi_line_str="""
This is first line
This is second line
"""
# 字符串操作
print(len(my_multi_line_str))
print(my_str[0])
print(my_str+my_multi_line_str)
print(my_str*2)
# 字符串方法
print(my_str.lower())
print(my_str.upper())
print(my_str.strip())
print(my_str.split(','))
print(','.join(my_str.split(',')))
print(my_str.replace(',','-'))

#格式化字符串
name=['Alice',"Mark",1,2]
age=[14,15,16,72]
for i in range(4):
    formatted_str=f"My name is {name[i]}, I am {age[i]} years old"
    print(formatted_str)
# 字符串修改性与字符数组
############################################Output
40
h
hello, World
This is first line
This is second line

hello, Worldhello, World
hello, world
HELLO, WORLD
hello, World
['hello', ' World']
hello, World
hello- World
My name is Alice, I am 14 years old
My name is Mark, I am 15 years old
My name is 1, I am 16 years old
My name is 2, I am 72 years old
```
## 推导式
`{expression for item in iterable if condition}`
`[expression for item in iterable if condition]`
`{expression:expression for item in iterable if condition}`
高级运用

- 多重条件
`numbers = [x for x in range(20) if x % 2 == 0 if x % 3 == 0]`
- 嵌套推导式
```Python
# 展平二维列表
matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
flattened = [num for row in matrix for num in row]
```
- if-else表达式
`numbers = [x if x % 2 == 0 else -x for x in range(10)`

## 集合，列表，字典，元组中可放的元素类型
- list
列表可以包含任意类型的元素，且元素类型可以混合。
可存放的元素类型：
1. 任何Python对象（包括数字、字符串、列表、字典、元组、集合、自定义对象等）
2. 不同类型可以混合存放
- tuple
元组与列表类似，可以包含任意类型的元素，但创建后不可变。
- dict
键(key)的可接受类型：
不可变类型：
1. 数字（int, float）
2. 字符串（str）
3. 布尔值（bool）
4. 元组（但只能包含不可变元素）
5. frozenset
不能作为键的类型：
1. 列表
2. 字典
3. 普通集合
4. 其他可变对象
值(value)的可接受类型:任何Python对象（无限制）
- set
可存放的元素类型：
1. 不可变类型：
2. 数字（int, float）
3. 字符串（str）
4. 元组（但只能包含不可变元素）
5. frozenset
不能存放的元素类型：
1. 列表
2. 字典
3. 普通集合
4. 其他可变对象
## Numpy


## Pandas
### 基本数据结构
- Series

