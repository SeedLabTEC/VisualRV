addi x5, x6, 40
SW x5, 40(x6)
loop: sw x5, 40(x6)
label: lh x5, 40(x6)
label:sh x5, 40(x6)
label:lb x5, x6, 99
label:lbu x5, x31, 56
label:sb x5, 40(x6)
label:and x5, x6, x7
abel:   or x5, x6, x8
label:xor x5, x6, x9
label:addi x5, x6, 29
label:ori x5, x6, 20
label:xori x5, x6, 20
abel:sll x5, x6, x7
;label:srl x5, x6, x7
;label:sra x5, x6, x7
label:
label:srli x5, x6, 3
srai x5, x6, 3
;label:beq x5, x6, 200
label:bne x5, x6, 100
label:blt x5, x6, 100
label:bge x5, x6, 100
abel:bltu x5, x6, 100
label:bgeu x5, x6, 100
label:jal x1, 100
addi x3, x6, 29
beq x1, x2, loope
last:jalr x30, 100(x1)
label:bne x5, x6, 100
label:blt x5, x6, 100
label:bge x5, x6, 100
abel:bltu x5, x6, 100
label:bgeu x5, x6, 100

