org 0h
db 'exec'                       ;id
dd offset lastbyte              ;size
dd 1024                         ;data
dd 1024                         ;stack
;-------------------------------

lui t0,7fffh
lui t1,7fffh
add t0,t0,t1

lastbyte:
