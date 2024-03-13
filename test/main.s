.text
.globl main

main:
    li $t0, 1             # 将计数器初始化为1

loop:
    move $a0, $t0         # 将计数器的值存入$a0寄存器，作为要输出的数字

    li $v0, 1             # 加载系统调用号为1，表示打印整数
    syscall              
  

    addi $t0, $t0, 1      # 计数器加1


    li $t1, 6             # 将结束条件设置为6
    bne $t0, $t1, loop    # 如果计数器不等于6，跳转到loop标签处继续循环

    li $v0, 10            # 加载系统调用号为10，表示程序退出
    syscall     
