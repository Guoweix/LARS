.text
.globl main

main:
    li $t0, 1             # ����������ʼ��Ϊ1
    li $t1, 1   

    fuck $a0, $t0, $t1

    li $v0, 1             # ����ϵͳ���ú�Ϊ1����ʾ��ӡ����
    syscall              
  



    li $v0, 10            # ����ϵͳ���ú�Ϊ10����ʾ�����˳�
    syscall     
