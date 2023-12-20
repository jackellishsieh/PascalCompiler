# Auto-generated description
# @author Jack Hsieh
# @version 2022/05/29
	
.data
	varfoo: .word 0
	varignore: .word 0
	varbar: .word 0
	vard: .word 0
	varf: .word 0
	newline: .asciiz "\n"
	
.text
	
.globl main
	
main:
	# loads integer literal
	li $v0 2
	
	# load $v0 into global f
	sw $v0 varf
	
	# push $ra onto the stack
	subu $sp $sp 4
	sw $ra ($sp)
	
	# loads integer literal
	li $v0 3
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# jump to procedure foo
	jal procfoo
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	# pop $ra from the stack
	lw $ra ($sp)
	addu $sp $sp 4
	
	# load $v0 into global ignore
	sw $v0 varignore
	
	# load global f
	la $t0 varf
	lw $v0 ($t0)
	
	# print $v0
	move $a0 $v0
	li $v0 1
	syscall
	# print newline
	la $a0 newline
	li $v0 4
	syscall
	
	# terminate execution
	li $v0 10
	syscall
	
procbar:
	# set default value for return variable varbar
	li $v0 0
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# load local f
	lw $v0 4($sp)
	
	# print $v0
	move $a0 $v0
	li $v0 1
	syscall
	# print newline
	la $a0 newline
	li $v0 4
	syscall
	
	# pop varbar
	# pop $v0 from the stack
	lw $v0 ($sp)
	addu $sp $sp 4
	
	# return
	jr $ra
	
procfoo:
	# set default value for return variable varfoo
	li $v0 0
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# push $ra onto the stack
	subu $sp $sp 4
	sw $ra ($sp)
	
	# compute + operator
	# load local d
	lw $v0 8($sp)
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# load global f
	la $t0 varf
	lw $v0 ($t0)
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	addu $v0 $t0 $v0
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# jump to procedure bar
	jal procbar
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	# pop $ra from the stack
	lw $ra ($sp)
	addu $sp $sp 4
	
	# load $v0 into global ignore
	sw $v0 varignore
	
	# pop varfoo
	# pop $v0 from the stack
	lw $v0 ($sp)
	addu $sp $sp 4
	
	# return
	jr $ra
	
