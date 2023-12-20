# Auto-generated description
# @author Jack Hsieh
# @version 2022/05/29
	
.data
	varcount: .word 0
	varcountUp: .word 0
	varx: .word 0
	varmax: .word 0
	newline: .asciiz "\n"
	
.text
	
.globl main
	
main:
	# push $ra onto the stack
	subu $sp $sp 4
	sw $ra ($sp)
	
	# loads integer literal
	li $v0 2
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# loads integer literal
	li $v0 4
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# jump to procedure countUp
	jal proccountUp
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	# pop $ra from the stack
	lw $ra ($sp)
	addu $sp $sp 4
	
	# load $v0 into global x
	sw $v0 varx
	
	# terminate execution
	li $v0 10
	syscall
	
proccountUp:
	# set default value for return variable varcountUp
	li $v0 0
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# load local count
	lw $v0 8($sp)
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# load local max
	lw $v0 8($sp)
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	bgt $t0 $v0 endIf1
	
	# load local count
	lw $v0 8($sp)
	
	# print $v0
	move $a0 $v0
	li $v0 1
	syscall
	# print newline
	la $a0 newline
	li $v0 4
	syscall
	
	# push $ra onto the stack
	subu $sp $sp 4
	sw $ra ($sp)
	
	# compute + operator
	# load local count
	lw $v0 12($sp)
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# loads integer literal
	li $v0 1
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	addu $v0 $t0 $v0
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# load local max
	lw $v0 12($sp)
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# jump to procedure countUp
	jal proccountUp
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	# pop $ra from the stack
	lw $ra ($sp)
	addu $sp $sp 4
	
	# load $v0 into local countUp
	sw $v0 0($sp)
	
	
endIf1:
	# pop varcountUp
	# pop $v0 from the stack
	lw $v0 ($sp)
	addu $sp $sp 4
	
	# return
	jr $ra
	
