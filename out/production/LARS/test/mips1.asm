.text
.globl main

main:
    li $t0, 1             # 将计数器初始化为1
    li $t1, 1   

    fuck $a0, $t0, $t1

    li $v0, 1             # 加载系统调用号为1，表示打印整数
    syscall              
  



    li $v0, 10            # 加载系统调用号为10，表示程序退出
    syscall     
