# Auto-generated description
# @author Jack Hsieh
# @version 2022/05/29
	
.data
	varignore: .word 0
	varprint: .word 0
	varn: .word 0
	newline: .asciiz "\n"
	
.text
	
.globl main
	
main:
	# loads integer literal
	li $v0 3
	
	# load $v0 into global n
	sw $v0 varn
	
	# push $ra onto the stack
	subu $sp $sp 4
	sw $ra ($sp)
	
	# loads integer literal
	li $v0 5
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# jump to procedure print
	jal procprint
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	# pop $ra from the stack
	lw $ra ($sp)
	addu $sp $sp 4
	
	# load $v0 into global ignore
	sw $v0 varignore
	
	# load global n
	la $t0 varn
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
	
procprint:
	# set default value for return variable varprint
	li $v0 0
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# load local n
	lw $v0 4($sp)
	
	# print $v0
	move $a0 $v0
	li $v0 1
	syscall
	# print newline
	la $a0 newline
	li $v0 4
	syscall
	
	# pop varprint
	# pop $v0 from the stack
	lw $v0 ($sp)
	addu $sp $sp 4
	
	# return
	jr $ra
	
