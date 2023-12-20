# Auto-generated description
# @author Jack Hsieh
# @version 2022/05/29
	
.data
	varn: .word 0
	varprintSquare: .word 0
	varx: .word 0
	varignore: .word 0
	newline: .asciiz "\n"
	
.text
	
.globl main
	
main:
	# loads integer literal
	li $v0 1
	
	# load $v0 into global x
	sw $v0 varx
	
	# push $ra onto the stack
	subu $sp $sp 4
	sw $ra ($sp)
	
	# compute + operator
	# load global x
	la $t0 varx
	lw $v0 ($t0)
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# loads integer literal
	li $v0 2
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	addu $v0 $t0 $v0
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# jump to procedure printSquare
	jal procprintSquare
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	# pop $ra from the stack
	lw $ra ($sp)
	addu $sp $sp 4
	
	# load $v0 into global ignore
	sw $v0 varignore
	
	# terminate execution
	li $v0 10
	syscall
	
procprintSquare:
	# set default value for return variable varprintSquare
	li $v0 0
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# compute * operator
	# load local n
	lw $v0 4($sp)
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# load local n
	lw $v0 8($sp)
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	mult $t0 $v0
	mflo $v0
	
	# print $v0
	move $a0 $v0
	li $v0 1
	syscall
	# print newline
	la $a0 newline
	li $v0 4
	syscall
	
	# pop varprintSquare
	# pop $v0 from the stack
	lw $v0 ($sp)
	addu $sp $sp 4
	
	# return
	jr $ra
	
