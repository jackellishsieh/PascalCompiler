# Auto-generated description
# @author Jack Hsieh
# @version 2022/05/29
	
.data
	vartimes: .word 0
	varlow: .word 0
	varprintSquares: .word 0
	varhigh: .word 0
	varcount: .word 0
	varsquare: .word 0
	varignore: .word 0
	newline: .asciiz "\n"
	
.text
	
.globl main
	
main:
	# loads integer literal
	li $v0 196
	
	# load $v0 into global count
	sw $v0 varcount
	
	# loads integer literal
	li $v0 0
	
	# load $v0 into global times
	sw $v0 vartimes
	
	# push $ra onto the stack
	subu $sp $sp 4
	sw $ra ($sp)
	
	# loads integer literal
	li $v0 10
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# loads integer literal
	li $v0 13
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# jump to procedure printSquares
	jal procprintSquares
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	# pop $ra from the stack
	lw $ra ($sp)
	addu $sp $sp 4
	
	# load $v0 into global ignore
	sw $v0 varignore
	
	# load global count
	la $t0 varcount
	lw $v0 ($t0)
	
	# print $v0
	move $a0 $v0
	li $v0 1
	syscall
	# print newline
	la $a0 newline
	li $v0 4
	syscall
	
	# load global times
	la $t0 vartimes
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
	
procprintSquares:
	# set default value for return variable varprintSquares
	li $v0 0
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# set default value for non-parameter local varcount
	li $v0 0
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# set default value for non-parameter local varsquare
	li $v0 0
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# load local low
	lw $v0 16($sp)
	
	# load $v0 into local count
	sw $v0 4($sp)
	
	
startWhile1:
	# load local count
	lw $v0 4($sp)
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# load local high
	lw $v0 16($sp)
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	bgt $t0 $v0 endWhile1
	
	# compute * operator
	# load local count
	lw $v0 4($sp)
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# load local count
	lw $v0 8($sp)
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	mult $t0 $v0
	mflo $v0
	
	# load $v0 into local square
	sw $v0 0($sp)
	
	# load local square
	lw $v0 0($sp)
	
	# print $v0
	move $a0 $v0
	li $v0 1
	syscall
	# print newline
	la $a0 newline
	li $v0 4
	syscall
	
	# compute + operator
	# load local count
	lw $v0 4($sp)
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# loads integer literal
	li $v0 1
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	addu $v0 $t0 $v0
	
	# load $v0 into local count
	sw $v0 4($sp)
	
	# compute + operator
	# load global times
	la $t0 vartimes
	lw $v0 ($t0)
	
	# push $v0 onto the stack
	subu $sp $sp 4
	sw $v0 ($sp)
	
	# loads integer literal
	li $v0 1
	
	# pop $t0 from the stack
	lw $t0 ($sp)
	addu $sp $sp 4
	
	addu $v0 $t0 $v0
	
	# load $v0 into global times
	sw $v0 vartimes
	
	j startWhile1
	
endWhile1:
	# pop non-parameter local varcount
	# push $t0 onto the stack
	subu $sp $sp 4
	sw $t0 ($sp)
	
	# pop non-parameter local varsquare
	# push $t0 onto the stack
	subu $sp $sp 4
	sw $t0 ($sp)
	
	# pop varprintSquares
	# pop $v0 from the stack
	lw $v0 ($sp)
	addu $sp $sp 4
	
	# return
	jr $ra
	
