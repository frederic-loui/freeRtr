use16
org 100h
;-------------------------------

mov ax,11h
int 10h

sub eax,eax
clts
dw 00h
;-------------------------------
