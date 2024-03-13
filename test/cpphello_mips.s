
main:


	addiu	$sp,$sp,-40
	sw	$31,36($sp)
	sw	$fp,32($sp)
	move	$fp,$sp

	addu	$2,$3,$2
	sw	$2,28($fp)

	jal	func
	nop



	.globl	func

func:

	addiu	$sp,$sp,-16
	sw	$fp,12($sp)
	move	$fp,$sp

	addiu	$2,$2,1
	sw	$2,4($fp)
	nop
	move	$sp,$fp
	lw	$fp,12($sp)
	addiu	$sp,$sp,16
	jr	$31
	nop

