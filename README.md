
## 1. 克隆项目的dev分支  

复制仓库地址
 
![](imgs/clone.png)

```
git clone -b dev git@github.com:SimpleFate/newenergy.git
```
![](imgs/gitssh2.png)

## 2. 使用idea导入  
![](imgs/import0.png)
![](imgs/import1.png)
![](imgs/import2.png)
![](imgs/import3.png)
![](imgs/import4.png)
![](imgs/import5.png)

## 3. 添加新功能
- 在本地新建一个`feat`分支 

![](imgs/p0.png)  

![](imgs/p1.png)

![](imgs/p2.png)

- 本地开发完成并测试成功后，`commit`代码到本地的`feat`分支 

![](imgs/p3.png)

![](imgs/p4.png)

- 切换到`dev`分支，`pull`拉取远程仓库的`dev`分支

![](imgs/p5.png)

![](imgs/p6.png)

![](imgs/p7.png)


- 将本地开发的`feat`分支合并到当前`dev`分支

    ![](imgs/p8.png)

    a. 如果没有冲突，将更新后的本地的`dev`分支`push`到远程仓库的`dev`分支

    ![](imgs/p9.png)

    ![](imgs/p10.png)

    b. 如果有冲突，根据提示修改冲突，修改完成后`commit`并`push`到远程仓库的`dev`分支
    
    例如：如果`dev`分支如图  

    ![](imgs/conflict0.png)

    `feat`分支如图  

    ![](imgs/conflict1.png)

    合并时会提示  

    ![](imgs/conflict2.png)

    选择`merge`之后  

    ![](imgs/conflict3.png)

    根据冲突进行修改  

    ![](imgs/conflict4.png)

    之后`commit`并`push`  

    ![](imgs/conflict5.png)






- 删除本地的`feat`分支
    
    ![](imgs/p11-1.png)

    或

    ![](imgs/p11-2.png)
    






