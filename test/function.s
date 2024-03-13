.text
.globl function
function:


    li $t0, 1

loop:
    # 输出计数器的值
    move $a0, $t0
    li $v0, 1
    syscall

    # 增加计数器
    addi $t0, $t0, 1

    # 检查是否达到5
    li $t1, 5
    beq $t0, $t1, exit

    # 跳转到循环开始
    j loop

exit:
    # 退出程序
    li $v0, 10
    syscall

    # 返回到调用者
    jr $ra