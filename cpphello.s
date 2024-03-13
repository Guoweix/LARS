	.file	"cpphello.c"
	.text
	.align	2
	.globl	main
	.type	main, @function
main:
.LFB0 = .
	.cfi_startproc
	addi.d	$r3,$r3,-32
	.cfi_def_cfa_offset 32
	st.d	$r22,$r3,24
	.cfi_offset 22, -8
	addi.d	$r22,$r3,32
	.cfi_def_cfa 22, 0
	addi.w	$r12,$r0,10			# 0xa
	st.w	$r12,$r22,-28
	addi.w	$r12,$r0,5			# 0x5
	st.w	$r12,$r22,-32
	ld.w	$r13,$r22,-28
	ld.w	$r12,$r22,-32
	add.w	$r12,$r13,$r12
	st.w	$r12,$r22,-20
	st.w	$r0,$r22,-24
	b	.L2
.L3:
	ld.w	$r12,$r22,-20
	addi.w	$r12,$r12,1
	st.w	$r12,$r22,-20
	ld.w	$r12,$r22,-24
	addi.w	$r12,$r12,1
	st.w	$r12,$r22,-24
.L2:
	ld.w	$r12,$r22,-24
	or	$r13,$r12,$r0
	addi.w	$r12,$r0,19			# 0x13
	ble	$r13,$r12,.L3
	or	$r12,$r0,$r0
	or	$r4,$r12,$r0
	ld.d	$r22,$r3,24
	.cfi_restore 22
	addi.d	$r3,$r3,32
	.cfi_def_cfa_register 3
	jr	$r1
	.cfi_endproc
.LFE0:
	.size	main, .-main
	.ident	"GCC: (LoongArch GNU toolchain rc1.2 (20230615)) 8.3.0"
	.section	.note.GNU-stack,"",@progbits
