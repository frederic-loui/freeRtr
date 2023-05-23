org 80008000h
firstbyte:
xor r0,r0,r0
lui t2,offset kernel_id2
ori t2,t2,offset kernel_id2
jal putOutAsciiZ
noop

jal instExcHndlr                ;setup exceptions...
noop
jal tlb_clear                   ;clear tlb table...
noop
jal memory_getFree              ;get free bytes...
noop
jal memory_copyRamDrv           ;copy ramdrive image...
noop
jal memory_initAll              ;init memory...
noop
jal getCPUinfo                  ;get cpu info...
noop
jal instTimerInt                ;install timer...
noop
jal process_create              ;init process...
noop
jal process_updateIniter        ;patch this process...
noop
jal process_begRun              ;start this process...
noop
jal process_create              ;init process...
noop
jal process_updateRamdrv        ;patch this process...
noop
jal process_begRun              ;start this process...
noop
jal excInstNormHnd              ;install normal handler...
noop
jal setupIRQenaMap              ;setup interrupt map...
noop
j process_startNext             ;start one process...
noop
;-------------------------------


;------------------------------- include files...
include datablk.inc             ;data structures...
include utils.inc               ;useful stuff...
include memory.inc              ;memory stuff...
include excint.inc              ;exception handlers...
include process.inc             ;process stuff...
include pipeline.inc            ;pipeline stuff...
include fileio.inc              ;fileio stuff...
include syscall0.inc            ;syscall servicer...
include syscall1.inc            ;syscall servicer...
include syscall2.inc            ;syscall servicer...
include syscall3.inc            ;syscall servicer...
include syscall4.inc            ;syscall servicer...
include crc32.inc               ;crc32 calculator...
;-------------------------------
kernelChecksum dd ?
lastbyte:
