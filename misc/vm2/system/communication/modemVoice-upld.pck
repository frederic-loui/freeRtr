pack|DA         �.'�.'encoded.code                                                                                                                                                                                                                                                   �ÿP��ρ�@������T��PW���!r���a�଀�!r���a����"�t��oiaaaapkfoibooadaal
paaabimmiionailohiomaioniljhmacpdkegigdabmdleaomnbamdoianaahdplmdleabcoi
lbghkacmnbemdboclmaioniilcogmaeliaaaccoilbghkacmnbeiaoeiahebbilaggmaeclm
fiboibcaahiofclmapjolabpibpmdabaaaaplimmiibmaaaabijaggbabclmaikaghkacija
ghkaclahcoikcpplahioijnppladkoijippolanlaaioijbppladpoiimppoiiopplabfoii
ppplacooihpppoijbpphcpbiapiaeapieikaaiapiabheacolngaobpaoahljidaalphmacf
bfhoihcppfpfjapicmoppkkefocpblohmaccoknpgnedkmeapiflappiknaljiaaaclnlkma
cniocplkmdkmdapifjnppdkbggaabhebjoidpppapidjapplaagoicmpplaaioibmpplahoo
ibhppojjapplaaioiappplacboiakpplohmacknioaggbabclppljiaaapdkeibaggbabaia
apoaggaablaagoiphpoojgfpplaagoioppolaaioinppolagpoinkpolagloinfpolaanoin
apolaakoimlpoimmiibmaaaabiboibaaaljaaabioniiomaionailobfafbclmaclmjclncc
lnlclpgclppclonml
packMg���         �% �% decoder.asm                                                                                                                                                                                                                                                    org 1234h                       ;anything could be here...;)
use16                           ;this is for real mode...;)

mov al,0c3h                     ;the stop character: opcode of ret..
mov di,9f50h                    ;the target address...
mov cx,di                       ;number of bytes to find...
xor di,9e40h                    ;get 110h for searching...
repne                           ;find the end of code...
  scasb
mov si,di                       ;setup source offset...
mov di,cx                       ;setup target offset...
mov cl,54h                      ;get 04h into cl...
sub cl,50h
push di                         ;save for target execution...
j1:
lodsb                           ;get one character...
cmp al,21h                      ;space or similar?
jb byte j1                      ;yes-->get another...
sub al,61h                      ;get the nibble...
mov ah,al                       ;store it...
j2:
lodsb                           ;get second character...
cmp al,21h                      ;space or similar?
jb byte j2                      ;yes-->get another...
sub al,61h                      ;get the nibble...
shl al,cl                       ;rotate to upper half...
shr ax,cl                       ;get the oroginal character...
stosb                           ;save it...
and ah,ah                       ;was there error?
jz byte j1                      ;no-->continue...
ret                             ;execute the original code...
pack24��         �% �-2encoder.pas                                                                                                                                                                                                                                                    {$sysinc system.inc}
{$sysinc filesys.inc}
{$sysinc textfile.inc}
Const lineSize=72;
Var
  buf:array[1..1024] of byte;
  siz:longint;
  out:String;

Procedure read(a:string);
Var f:xFile;
Begin;
siz:=0;
if (xOpen(f,a,xGenFilMod_r)<>0) then exit;
siz:=xFileSize(f);
xBlockRead(f,buf,siz);
xClose(f);
End;

Procedure appendByte(i:LongInt);
Begin;
out:=out+chr((i shr 4)+$61)+chr((i and $f)+$61);
End;


Var
  i:LongInt;
  t:xtText;
  a:String;
BEGIN;
read('decoder.code');
move(buf,out[1],siz);
out[0]:=chr(siz);
read('recvxmod.code');
a:='encoded.code';
xErase(a);
xCreate(a);
if (xtOpen(t,a,false)<>0) then exit;
for i:=1 to siz do begin;
  appendByte(buf[i]);
  if (length(out)<lineSize) then continue;
  xtWriteLn(t,copy(out,1,lineSize));
  out:=copy(out,lineSize+1,666);
  end;
xtWriteLn(t,out);
xtClose(t);
WriteLn('encoded successfully!');
END.packT���         �% �% recvxmod.asm                                                                                                                                                                                                                                                   firstbyte:
call init_j1
init_j1:
cli
pop si
sub si,3
mov di,offset firstbyte
mov ax,cs
mov ss,ax
mov sp,di
mov es,ax
mov ds,ax
mov cx,offset lastbyte
rep
  movsb
push word offset init_j2
ret


proc displayChar
;in: al-char to display...
mov ah,0eh
int 10h
ret
endp

proc flushRx
call getChar
jnc byte flushRx
ret
endp

proc putChar
;in: al-char to send...
mov ah,1                        ;send one character...
mov dx,cs:[portNum]
int 14h
ret
endp

proc getChar
;out: carry-cleared if succeeded...
;     al-character received...
push ds
sub ax,ax
mov ds,ax
mov bp,def:[46ch]
getChar_j1:
mov ax,200h                     ;receive one character...
mov dx,cs:[portNum]
int 14h
and ah,80h
jz byte getChar_ok
mov ax,def:[46ch]
sub ax,bp
sub ax,18
js byte getChar_j1
sub ax,ax
stc
jmp byte getChar_vege
getChar_ok:
clc
getChar_vege:
pop ds
ret
endp


segDiff equ 100h
char_ack equ 06h
char_nak equ 15h
char_eot equ 04h
char_soh equ 01h
nextSeq db 1
nextWrt dw 0



init_j2:
sti

mov ax,cs
add ax,segDiff
mov def:[nextWrt],ax
sub ax,ax
mov al,def:[portNum]
mov def:[portNum],ax

;write greeting message...
mov al,'r'
call displayChar
mov al,'x'
call displayChar
mov al,':'
call displayChar

jmp byte recv_nak

recv_flsh:
mov al,8
call displayChar
mov al,'?'
call displayChar
call flushRx
recv_nak:
mov al,char_nak
call putChar
recv_nxt:
mov al,'.'                      ;display message...;)
call displayChar
call getChar
jc byte recv_nak                ;timeout-->nak again...
cmp al,char_eot
je word recv_don
cmp al,char_soh
je byte recv_hdr
jmp byte recv_flsh

recv_hdr:                       ;receive header...
push cs
pop ds
push cs
pop es
mov cx,131
mov di,offset lastbyte
recv_hdr1:
push cx
push di
call getChar
pop di
pop cx
jc word recv_nak                ;timeout-->nak...
stosb
inc bp
loop recv_hdr1
mov si,offset lastbyte
lodsw cs                        ;read sequence number...
not ah
cmp al,ah
jne word recv_flsh              ;bad-->flush...
mov dl,al
mov cx,128
sub bx,bx
recv_hdr2:                      ;calculate checksum...
lodsb
add bl,al
loop recv_hdr2
lodsb                           ;compare chksum...
cmp al,bl
jne word recv_flsh              ;bad-->flush...
cmp dl,def:[nextSeq]
je byte recv_god
call getChar
jnc word recv_flsh              ;more chars-->flush...
mov al,char_ack
call putChar
mov al,8
call displayChar
mov al,'~'
call displayChar
jmp word recv_nxt

recv_god:                       ;good packet...
mov al,8
call displayChar
mov al,'!'
call displayChar
mov si,offset lastbyte
lodsw
mov es,def:[nextWrt]
sub di,di
mov cx,128
rep
  movsb
add word deF:[nextWrt],8
inc byte def:[nextSeq]
mov al,char_ack
call putChar
jmp word recv_nxt

recv_don:
mov al,char_ack
call putChar
;display message...
mov al,8
call displayChar
mov al,'o'
call displayChar
mov al,'k'
call displayChar
mov al,13
call displayChar
mov al,10
call displayChar

;start code...
mov ax,cs
add ax,segDiff
sub ax,10h
mov cx,100h
mov ds,ax
mov es,ax
mov ss,ax
mov sp,cx
push ax
push cx
sub ax,ax
sub cx,cx
sub dx,dx
sub bx,bx
sub si,si
sub di,di
sub bp,bp
retf

portNum dw ?
lastbyte:
pack�N��         �+�/uploader.mod                                                                                                                                                                                                                                                   ;configuration...
debugMode 2                                      ;debug level...
setVariable15 "2"                                ;remote port number...
setVariable16 "9600"                             ;port speed...


echo "uploader v1.0, done by Mc at 2003-05-22 00:46:33."
echo ""

calcVariable14 \var15\ + 96
scriptCommand "setVariable14 \\\var14\\\"
if \readEd\ = "" then goto paramerr
setVariable13 \readEd\

echo "please type the following lines on remote computer:"
echo "mode com\var15\:\var16\,n,8,1"
echo "ctty com\var15\"

setPortLine \var16\
setPortFlow 0
setPortModem 3

setVariable9 ""
gosub doCmd

echo "erasing code..."

setVariable12 "!uploded.com"
setVariable9 "del \var12\"
gosub doCmd

setVariable10 0
openFileR encoded.code
if \result\ <> 0 then goto fileerr
send1:
readLnFile
if \result\ <> 0 then goto send2
calcVariable10 \var10\ + 1
echo "uploading line #\var10\..."
setVariable9 "echo \readed\>>\var12\"
gosub doCmd
goto send1
send2:

echo "starting code..."
setVariable9 "echo a\var14\x>>\var12\"
gosub doCmd
setVariable9 ".\\\var12\"
gosub doSend

echo "uploading image..."
execPR \\utils\\terminal\\proto-xmodem.code tc\var13\
echo "upload finished..."
quit



doCmd:
gosub doSend
gosub doWait
return

doSend:
if \var9\ = "" then goto doSend1
stringGetPart \var9\ 1 1
binaryWrite \readed\
binaryReadLn 1 1 ">" 1
stringGetPart \var9\ 2 666
setVariable9 \readed\
goto doSend
doSend1:
binaryWrite \13\
return

doWait:
binaryReadLn 5 666 ">" 1
if ":\\" @ \readed\ then goto doWait1
goto doWait
doWait1:
return




paramerr:
echo "using: uploader.mod <code-to-upload>"
quit

fileerr:
echo "error opening file!"
quit
pack�|��          �%5�.	encoded.make                                                                                                                                                                                                                                                   noerr del encoded.code
compASMn decoder
compASMn recvxmod
compPAS encoder
exec encoder.code
del decoder.code
del recvxmod.code
del encoder.code
